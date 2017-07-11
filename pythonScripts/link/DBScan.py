import pandas as pa
import pygal
import numpy as nu 
import plotly.graph_objs as go
import plotly.plotly as pl
from sklearn.cluster import DBSCAN
from geopy.distance import great_circle
from shapely.geometry import MultiPoint
import sys
from Plotter import ScatterChartPlotter
import staticmap
from staticmap import StaticMap
from staticmap import CircleMarker
import gmplot
from geopy.distance import *

global fileName
fileName = ""
kmRadiant = 6371.0088
epsilon = 0.250 / kmRadiant
# epsilon = 0.25
minSamples = 2
algorithm = 'ball_tree'
metric = 'euclidean'
tMin = 30
tMax = 21

def _timeDifference(p1, p2):
	diff = (p1 - p2)
	return abs(diff.total_seconds())/60

# http://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula
def distance(lat1,lon1,lat2,lon2):
  	miles = great_circle((lat1, lon1), (lat2, lon2)).miles
  	return (miles/1.60934)*1000

def getClustersTupleList(clusters):
	t = []
	for c in clusters:
		t.append((c[0], c[1]))

	return t

def plot(initX, initY, x, y, fileName, markSize, heat):
	gmap = gmplot.GoogleMapPlotter(initX,initY, 20)
	if(heat):
		gmap.heatmap(x, y)
	else:
		gmap.scatter(x, y, '#3B0B39', size=markSize, marker=False)
	
	gmap.draw( fileName + ".html")


def getCenterMostPoint(cluster):
	if len(cluster) >= 1:
	    x = MultiPoint(cluster).centroid.x
	    y = MultiPoint(cluster).centroid.y
	    centroid = (x,y)
	    centermostPoint = min(cluster, key=lambda point: great_circle(point, centroid).m)
	    return (x, y)

def getColumn(data, idx):
	column = []
	for d in data:
		column.append(d[idx])

	return column

def getCentroid(cluster):
	xx = getColumn(cluster, 1)
	yy = getColumn(cluster, 2)

	x = MultiPoint(zip(xx, yy)).centroid.x
	y = MultiPoint(zip(xx, yy)).centroid.y
	return (x,y)

# def getTraceCluster(clusters, point, d):
# 	i = 0
# 	for c in clusters:
# 		dist = distance(point[0], point[1], c[0], c[1])
# 		if dist < d:
# 			return i
# 		i = i + 1

# 	return None

# def _getTraceCluster(clusters, point, d):
# 	i = 0
# 	for c in clusters:
# 		dist = distance(point[1], point[2], c[0], c[1])
# 		if dist < d:
# 			return i
# 		i = i + 1

# 	return None

# cluster, tStart, tEnd, coords
def getTrace(data):
	clusters = getClusters(data)
	
	pTrace = []
	for d in data:
		c = getTraceCluster(clusters, d, 250.00)
		if c != None:
			pTrace.append((d[0], c))
		else:
			pTrace.append((d[0], "N/A"))

	trace = []
	currCluster = None
	currStayPoints = []
	# for i in range(1, len(pTrace)):
	# 	p = pTrace[i]
	# 	if p[1] == currCluster:
	# 		currStayPoints.append(p)
	# 	else:
	# 		if len(currStayPoints) > 1 and _timeDifference(currStayPoints[0][0], currStayPoints[-1][0]) >= tMin:
	# 			trace.append((currCluster, currStayPoints[0][0], currStayPoints[-1][0], clusters[currCluster]))
	# 		currCluster = p[1]
	# 		currStayPoints = [p]

	for i in range(0, len(pTrace)):
		p = pTrace[i]

		if p[1] == "N/A":
			if len(currStayPoints) > 1:
				trace.append((currCluster, currStayPoints[0][0], currStayPoints[-1][0], clusters[currCluster]))
			currStayPoints = []
			currCluster = None
		else:
			if len(currStayPoints) == 0:
				currStayPoints.append(p)
				currCluster = p[1]
			else:
				if p[1] == currCluster:
					if len(currStayPoints) <= 0:
						currStayPoints.append(p)
					else:
						if _timeDifference(currStayPoints[0][0], p[0]) >= tMin:
							currStayPoints.append(p)	
				else:
					if len(currStayPoints) > 1:
						trace.append((currCluster, currStayPoints[0][0], currStayPoints[-1][0], clusters[currCluster]))
					currCluster = p[1]
					currStayPoints = [p]

	if len(currStayPoints) > 1:
		trace.append((currCluster, currStayPoints[0][0], currStayPoints[-1][0], clusters[currCluster]))
	return trace

def getTraceWithClusters(data, clusters):
	pTrace = []
	for d in data:
		c = getTraceCluster(clusters, d, 250.00)
		if c != None:
			pTrace.append((d[0], c))
		else:
			pTrace.append((d[0], "N/A"))

	trace = []
	currCluster = None
	currStayPoints = []
	# for i in range(1, len(pTrace)):
	# 	p = pTrace[i]
	# 	if p[1] == currCluster:
	# 		currStayPoints.append(p)
	# 	else:
	# 		if len(currStayPoints) <= 1 and _timeDifference(currStayPoints[0][0], p[0]) >= tMin:
	# 			trace.append((currCluster, currStayPoints[0][0], p[0], clusters[currCluster]))
	# 		#IF 1, THEN USE END TIME OF NEXT RECORD
	# 		if len(currStayPoints) > 1 and _timeDifference(currStayPoints[0][0], currStayPoints[-1][0]) >= tMin:
	# 			trace.append((currCluster, currStayPoints[0][0], currStayPoints[-1][0], clusters[currCluster]))

	# 		currCluster = p[1]
	# 		currStayPoints = [p]
	for i in range(0, len(pTrace)):
		p = pTrace[i]

		if p[1] == "N/A":
			if len(currStayPoints) > 1:
				trace.append((currCluster, currStayPoints[0][0], currStayPoints[-1][0], clusters[currCluster]))
			currStayPoints = []
			currCluster = None
		else:
			#consider use <= 1
			if len(currStayPoints) == 0:
				currStayPoints.append(p)
				currCluster = p[1]
			else:
				if p[1] == currCluster:
					if len(currStayPoints) <= 0:
						currStayPoints.append(p)
					else:
						if _timeDifference(currStayPoints[0][0], p[0]) >= tMin:
							currStayPoints.append(p)
				else:
					if len(currStayPoints) > 1:
						trace.append((currCluster, currStayPoints[0][0], currStayPoints[-1][0], clusters[currCluster]))
					currCluster = p[1]
					currStayPoints = [p]

	if len(currStayPoints) > 1:
		trace.append((currCluster, currStayPoints[0][0], currStayPoints[-1][0], clusters[currCluster]))
	return trace

def getDataList(M):
	l = []
	for i in M:
		l.append(i)

	return l

def getStayRegions(stayPoints):
	# coords = getColumn(stayPoints, 0)
	# print coords
	return getClusters(stayPoints)

def _getTraceCluster(clusters, point, d):
	i = 0
	for c in clusters:
		dist = distance(point[0][0], point[0][1], c[0], c[1])
		# print dist
		if dist < d:
			return i
		i = i + 1

	return None

def getTraceCluster(clusters, point, d):
	i = 0
	for c in clusters:
		dist = distance(point[1], point[2], c[0], c[1])
		if dist < d:
			return i
		i = i + 1

	return None

def getTraceMontoliu(M):
	l = getDataList(M)
	stayPoints = []

	for i in range(0, len(l)):
		pi = l[i]
		for j in range(i+1, len(l)):
			pj = l[j]
			_pj = l[j-1]
			tMaxDiff = _timeDifference(_pj[0], pj[0])

			if tMaxDiff > tMax:
				i = j
				# stayPoint = getStayPoint(currStayPoint)
				# if stayPoint != None:
				# 	stayPoints.append(stayPoint)
				# currStayPoint = []
				break

			d = distance(pi[1], pi[2], pj[1], pj[2])

			if d > 250:
				tMinDiff = _timeDifference(pi[0], _pj[0])

				if tMinDiff > tMin:
					stayPoints.append((getCentroid(l[i:j+1]), l[i][0], l[j-1][0]))
				i = j
				break

	return stayPoints
		# stayRegions = getStayRegions(stayPoints)
		# print stayRegions
		# trace = []

		# for i in range (0, len(stayPoints)):
		# 	stayPoint = stayPoints[i]
		# 	s = getTraceCluster(stayRegions, stayPoint, 250)
		# 	trace.append((s, stayPoint[0], stayPoint[1], stayPoint[2]))

		# return trace




def removeTsColumn(M):
	ts = []
	latsLons = []

	for i in range(0, len(M)):

		latsLons.append([M[i][1], M[i][2]])

	return nu.asarray(latsLons)

def _removeTsColumn(M):
	latsLons = []

	for i in range(0, len(M)):
		latsLons.append([M[i][0][0], M[i][0][1]])

	return nu.asarray(latsLons)

def getClusters(M):
	M = removeTsColumn(M)
	result = DBSCAN(eps=epsilon, min_samples=minSamples).fit(nu.radians(M))
	clusterLabels = result.labels_
	numClusters = len(set(clusterLabels))
	c = pa.Series([M[clusterLabels == n] for n in range(len(set(clusterLabels)))])
	centermostPoints = c.map(getCenterMostPoint)
	centermostPoints = [i for i in centermostPoints if i != None]

	lats, lons = zip(*centermostPoints)
	clusters = pa.DataFrame({'longitude':lons, 'latitude':lats})

	return clusters.as_matrix()

def filterbyvalue(seq, value):
   for el in seq:
       if el.attribute==value: yield el

def plotClusters(csv):
	clusters = getClusters(csv)

	t = getClustersTupleList(clusters)
	x = []
	y = []

	for i in range(0, len(clusters)):
		x.append(clusters[i][0])
		y.append(clusters[i][1])

	plot(x[0],y[0], x, y, "clusters", 30, False)

def plotOriginalData(csv):
	x = []
	y = []

	for i in range(0, len(csv)):
		x.append(csv[i][0])
		y.append(csv[i][1])

	plot(x[0],y[0], x, y, "original", 10, False)

def plotBoth(clusters, data, fileName, markSize):
	lat = []
	lon = []

	for i in range(0, len(clusters)):
		lat.append(clusters[i][1])
		lon.append(clusters[i][2])

	x = []
	y = []

	for i in range(0, len(data)):
		x.append(data[i][1])
		y.append(data[i][2])

	gmap = gmplot.GoogleMapPlotter(x[0], y[0], 20)
	gmap.scatter(x, y, "#0000ff", size=markSize, marker=False)
	gmap.scatter(lat, lon, "#ff0000", size=markSize, marker=False)
	gmap.draw(output + "/all_" + fileName + ".html")

def init():
	csvF = nu.loadtxt(fileName, delimiter=',', skiprows=1, usecols=range(3,5))

	clusters = getClusters(csvF)
	# print clusters
	# plotBoth(clusters, csvF, "aaa", 1)

	# plotClusters(csvF)
	# plotOriginalData(csvF)

# fileName = sys.argv[1]
# init()