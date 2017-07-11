import DataExtractor as extractor
import sys
from datetime import datetime
import DBScan as DBScan
import RelevantPlacesMontoliu as mon
import os
import gmplot
from staticmap import StaticMap, Line, CircleMarker
import googlemaps
import math
from geopy.distance import great_circle
from shapely.geometry import MultiPoint
import userIdToId as u

gmaps = googlemaps.Client(key='AIzaSyCr6IWd9hNb5_MaX3ukMgfALvRSXESP6wI')
## DB
db = None
subject = None

## Relevant places
algorithm = None

## Intervals
interval = None
date = None

## plot
script = os.path.realpath(__file__)
output = script[0: script.rfind("/")]
overlap = False


def getCmdArguments():
	global db
	global subject
	global algorithm
	global date
	global interval
	global output
	global overlap

	if len(sys.argv) < 4:
		print "Usage: python MovementTraces.py <DBScan|Montoliu> <Memotion|StudentLife> <subjectName|all> -p <output,overlap> -i <dates> "
		print "    dates: <date|dateStart,dateEnd>"
		print "    overlap: <0|1>"

	algorithm = sys.argv[1]
	db = sys.argv[2]
	subject = sys.argv[3]

	if len(sys.argv) >= 5:
		opt = sys.argv[4]

		if opt == "-p":
			plotInfo = sys.argv[5].split(",")
			output = plotInfo[0]
			if plotInfo[1] == "false":
				overlap = False	
			else:
				overlap = True

			if len(sys.argv) >= 7:
				d = sys.argv[7]
				if d.find(",") >= 0:
					interval = d.split(",")
				else:
					date = d
		else:
			d = sys.argv[5]
			if d.find(",") >= 0:
				interval = d.split(",")
			else:
				date = d

def getDate(date, begin):
	if begin:
		time = '00:00:01'
	else:
		time = '23:59:59'

	return date + " " + time

def getUserData(user, cols):
	if interval == None and date == None:
		return extractor.getUserLocationData(user, cols)
	
	if interval != None:
		return extractor.getUserLocationDataInterval(user, getDate(interval[0], True), getDate(interval[1], False), cols)

	if date != None:
		return extractor.getUserLocationDataInterval(user, getDate(date, True), getDate(date, False), cols)

def getData(cols):
	data = {}
	if subject != "all":
		data[subject] = getUserData(subject, cols)
	else:
		users = extractor.getusers()
		for user in users:
			data[user] = getUserData(user, cols)

	return data

def getColumn(data, idx):
	column = []
	for d in data:
		column.append(d[idx])

	return column

########################## DBSCAN ##########################

def processDBScanClusters():
	data = getData(['lat', 'lon'])
	for d in data:
		clusters = DBScan.getClusters(data[d])
		lats = getColumn(clusters, 0)
		lons = getColumn(clusters, 1)
		
		if overlap:
			oLats = getColumn(data[d], 0)
			oLons = getColumn(data[d], 1)
			plotBoth(lats, lons, oLats, oLons, "dbScan_overlap_" + d, 10)
		else:
			plotClusters(lats, lons, "dbScan_" + d, 10, False)

def getDBScanTrace(data):
	for d in data:
		trace = DBScan.getTrace(data[d])
		print "Total distance: " + str(metricTotalDistance(trace))
		print "Max distance: " + str(metricMaxDistanceBetweenPoints(trace))
		print "Radius of Gyration: " + str(metricRadiusOfGyration(trace))
		print "Standard deviation of dispacements: " + str(metricStdDeviationDisplacement(trace))
		# plotTrace(trace, data[d], "dbScan_" + d, False)

def divideInDays(data):
	days = {}

	for d in data:
		date = d[0].date()
		if date in days:
			d[date].append(d)
		else:
			d[date] = [d]

	return days

def processDBScanTraceFullPeriodDaily():
	data = getData(['ts', 'lat', 'lon'])

	daysTraces = {}
	daysMetrics = {}
	for d in data:
		days = divideInDays(data)
		for day in days:
			trace = DBScan.getTrace(days[day])
			daysTraces[day] = trace
			daysMetrics[day]['total_distance'] = metricTotalDistance(trace)
			daysMetrics[day]['max_distance'] = metricMaxDistance(trace)
			daysMetrics[day]['radius_gyration'] = metricRadiusOfGyration(trace)
			daysMetrics[day]['std_deviation_displacement'] = metricStdDeviationDisplacement(trace)
			
########################## UTILITY ##########################
def _timeDifference(p1, p2):
	diff = (p1 - p2)
	return abs(diff.total_seconds())/60

#CHECK ME
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
	max = 0
	for i in range(0, len(trace)-1):
		j = i + 1
		pI = trace[i][3]
		pJ = trace[j][3]
		d = distance(pI[0], pI[1], pJ[0], pJ[1])
		if d > max:
			max = d
	return max

def metricRadiusOfGyration(trace):
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
		sum = sum + (times[point[0]] * distance(point[3][0], point[3][1], centroid[0], centroid[1]))

	sum = sum * (1/T)	

	return math.sqrt(sum)

def metricStdDeviationDisplacement(trace):
	N = len(trace)
	D = 0
	for i in range(0, N-1):
		j = i + 1
		pI = trace[i][3]
		pJ = trace[j][3]
		D = D + distance(pI[0], pI[1], pJ[0], pJ[1])

	D = D/N

	sum = 0
	for i in range(0, N-1):
		j = i + 1
		pI = trace[i][3]
		pJ = trace[j][3]
		d = distance(pI[0], pI[1], pJ[0], pJ[1])

		sum = sum + math.pow((d - D), 2)

	sum = sum/N

	return math.sqrt(sum)

d
########################## MONTOLIU #########################

def processMontoliu():
	data = getData(['ts', 'lat', 'lon'])
	for d in data:
		# clusters = mon.extractStayPoints(data[d], 30, 10, 250.00)
		clusters = mon.stayPoints(data[d], 30, 10, 250.00)
		lats = getColumn(clusters, 1)
		lons = getColumn(clusters, 2)
		ty = getColumn(clusters, 0)
		# print lats
		if overlap:
			oLats = getColumn(data[d], 0)
			oLons = getColumn(data[d], 1)
			plotBoth(lats, lons, oLats, oLons, "montoliu_overlap_" + d, 10)
		else:
			plotTrace(lats, lons, ty, "montoliu_" + d, 10, False)
			# plotClusters(lats, lons, "montoliu_" + d, 10, False)

########################### PLOTS ###########################

def plotTrace(trace, data, fileName, stroke):
	
	dLats = getColumn(data, 0)
	dLons = getColumn(data, 1)
	t = getColumn(trace, 0)
	latsLons = getColumn(trace, 1)
	lats = getColumn(latsLons, 0)
	lons = getColumn(latsLons, 1)

	sPointsLats = []
	sPointsLons = []
	mPointsLats = []
	mPointsLons = []

	for i in range(0, len(lats)):
		if t[i] == "M":
			mPointsLats.append(lats[i])
			mPointsLons.append(lons[i])
		else:
			sPointsLats.append(lats[i])
			sPointsLons.append(lons[i])

	

	gmap = gmplot.GoogleMapPlotter(lats[0], lons[0], 15)
	gmap.plot(lats, lons, "#0000ff", edge_width=1)

	gmap.scatter(sPointsLats, sPointsLons, "#ff0000", size=20, marker=False)
	gmap.scatter(mPointsLats, mPointsLons, "#0000ff", size=10, marker=False)
	
	gmap.draw(output + "/" + fileName + ".html")

def plotBoth(cLats, cLons, lats, lons,fileName, markSize):

	gmap = gmplot.GoogleMapPlotter(lats[0], lons[0], 15)
	gmap.scatter(cLats, cLons, "#0000ff", size=markSize, marker=False)
	gmap.scatter(lats, lons, "#ff0000", size=markSize, marker=False)
	
	gmap.draw(output + "/" + fileName + ".html")

def plotClusters(lats, lons, fileName, markSize, heat):
	gmap = gmplot.GoogleMapPlotter(lats[0], lons[0], 15)

	if(heat):
		gmap.heatmap(lats, lons)
	else:
		gmap.scatter(lats, lons, '#ff0000', size=markSize, marker=False)
	
	gmap.draw(output + "/" + fileName + ".html")

############################ INIT ###########################
def init():
	extractor.useDb(db)
	
	if algorithm == "DBScan":
		processDBScanTrace()
	else:
		processMontoliu()


getCmdArguments()
init()




