from shapely.geometry import MultiPoint
from geopy.distance import great_circle
import math
from collections import OrderedDict
########################## UTILITY ##########################
def getColumn(data, idx):
	column = []
	for d in data:
		column.append(d[idx])

	return column

def _timeDifference(p1, p2):
	diff = (p1 - p2)
	# return abs(diff.total_seconds())/60
	return abs(diff.total_seconds())

def timeDifference(dateTime1, dateTime2):

	return _timeDifference(dateTime1, dateTime2)

def distance(lat1,lon1,lat2,lon2):
  	miles = great_circle((lat1, lon1), (lat2, lon2)).miles
  	return (miles/1.60934)*1000

def getCenterMostPoint(cluster):
	if len(cluster) >= 1:
	    x, y = getCenteroid(cluster)
	    centermostPoint = min(cluster, key=lambda point: great_circle(point, centroid).m)
	    return tuple(centermostPoint)

def getCenteroid(cluster):
	if len(cluster) >= 1:
	    x = MultiPoint(cluster).centroid.x
	    y = MultiPoint(cluster).centroid.y
	    return (x, y)

def getHomeCluster(fullTrace):
	idTwo = '02:00'
	idSix = '06:00'
	idTwoThree = '23:00'
	d = {
		idTwo: {},
		idSix: {},
		idTwoThree: {}
	}

	twoDate = None
	sixDate = None
	twoThreeDate = None
	for t in fullTrace:
		if t[1].isoweekday() in range(1, 5):
			s = t[1]
			e = t[2]

			twoDate = e.replace(hour=2, minute=0)
			sixDate = e.replace(hour=6, minute=0)
			twoThreeDate = s.replace(hour=23, minute=0)
			# print "-------"
			# print s
			# print e
			# print twoDate
			# print sixDate
			# print twoThreeDate

			if twoDate <= e and twoDate >= s:
				if t[0] in d[idTwo]:
					d[idTwo][t[0]] = d[idTwo][t[0]]+1
				else:
					d[idTwo][t[0]] = 1

			if sixDate <= e and sixDate >= s:
				if t[0] in d[idSix]:
					d[idSix][t[0]] = d[idSix][t[0]]+1
				else:
					d[idSix][t[0]] = 1

			if twoThreeDate <= e and twoThreeDate >= s:
				if t[0] in d[idTwoThree]:
					d[idTwoThree][t[0]] = d[idTwoThree][t[0]]+1
				else:
					d[idTwoThree][t[0]] = 1
	
	twoFreq = OrderedDict(sorted(d[idTwo].items(), key=lambda x: x[1], reverse=True))
	sixFreq = OrderedDict(sorted(d[idSix].items(), key=lambda x: x[1], reverse=True))
	ttFreq = OrderedDict(sorted(d[idTwoThree].items(), key=lambda x: x[1], reverse=True))

	freq = {}

	currMax = None
	for i in twoFreq:
		if currMax == None:
			currMax = twoFreq[i]

		if twoFreq[i] >= currMax:
			if i in freq:
				freq[i] = freq[i] + 1
			else:
				freq[i] = 1

	currMax = None
	for i in sixFreq:
		if currMax == None:
			currMax = sixFreq[i]

		if sixFreq[i] >= currMax:
			if i in freq:
				freq[i] = freq[i] + 1
			else:
				freq[i] = 1

	currMax = None
	for i in ttFreq:
		if currMax == None:
			currMax = ttFreq[i]

		if ttFreq[i] >= currMax:
			if i in freq:
				freq[i] = freq[i] + 1
			else:
				freq[i] = 1

	currMax = 0
	currMaxCluster = None
	for f in freq:
		if freq[f] > currMax:
			currMax = freq[f]
			currMaxCluster = f

	for t in fullTrace:
		if currMaxCluster == t[0]:
			return t[3]


########################## METRICS ##########################

def metricTotalDistance(trace):
	sum = 0;
	for i in range(0, len(trace)-1):

		j = i + 1
		pI = trace[i][3]
		pJ = trace[j][3]
		sum = sum + distance(pI[0], pI[1], pJ[0], pJ[1])

	return sum

def metricMaxDistanceBetweenPoints(trace):

	if len(trace) > 0:
		max = 0
		for i in range(0, len(trace)):
			for j in range(0, len(trace)):
				pI = trace[i][3]
				pJ = trace[j][3]
				d = distance(pI[0], pI[1], pJ[0], pJ[1])
				if d > max:
					max = d
		return max
	else:
		return 0

def metricRadiusOfGyration(trace):
	
	if len(trace) > 0:
		times = {}

		for point in trace:
			if point[0] in times:
				times[point[0]] = times[point[0]] + timeDifference(point[1], point[2])
			else:
				times[point[0]] = timeDifference(point[1], point[2])

		T = 0

		for t in times:
			T = T + times[t]


		latsLons = getColumn(trace, 3)
		lats = getColumn(latsLons, 0)
		lons = getColumn(latsLons, 1)
		centroid = getCenteroid(list(zip(lats, lons)))

		sum = 0;
		for i in range(0, len(trace)):
			point = trace[i]
			sum = sum + (times[point[0]] * math.pow(distance(point[3][0], point[3][1], centroid[0], centroid[1]), 2))

		sum = sum * (1/T)	

		return math.sqrt(sum)
	else:
		return 0

def metricStdDeviationDisplacement(trace):
	if len(trace) > 1:
		N = len(trace)
		D = 0
		for i in range(0, N-1):
			j = i + 1
			pI = trace[i][3]
			pJ = trace[j][3]
			D = D + distance(pI[0], pI[1], pJ[0], pJ[1])

		D = D/(N - 1)

		sum = 0
		for i in range(0, N-1):
			j = i + 1
			pI = trace[i][3]
			pJ = trace[j][3]
			d = distance(pI[0], pI[1], pJ[0], pJ[1])

			sum = sum + math.pow((d - D), 2)

		sum = sum/(N-1)

		return math.sqrt(sum)
	else:
		return 0

def metricMaximumDistanceFromHome(fullTrace, trace):
	home = getHomeCluster(fullTrace)
	if home == None:
		return 0

	N = len(trace)

	maxDist = 0

	for i in range(0, N):
		dist = distance(trace[i][3][0], trace[i][3][1], home[0], home[1])
		if dist > maxDist:
			maxDist = dist

	return maxDist

def metricNumberOfVisitedPlaces(trace):
	d = {}

	for t in trace:
		d[t[0]] = 1

	return len(d)

def metricRelevantPlacesNb(fullTrace, trace):
	revPlaces = metricRelevantPlaces(fullTrace)
	done = []
	nb = 0

	for t in trace:
		if t[0] in revPlaces and not(t[0] in done):
			nb = nb + 1
			done.append(t[0])

	return nb
def metricRelevantPlaces(fullTrace):
	d = {}

	for t in fullTrace:
		if t[0] in d:
			d[t[0]] = d[t[0]] + 1
		else:
			d[t[0]] = 1

	d = OrderedDict(sorted(d.items(), key=lambda x: x[1], reverse=True))

	relevantPlaces = []

	i = 0
	for j in d:
		if i < 10:
			relevantPlaces.append(j)

		i = i + 1

	return relevantPlaces