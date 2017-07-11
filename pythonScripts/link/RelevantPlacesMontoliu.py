from Database_Handler import Database_Handler
import sys
from math import *
from datetime import datetime
from shapely.geometry import MultiPoint
from geopy.distance import *
import numpy as nu 
import userIdToId as mu
import datetime

import staticmap
from staticmap import StaticMap
from staticmap import CircleMarker
import gmplot
import userIdToId as u

fromCsv = None
output = None

csvPath = None
csvCols = None

dbInformation = None
dbCols = None
dbHandler = None
tableName = None

subject = None
mode = None
tStart = None
tEnd = None


def getCmdArguments():
	global dbInformation
	global dbHandler
	global mode
	global subject
	global fromCsv
	global csvPath
	global output
	global csvCols
	global dbCols
	global tStart
	global tEnd
	global tableName

	cmdArguments = sys.argv
	fromOpt = cmdArguments[1]

	if fromOpt != "-from":
		if fromOpt == '-h':
			print "Usage RelevantPlacesMontoliu.py -from <csv|db> <specificConfig> -m <all|<subjectId>> -o <path>"
			print "    <csv|db>: string. ('csv': get data from csv file) ('db': get data from database)"
			print "    <all|<subjectId>>: string. ('all': all subjects) (<subjectId>: specific subject id)"
			print "    <path>: string. Output path."
			print "    <specificConfig> list."
			print "        -from=csv -> -csvInfo <path> <ts_col_idx,lat_col_idx,lon_col_idx>"
			print "        -from=db -> -dbInfo <host,port,user,password,dbName> <ts_col_name, lat_col_name, lon_col_name>"
			sys.exit(0)
		else:
			print "-from <csv|db> must be the first argument option."
			sys.exit(1)

	fromVal = cmdArguments[2]

	if fromVal == "csv":
		fromCsv = True
		csvInfo = cmdArguments[3]

		if csvInfo != "-csvInfo":
			print "With -from csv option, the next argument must be -csvInfo."
			sys.exit(1)
		csvPath = cmdArguments[4]
		csvCols = cmdArguments[5].split(",")
	elif fromVal == "db":
		fromCsv = False
		dbInfo = cmdArguments[3]

		if dbInfo != "-dbInfo":
			print "With -from db option, the next argument must be -dbInfo."
			sys.exit(1)

		dbInformation = cmdArguments[4].split(",")
		dbHandler = Database_Handler(dbInformation[0], int(dbInformation[1]), dbInformation[2], dbInformation[3], dbInformation[4])
		dbI = cmdArguments[5].split(",")
		dbCols = dbI[1:]
		tableName = dbI[0:1]
	else:
		print "Unknown data mode. Please either choose 'csv' or 'db'"
		sys.exit(1)

	intervalOpd = cmdArguments[6]
	modeIdx = 6
	if intervalOpd == "-i":
		tStart = cmdArguments[7].split(",")[0]
		tEnd = cmdArguments[7].split(",")[1]
		modeIdx = 8

	modeOpt = cmdArguments[modeIdx]
	if modeOpt != "-m":
		print "Need -m option. Usage -m <'all'|<subjectId>>"
		sys.exit(1)
	mode = cmdArguments[modeIdx+1]
	if mode != 'all':
		subject = mode	

	if(len(cmdArguments) >= modeIdx+2):
		out = cmdArguments[modeIdx+2]
		if out != "-o":
			print "Unknown option"
			sys.exit(1)
		output = cmdArguments[modeIdx+3]

def getUserData(user):
	if fromCsv:
		return getUserDataFromCsv()
	else:
		return getUserDataFromDb(user)
		
def getUserDataFromCsv():
	csvF = nu.loadtxt(csvPath + "/" + subject + ".csv", delimiter=',', skiprows=1, usecols=(int(csvCols[0]), int(csvCols[1]), int(csvCols[2])))
	tsCol = []
	latCol = []
	lonCol = []

	for r in csvF:
		tsCol.append(datetime.datetime.fromtimestamp(r[0]/1000.0))
		latCol.append(r[1])
		lonCol.append(r[2])	

	return zip(tsCol, latCol, lonCol)

def getUserDataFromDb(user):

	qt = None
	if tStart != None:
		qt = "`gps`.time >= '" + tStart + "' and `gps`.time <= '" + tEnd + "'"

	if mode != 'all':
		query = "SELECT " + dbCols[0] + " as ts, " + dbCols[1] + " as latitude, " + dbCols[2] + " as longitude FROM `" + tableName[0] + "` WHERE user_id = '" + str(mu.getMemotionUserId(subject)) + "'" 
		if qt != None:
			query = query + " and " + qt
		query = query + " ORDER BY ts ASC"

	else:
		query = "SELECT " + dbCols[0] + " as ts, " + dbCols[1] + " as latitude, " + dbCols[2] + " as longitude FROM `" + tableName[0] + "` WHERE user_id = '" + str(user) + "'" 
		if qt != None:
			query = query + " WHERE " + qt	
		query = query + " ORDER BY ts ASC"

	result = dbHandler.select(query)
	return result
	

def spaceDistance(p1, p2):
	return distance(p1[1], p1[2], p2[1], p2[2])

# http://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula
def distance(lat1,lon1,lat2,lon2):
  miles = great_circle((lat1, lon1), (lat2, lon2)).miles
  return (miles/1.60934)*1000

def deg2rad(deg) :
  return deg * (pi/180)

def addMovementPoint(trace, point):
	ts = point[0]
	lat = point[1]
	lon = point[2]
	trace.append(("M", lat, lon, ts))

def addStayPoint(trace, pointList, i, j):
	points = pointList[i:j+1]
	lati, longi = estimateCentroid(points)
	tStart = points[0][0]
	tEnd = points[len(points)-1][0]
	stayPoint = ("S", lati, longi, tStart, tEnd)
	trace.append(stayPoint)

def stayPoints(pointList, tMin, tMax, dMax):
	N = len(pointList)
	i = 0
	trace = []
	currStayPoint = []
	while i < N:
		j = i + 1
		while j < N:
			t = _timeDifference(pointList[j], pointList[j-1])

			if t > tMax:
				if len(currStayPoint) > 1:
					addStayPoint(trace, currStayPoint)
					currStayPoint = []
				else:
					addMovementPoint(trace, pointList[j-1])
				i = j
				break

			d = spaceDistance(pointList[i], pointList[j])
			if d < dMax:
				t = _timeDifference(pointList[i], pointList[j])
				if not(len(currStayPoint) == 0) or t > tMin:
					currStayPoint.append(pointList[j-1])
				j = j + 1
			else:
				addMovementPoint(trace, pointList[j])
				# if len(currStayPoint) == 0:
					
				# else:
				# 	addStayPoint(trace, currStayPoint)
				# 	currStayPoint = []

				i = j
				break
		i = j

	for t in trace:
		print t
	return trace

def stayPoints(pointList, tMin, tMax, dMax):
	N = len(pointList)
	i = 0
	trace = []
	currStayPoint = []
	while i < N:
		j = i + 1
		while j < N:
			t = _timeDifference(pointList[j], pointList[j-1])

			if t > tMax:
				addMovementPoint(trace, pointList[j-1])
				i = j
				break

			d = spaceDistance(pointList[i], pointList[j])
			if d < dMax:
				t = _timeDifference(pointList[i], pointList[j])
				if j - 1 != i:
					if t < tMin:
						addStayPoint(trace, pointList, i, j)
						i = j
						break
				j = j + 1
			else:
				addMovementPoint(trace, pointList[j])
				addStayPoint(trace, pointList, i, j)
				i = j
				break
		i = j

	for t in trace:
		print t
	return trace

def extractStayPoints(pointList, tMin, tMax, dMax):
	N = len(pointList)
	id = 0
	i = 0
	lsp = []
	while i < N-1:
		j = i + 1
		while j < N-1:
			t = _timeDifference(pointList[j], pointList[j-1])
			if t > tMax:
				i = j
				break
			d = spaceDistance(pointList[i], pointList[j])
			if d > dMax:
				t = _timeDifference(pointList[i],pointList[j-1])
				if t > tMin:
					lati, longi = estimateCentroid(getPointsForCentroid(i, j, pointList))
					tStart = pointList[i][0]
					tEnd = pointList[j-1][0]
					sp = (id, lati, longi, tStart, tEnd)
					lsp.insert(len(lsp), sp)
					id = id + 1
				i = j
				break
			j = j + 1
			if j == len(pointList)-1:
				i = j
	return lsp

# def estimateStayRegions(stayPointList, dMax):
# 	sp = set()
# 	for d in range(0, len(stayPointList)):
# 		sp = sp | stayPointList[d]

# def getCenterMostPoint(cluster):
# 	d = zip(*cluster)
# 	print d[1]
# 	print d[2]
# 	if len(cluster) >= 1:
# 	    x = MultiPoint(list(d[1])).centroid.x
# 	    y = MultiPoint(list(d[2])).centroid.y
# 	    centroid = (x,y)
# 	    print centroid
# 	    # centermostPoint = min(cluster, key=lambda point: great_circle(point, centroid).m)
# 	    return tuple(centermostPoint)

def sign(x):
	if x >= 0:
		return 1
	else:
		return -1

# http://www.samuelbosch.com/2014/05/working-in-lat-long-great-circle.html
def estimateCentroid(points):
	""" 
    http://www.geomidpoint.com/example.html 
    http://gis.stackexchange.com/questions/6025/find-the-centroid-of-a-cluster-of-points
    """
	sum_x,sum_y,sum_z = 0,0,0
	for p in points:	
		lat = radians(p[1])
		lon = radians(p[2])
		## convert lat lon to cartesian coordinates
		sum_x = sum_x + cos(lat) * cos(lon)
		sum_y = sum_y + cos(lat) * sin(lon)
		sum_z = sum_z + sin(lat)
	avg_x = sum_x / float(len(points))
	avg_y = sum_y / float(len(points))
	avg_z = sum_z / float(len(points))
	center_lon = atan2(avg_y,avg_x)
	hyp = sqrt(avg_x*avg_x + avg_y*avg_y) 
	center_lat = atan2(avg_z, hyp)
	return degrees(center_lat), degrees(center_lon)

def getAllUsersData():
	users = u.getUsersList()
	data = {}
	for user in users:
		data[users[user]] = getUserData(user)

	return data
def _timeDifference(p1, p2):
	diff = (p1[0] - p2[0])
	return abs(diff.total_seconds())/60

def getPointsForCentroid(i, j, pointList):
	return pointList[i:j+1]



# data = getAllUsersData()
# data = getUserData(subject)
# clusters = extractStayPoints(data, 40, 10, 250.00)

# plotData(subject, clusters)
# plotOriginalData(subject, data)
# plotBoth(clusters, data, subject, 10)