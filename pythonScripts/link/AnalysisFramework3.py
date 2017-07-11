import sys
import DataExtractor as extractor
import DBScan as DBScan
from collections import OrderedDict
import os
import MovementMetrics2 as me
import datetime
import time
from scipy.stats.stats import pearsonr
from Plotter import LineChartPlotter
import Plot
import gmplot
import math

## DB
db = None
subject = None

## Relevant places
algorithm = None

## Intervals
interval = None
date = None
fullStudy = False
frequency = "daily"
StudentLifeStart = datetime.datetime.now().replace(year = 2013, month = 3 , day=27, hour=0, minute=0, second=1)
StudentLifeEnd = datetime.datetime.now().replace(year = 2013, month = 6 , day=1, hour=23, minute=59, second=59)

## Destination
## Default pwd
script = os.path.realpath(__file__)
output = script[0: script.rfind("/")]

nbOne = 0

removedUsers = []
goodUsers = []
def getCmdArguments():
	global db
	global subject
	global algorithm
	global date
	global interval
	global output
	global fullStudy
	global frequency

	args = sys.argv

	if len(args) < 7:
		print "Usage: python AnalysisFramework.py -a <opt> -db <opt> -u <opt> -f <opt> -o <path> -d <opt> "
		print "    -a: algorithm to compute clusters <DBScan|Montoliu>."
		print "    -db: database from which get the data <Memotion|StudentLife>."
		print "    -u: subject or all subjects <subjectName|all>."
		print "    -f: frequency of metrics. <daily|weekly> "
		print "    -o: analysis results output path. Default to pwd."
		print "    -d: date start and date end. Default to whole study. <dateStart,dateEnd>"

		sys.exit(1)

	for i in xrange(1, len(args), 2):
		if args[i] == "-a":
			algorithm = args[i+1]
		elif args[i] ==  "-db":
			db = args[i+1]
		elif args[i] == "-u":
			subject = args[i+1]
		elif args[i] == "-f":
			frequency = args[i+1]
		elif args[i] == "-o":
			output = args[i+1]
		elif args[i] == "-d":
			d = args[i+1]
			if d.find(",") >= 0:
				interval = d.split(",")
			else:
				date = d

	if date == None and interval == None:
		fullStudy = True

######################### UTIL #########################

def getColumn(data, idx):
	column = []
	for d in data:
		column.append(d[idx])

	return column

def getDate(date, begin):
	if begin:
		time = '00:00:01'
	else:
		time = '23:59:59'

	return date + " " + time

def divideInDays(data):
	days = {}

	for d in data:

		date = d[0].date()
		date = date.strftime('%Y/%m/%d')
		if date in days:
			days[date].append(d)
		else:
			days[date] = []
			days[date].append(d)

	return OrderedDict(sorted(days.items()))

######################### DATA #########################

def getUserData(cols, user, interval):
	if interval != None:
		return extractor.getUserLocationDataInterval(user, getDate(interval[0], True), getDate(interval[1], False), cols)
	else:
		return extractor.getUserLocationData(user, cols)
	
def getData(cols, subject, interval):
	data = {}
	if subject != "all":
		data[subject] = getUserData(cols, subject, interval)
	else:
		users = extractor.getUsers()
		for user in users:
			data[user] = getUserData(cols, user, interval)

	return data

def getWeeks(firstDay, lastDay, nb):
	if nb == '1week':
		return getOneWeeks(firstDay, lastDay)
	elif nb == '2week':
		return getTwoWeeks(firstDay, lastDay)
	elif nb == 'overlap':
		return getOverlapTwoWeeks(firstDay, lastDay)

def getOneWeeks(firstDay, lastDay):
	weeks = []

	day_of_week = firstDay.weekday()

	to_beginning_of_week = datetime.timedelta(days=day_of_week)
	beginning_of_week = firstDay - to_beginning_of_week

	to_end_of_week = datetime.timedelta(days=6 - day_of_week)
	end_of_week = firstDay + to_end_of_week

	weeks.append((beginning_of_week, end_of_week))

	currBegin = end_of_week + datetime.timedelta(days=1)
	while currBegin < lastDay:
		weeks.append((currBegin, currBegin + datetime.timedelta(days=6)))
		currBegin = currBegin + datetime.timedelta(days=7)

	return weeks

def getTwoWeeks(firstDay, lastDay):
	weeks = []

	day_of_week = firstDay.weekday()

	to_beginning_of_week = datetime.timedelta(days=day_of_week)
	beginning_of_week = firstDay - to_beginning_of_week

	to_end_of_week = datetime.timedelta(days=13 - day_of_week)
	end_of_week = firstDay + to_end_of_week

	weeks.append((beginning_of_week, end_of_week))

	currBegin = end_of_week + datetime.timedelta(days=1)
	while currBegin < lastDay:
		weeks.append((currBegin, currBegin + datetime.timedelta(days=13)))
		currBegin = currBegin + datetime.timedelta(days=14)

	return weeks

def getOverlapTwoWeeks(firstDay, lastDay):
	weeks = []

	day_of_week = firstDay.weekday()

	to_beginning_of_week = datetime.timedelta(days=day_of_week)
	beginning_of_week = firstDay - to_beginning_of_week

	to_end_of_week = datetime.timedelta(days=13 - day_of_week)
	end_of_week = firstDay + to_end_of_week

	weeks.append((beginning_of_week, end_of_week))

	currBegin = beginning_of_week + datetime.timedelta(days=7)

	while currBegin < lastDay:
		weeks.append((currBegin, currBegin + datetime.timedelta(days=13)))
		currBegin = currBegin + datetime.timedelta(days=7)

	# print weeks[-1][1]
	weeks[-1] = (weeks[-1][0], StudentLifeEnd)

	return weeks

def divideTraceInWeeks(fullTrace):
	firstDay = fullTrace[0][1]
	lastDay = fullTrace[-1][1]

	weeks = {}

	day_of_week = firstDay.weekday()

	to_beginning_of_week = datetime.timedelta(days=day_of_week)
	beginning_of_week = firstDay - to_beginning_of_week

	to_end_of_week = datetime.timedelta(days=6 - day_of_week)
	end_of_week = firstDay + to_end_of_week

	weeks.append((beginning_of_week, end_of_week))

	currBegin = end_of_week + datetime.timedelta(days=1)
	while currBegin < lastDay:
		weeks.append((currBegin, currBegin + datetime.timedelta(days=6)))
		currBegin = currBegin + datetime.timedelta(days=7)


	return weeks

########################## OUT #########################

def plotCorrelations(preCorrelations, postCorrelations, freq):
	# for c in preCorrelations:
	# 	print preCorrelations

	# for c in postCorrelations:
	# 	print postCorrelations

	if freq == "1week" or freq == "overlap":
		xLabels = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
	else:
		xLabels = [1, 2, 3, 4, 5]

	metricLabels = ['total_distance', 'max_distance', 'radius_gyration', 'std_deviation_displacement', 'max_distance_home', 'different_places_visited']
	preTotDist = []
	preMaxDist = []
	preGyration = []
	preDev = []
	preMaxDistHome = []
	preDiff = []
	errPreTotDist = []
	errPreMaxDist = []
	errPreGyration = []
	errPreDev = []
	errPreMaxDistHome = []
	errPreDiff = []

	for i in preCorrelations:
		preTotDist.append(preCorrelations[i]['total_distance'][0])
		preMaxDist.append(preCorrelations[i]['max_distance'][0])
		preGyration.append(preCorrelations[i]['radius_gyration'][0])
		preDev.append(preCorrelations[i]['std_deviation_displacement'][0])
		preMaxDistHome.append(preCorrelations[i]['max_distance_home'][0])
		preDiff.append(preCorrelations[i]['different_places_visited'][0])

		errPreTotDist.append(preCorrelations[i]['total_distance'][1])
		errPreMaxDist.append(preCorrelations[i]['max_distance'][1])
		errPreGyration.append(preCorrelations[i]['radius_gyration'][1])
		errPreDev.append(preCorrelations[i]['std_deviation_displacement'][1])
		errPreMaxDistHome.append(preCorrelations[i]['max_distance_home'][1])
		errPreDiff.append(preCorrelations[i]['different_places_visited'][1])

	Plot.plotCorrelations(xLabels, [preTotDist, preMaxDist, preGyration, preDev, preMaxDistHome, preDiff], metricLabels, [errPreTotDist, errPreMaxDist, errPreGyration, errPreDev, errPreMaxDistHome, errPreDiff], output + "/pre_" + freq)
	
	postTotDist = []
	postMaxDist = []
	postGyration = []
	postDev = []
	postMaxDistHome = []
	postDiff = []

	errPostTotDist = []
	errPostMaxDist = []
	errPostGyration = []
	errPostDev = []
	errPostMaxDistHome = []
	errPostDiff = []

	for i in preCorrelations:
		postTotDist.append(postCorrelations[i]['total_distance'][0])
		postMaxDist.append(postCorrelations[i]['max_distance'][0])
		postGyration.append(postCorrelations[i]['radius_gyration'][0])
		postDev.append(postCorrelations[i]['std_deviation_displacement'][0])
		postMaxDistHome.append(postCorrelations[i]['max_distance_home'][0])
		postDiff.append(postCorrelations[i]['different_places_visited'][0])

		errPostTotDist.append(postCorrelations[i]['total_distance'][1])
		errPostMaxDist.append(postCorrelations[i]['max_distance'][1])
		errPostGyration.append(postCorrelations[i]['radius_gyration'][1])
		errPostDev.append(postCorrelations[i]['std_deviation_displacement'][1])
		errPostMaxDistHome.append(postCorrelations[i]['max_distance_home'][1])
		errPostDiff.append(postCorrelations[i]['different_places_visited'][1])


	Plot.plotCorrelations(xLabels, [postTotDist, postMaxDist, postGyration, postDev, postMaxDistHome, postDiff], metricLabels, [errPostTotDist, errPostMaxDist, errPostGyration, errPostDev, errPostMaxDistHome, errPostDiff], output + "/post_" + freq)


def printMovementAnalysis(analysis):
	one = False
	theOnes = {}
	for user in analysis:
		print "+++++++++++++++++++++++++++"
		print "+++++++++++++++++++++++++++"
		print "Subject: " + user
		print ""
		for a in analysis[user][1]:
			print ""
			print "----" + a + "----"
			print analysis[user][0]
			for m in analysis[user][1][a]:
				if m == 'different_places_visited':
					if analysis[user][1][a][m] <= 1:
						theOnes[user] = 1
				print m + ": " + str(analysis[user][1][a][m])

def writePlots(metrics, freq):
	path = output + "/"
	
	for user in metrics:
		lats = None
		lons = None
		data = metrics[user]
		clusters = data['clusters']
		fullData = data['fullData']
		cLats = getColumn(clusters, 0)
		cLons = getColumn(clusters, 1)

		gmap = gmplot.GoogleMapPlotter(cLats[0], cLons[0], 15)
		gmap.scatter(cLats, cLons, '#0000ff', size=250, marker=False)
		gmap.scatter(getColumn(fullData, 1), getColumn(fullData, 2), '#ff0000', size=15, marker=False)
		gmap.draw(path + user + "/clusters_fullData" + ".html")

		for period in data['metrics']:
			lats = None
			lons = None
			cPath = None
			cPath = path + user + "/movements_" + freq + "_" + period
			lats = getColumn(data['metrics'][period]['data'], 1)
			lons = getColumn(data['metrics'][period]['data'], 2)

			gmap = gmplot.GoogleMapPlotter(lats[0], lons[0], 15)
			gmap.scatter(cLats, cLons, '#0000ff', size=250, marker=False)
			gmap.scatter(lats, lons, '#ff0000', size=15, marker=False)
			gmap.draw(cPath + ".html")


def statistics(correlations, freq):

	preTotDistAvg = 0
	preTotDistPAvg = 0

	preMaxDistAvg = 0
	preMaxDistPAvg = 0

	preGyrAvg = 0
	preGyrPAvg = 0

	preDevAvg = 0
	preDevPAvg = 0

	preDistHomeAvg = 0
	preDistHomePAvg = 0

	preDiffAvg = 0
	preDiffPAvg = 0

	postTotDistAvg = 0
	postTotDistPAvg = 0

	postMaxDistAvg = 0
	postMaxDistPAvg = 0

	postGyrAvg = 0
	postGyrPAvg = 0

	postDevAvg = 0
	postDevPAvg = 0

	postDistHomeAvg = 0
	postDistHomePAvg = 0

	postDiffAvg = 0
	postDiffPAvg = 0
	N = 0
	for period in correlations[0]:
		preTotDistAvg = preTotDistAvg + abs(correlations[0][period]['total_distance'][0])
		preMaxDistAvg = preMaxDistAvg + abs(correlations[0][period]['max_distance'][0])
		preGyrAvg = preGyrAvg + abs(correlations[0][period]['radius_gyration'][0])
		preDevAvg = preDevAvg + abs(correlations[0][period]['std_deviation_displacement'][0])
		preDistHomeAvg = preDistHomeAvg + abs(correlations[0][period]['max_distance_home'][0])
		preDiffAvg = preDiffAvg + abs(correlations[0][period]['different_places_visited'][0])

		preTotDistPAvg = preTotDistPAvg + abs(correlations[0][period]['total_distance'][1])
		preMaxDistPAvg = preMaxDistPAvg + abs(correlations[0][period]['max_distance'][1])
		preGyrPAvg = preGyrPAvg + abs(correlations[0][period]['radius_gyration'][1])
		preDevPAvg = preDevPAvg + abs(correlations[0][period]['std_deviation_displacement'][1])
		preDistHomePAvg = preDistHomePAvg + abs(correlations[0][period]['max_distance_home'][1])
		preDiffPAvg = preDiffPAvg + abs(correlations[0][period]['different_places_visited'][1])
		N = N + 1

	preTotDistAvg = preTotDistAvg/N
	preMaxDistAvg = preMaxDistAvg/N
	preGyrAvg = preGyrAvg/N
	preDevAvg = preDevAvg/N
	preDistHomeAvg = preDistHomeAvg/N
	preDiffAvg = preDiffAvg/N

	preTotDistPAvg = preTotDistPAvg/N
	preMaxDistPAvg = preMaxDistPAvg/N
	preGyrPAvg = preGyrPAvg/N
	preDevPAvg = preDevPAvg/N
	preDistHomePAvg = preDistHomePAvg/N
	preDiffPAvg = preDiffPAvg/N

	N = 0
	for period in correlations[1]:
		postTotDistAvg = postTotDistAvg + abs(correlations[1][period]['total_distance'][0])
		postMaxDistAvg = postMaxDistAvg + abs(correlations[1][period]['max_distance'][0])
		postGyrAvg = postGyrAvg + abs(correlations[1][period]['radius_gyration'][0])
		postDevAvg = postDevAvg + abs(correlations[1][period]['std_deviation_displacement'][0])
		postDistHomeAvg = postDistHomeAvg + abs(correlations[1][period]['max_distance_home'][0])
		postDiffAvg = postDiffAvg + abs(correlations[1][period]['different_places_visited'][0])

		postTotDistPAvg = postTotDistPAvg + abs(correlations[1][period]['total_distance'][1])
		postMaxDistPAvg = postMaxDistPAvg + abs(correlations[1][period]['max_distance'][1])
		postGyrPAvg = postGyrPAvg + abs(correlations[1][period]['radius_gyration'][1])
		postDevPAvg = postDevPAvg + abs(correlations[1][period]['std_deviation_displacement'][1])
		postDistHomePAvg = postDistHomePAvg + abs(correlations[1][period]['max_distance_home'][1])
		postDiffPAvg = postDiffPAvg + abs(correlations[1][period]['different_places_visited'][1])
		N = N + 1

	postTotDistAvg = postTotDistAvg/N
	postMaxDistAvg = postMaxDistAvg/N
	postGyrAvg = postGyrAvg/N
	postDevAvg = postDevAvg/N
	postDistHomeAvg = postDistHomeAvg/N
	postDiffAvg = postDiffAvg/N

	postTotDistPAvg = postTotDistPAvg/N
	postMaxDistPAvg = postMaxDistPAvg/N
	postGyrPAvg = postGyrPAvg/N
	postDevPAvg = postDevPAvg/N
	postDistHomePAvg = postDistHomePAvg/N
	postDiffPAvg = postDiffPAvg/N

	print "==================== AVG ====================" 
	print "==================== PRE ====================" 
	print "Total distance: " + str(preTotDistAvg) + ", " + str(preTotDistPAvg)
	print "Max distance: " + str(preMaxDistAvg) + ", " + str(preMaxDistPAvg)
	print "Radius gyration: " + str(preGyrAvg) + ", " + str(preGyrPAvg)
	print "Std deviation: " + str(preDevAvg) + ", " + str(preDevPAvg)
	print "Max distance home: " + str(preDistHomeAvg) + ", " + str(preDistHomePAvg) 
	print "Different places: " + str(preDiffAvg) + ", " + str(preDiffPAvg)

	print "==================== POST ====================" 
	print "Total distance: " + str(postTotDistAvg) + ", " + str(postTotDistPAvg)
	print "Max distance: " + str(postMaxDistAvg) + ", " + str(postMaxDistPAvg)
	print "Radius gyration: " + str(postGyrAvg) + ", " + str(postGyrPAvg)
	print "Std deviation: " + str(postDevAvg) + ", " + str(postDevPAvg)
	print "Max distance home: " + str(postDistHomeAvg) + ", " + str(postDistHomePAvg) 
	print "Different places: " + str(postDiffAvg) + ", " + str(postDiffPAvg)

	print "==================== TOT ====================" 
	print "==================== PRE ====================" 
	for period in correlations[0]:
		print period
		print "Total distance: " + str(correlations[0][period]['total_distance'][0]) + ", " + str(correlations[0][period]['total_distance'][1])
		print "Max distance: " + str(correlations[0][period]['max_distance'][0]) + ", " + str(correlations[0][period]['max_distance'][1])
		print "Radius gyration: " + str(correlations[0][period]['radius_gyration'][0]) + ", " + str(correlations[0][period]['radius_gyration'][1])
		print "Std deviation: " + str(correlations[0][period]['std_deviation_displacement'][0]) + ", " + str(correlations[0][period]['std_deviation_displacement'][1])
		print "Max distance home: " + str(correlations[0][period]['max_distance_home'][0]) + ", " + str(correlations[0][period]['max_distance_home'][1])
		print "Different places: " + str(correlations[0][period]['different_places_visited'][0]) + ", " + str(correlations[0][period]['different_places_visited'][1])

	print "==================== POST ====================" 
	for period in correlations[1]:
		print period
		print "Total distance: " + str(correlations[1][period]['total_distance'][0]) + ", " + str(correlations[1][period]['total_distance'][1])
		print "Max distance: " + str(correlations[1][period]['max_distance'][0]) + ", " + str(correlations[1][period]['max_distance'][1])
		print "Radius gyration: " + str(correlations[1][period]['radius_gyration'][0]) + ", " + str(correlations[1][period]['radius_gyration'][1])
		print "Std deviation: " + str(correlations[1][period]['std_deviation_displacement'][0]) + ", " + str(correlations[1][period]['std_deviation_displacement'][1])
		print "Max distance home: " + str(correlations[1][period]['max_distance_home'][0]) + ", " + str(correlations[1][period]['max_distance_home'][1])
		print "Different places: " + str(correlations[1][period]['different_places_visited'][0]) + ", " + str(correlations[1][period]['different_places_visited'][1])

def writeOut(metrics, correlations, freq):
	createAnalysisFolders()
	statistics(correlations, freq)
	plotCorrelations(correlations[0], correlations[1], freq)
	for user in metrics:
		writeMetrics(metrics[user]['metrics'], user)
	writePlots(metrics, freq)
	return

################## MOVEMENT ANALYSIS ###################

def computeMetrics(data, clusters, fullTrace, user):
	if user == "user58":
		print "+=+=+======++=+++++======++=======+"
		print user

	metrics = {}
	for period in data:
		trace = DBScan.getTraceWithClusters(data[period], clusters)
		metrics[period] = {}
		metrics[period]['trace'] = trace
		metrics[period]['data'] = data[period]
		metrics[period]['total_distance'] = me.metricTotalDistance(trace)
		metrics[period]['max_distance'] = me.metricMaxDistanceBetweenPoints(trace)
		metrics[period]['radius_gyration'] = me.metricRadiusOfGyration(trace)
		metrics[period]['std_deviation_displacement'] = me.metricStdDeviationDisplacement(trace)
		metrics[period]['max_distance_home'] = me.metricMaximumDistanceFromHome(fullTrace, trace)
		metrics[period]['different_places_visited'] = me.metricNumberOfVisitedPlaces(trace)
		metrics[period]['relevant_places'] = me.metricRelevantPlaces(fullTrace)
		metrics[period]['relevant_places_visited'] = me.metricRelevantPlacesNb(fullTrace, trace)
			

	return OrderedDict(sorted(metrics.items(), key= lambda x: time.strptime(x[0].split(",")[0], "%Y-%m-%d")))

def checkMetrics(metrics, user):
	for m in metrics:
		if metrics[m]['different_places_visited'] <= 1:
			print "Removing user " + user + ". One place visited for period " + m
			removedUsers.append(user)
			return False	
	
	return True

############## DAILY MOEVEMENT ANALYSIS ################

# def getDailyMovementResults(user, data, clusters, fullTrace):
# 	daysTraces = {}
# 	daysMetrics = {}

# 	for day in data:
# 		if not(day in daysMetrics):
# 			daysMetrics[day] = {}

# 		trace = DBScan.getTrace(data[day])

# 		daysMetrics[day]['trace'] = trace
# 		daysMetrics[day]['data'] = data[day]
# 		daysMetrics[day]['total_distance'] = metrics.metricTotalDistance(trace)
# 		daysMetrics[day]['max_distance'] = metrics.metricMaxDistanceBetweenPoints(trace)
# 		daysMetrics[day]['radius_gyration'] = metrics.metricRadiusOfGyration(trace)
# 		daysMetrics[day]['std_deviation_displacement'] = metrics.metricStdDeviationDisplacement(trace)
# 		daysMetrics[day]['max_distance_home'] = metrics.metricMaximumDistanceFromHome(fullTrace, trace)
# 		daysMetrics[day]['different_places_visited'] = metrics.metricNumberOfVisitedPlaces(trace)
# 		daysMetrics[day]['relevant_places'] = metrics.metricRelevantPlaces(fullTrace)

# 	return OrderedDict(sorted(daysMetrics.items()))

# def dailyMovementAnalysis():
# 	usersAnalysis = {}
# 	if subject != "all":
# 		fullData = getData(['ts', 'lat', 'lon'], subject, None)
# 		fullClusters = DBScan.getClusters(fullData[subject])

# 		fullTrace = DBScan.getTraceWithClusters(fullData[subject], fullClusters)
# 		dailyData = divideInDays(fullData[subject])
# 		usersAnalysis[subject] = getDailyMovementResults(subject, dailyData, fullClusters, fullTrace)
# 	else:
# 		for user in users:
# 			fullData = getData(['ts', 'lat', 'lon'], user, None)
# 			if len(fullData[user]) > 0:
# 				fullClusters = DBScan.getClusters(fullData[user])
	
# 				fullTrace = DBScan.getTraceWithClusters(fullData[user], fullClusters)
# 				dailyData = divideInDays(fullData[user])
# 				if dailyData != None:
# 					usersAnalysis[user] = getDailyMovementResults(user, dailyData, fullClusters, fullTrace)
			

# 	return usersAnalysis

def getDays(firstDate, lastDate):
	days = []


	days.append((firstDate, firstDate + datetime.timedelta(days=1)))

	currBegin = firstDate + datetime.timedelta(days=1)

	while currBegin < lastDate:
		days.append((currBegin, currBegin + datetime.timedelta(days=1)))
		currBegin = currBegin + datetime.timedelta(days=1)

	# print weeks[-1][1]
	# weeks[-1] = (weeks[-1][0], StudentLifeEnd)

	return days




############## WEEKLY MOEVEMENT ANALYSIS ################

def getWeeklyDataForSubject(subject, frequency):
	weeksData = {}
	
	firstDate = extractor.getFirstDayDate(subject)

	if firstDate != None:
		lastDate = extractor.getLastDayDate(subject)

		if db == "StudentLife":
			weeks = getWeeks(StudentLifeStart, StudentLifeEnd, frequency)
		else:
			weeks = getWeeks(memotionStart, memotionEnd, frequency)

		for week in weeks:
			weekKey = week[0].strftime("%Y-%m-%d") + "," + week[1].strftime("%Y-%m-%d")

			data = getUserData(['ts', 'lat', 'lon'], subject, (week[0].strftime("%Y-%m-%d"), week[1].strftime("%Y-%m-%d")))

			if len(data) >= 1:
				weeksData[weekKey] = data	
			else:
				print "Removing user " + subject + ". No data for period (" + week[0].strftime("%Y-%m-%d") + "," + week[1].strftime("%Y-%m-%d") + ")"
				return None

		return OrderedDict(sorted(weeksData.items(), key= lambda x: time.strptime(x[0].split(",")[0], "%Y-%m-%d")))

# def memotionWeeklyMovementAnalysis():
# 	global removedUsers


# 	if subject != "all":
		fullData = getData(['ts', 'lat', 'lon'], subject, None)
		fullData = fullData[subject]
		if len(fullData) > 0:
			#array(clusters)
			fullClusters = DBScan.getClusters(fullData)
			#array(stayPoints)
			fullTrace = DBScan.getTraceWithClusters(fullData, fullClusters)
			#week => array(data)
			dailyData = getDailyDataForSubject(subject)
			for p in dailyData:
				print p
			#week => array(metrics)
			metrics = computeMetrics(dailyData, fullClusters, fullTrace)

			if checkMetrics(metrics):
				results = {subject : {'fullData' : fullData, 'data' : weeklyData, 'clusters' : fullClusters, 'fullTrace' : fullTrace, 'metrics' : metrics}}
				goodUsers.append(subject)
			else:

				removedUsers.append(subject)
				return {}
		else:
			removedUsers.append(subject)
			return {}
# 	else:
# 		results = {}
# 		users = extractor.getUsers()

# 		for user in users:
# 			#user => array(data)
# 			fullData = getData(['ts', 'lat', 'lon'], user, None)
# 			fullData = fullData[user]

# 			if len(fullData) > 0:
# 				#array(clusters)
# 				fullClusters = DBScan.getClusters(fullData)
# 				#array(stayPoints)
# 				fullTrace = DBScan.getTraceWithClusters(fullData, fullClusters)
# 				#week => array(data)
# 				weeklyData = getWeeklyDataForSubject(user, 1)
# 				#week => array(metrics)
# 				metrics = computeMetrics(weeklyData, fullClusters, fullTrace)

# 				if checkMetrics(metrics, user):
# 					results[user] = {'fullData' : fullData, 'data' : weeklyData, 'clusters' : fullClusters, 'fullTrace' : fullTrace, 'metrics' : metrics}
# 					goodUsers.append(user)
# 			else:
# 				removedUsers.append(user)
# 				print "Removing user " + user + ". No data."
# 		return OrderedDict(sorted(results.items(), reverse=True))

def studentLifeWeeklyMovementAnalysis(frequency):
	global removedUsers

	if subject != "all":
		a = 1
		# #subject => array(data)
		# fullData = getData(['ts', 'lat', 'lon'], subject, None)
		# fullData = fullData[subject]

		# if len(fullData) > 0:
		# 	#array(clusters)
		# 	fullClusters = DBScan.getClusters(fullData)
		# 	#array(stayPoints)
		# 	fullTrace = DBScan.getTraceWithClusters(fullData, fullClusters)
		# 	#week => array(data)
		# 	weeklyData = getWeeklyDataForSubject(subject, frequency)

		# 	if weeklyData != None:
		# 		#week => array(metrics)
		# 		metrics = computeMetrics(weeklyData, fullClusters, fullTrace)
		# 		print metrics

		# 		if checkMetrics(metrics):
		# 			results = {subject : {'fullData' : fullData, 'data' : weeklyData, 'clusters' : fullClusters, 'fullTrace' : fullTrace, 'metrics' : metrics}}
		# 			goodUsers.append(subject)
		# 		else:
		# 			removedUsers.append(subject)
		# 			return {}
		# else:
		# 	removedUsers.append(subject)
		# 	return {}
	else:
		results = {}
		users = extractor.getUsers()

		for user in users:
			#user => array(data)
			fullData = getData(['ts', 'lat', 'lon'], user, None)
			fullData = fullData[user]

			if len(fullData) > 0:
				#array(clusters)
				fullClusters = DBScan.getClusters(fullData)
				DBScan.getTrace(fullData)
				#array(stayPoints)
				fullTrace = DBScan.getTraceWithClusters(fullData, fullClusters)
				#week => array(data)
				weeklyData = getWeeklyDataForSubject(user, frequency)
				if weeklyData != None:
					for p in weeklyData:
						print p
				if weeklyData != None:
					#week => array(metrics)
					metrics = computeMetrics(weeklyData, fullClusters, fullTrace, user)
					if checkMetrics(metrics, user):
						results[user] = {'fullData' : fullData, 'data' : weeklyData, 'clusters' : fullClusters, 'fullTrace' : fullTrace, 'metrics' : metrics}
						goodUsers.append(user)
			else:
				removedUsers.append(user)
				print "Removing user " + user + ". No data."

		print 'users ' + str(len(results))
		return OrderedDict(sorted(results.items(), reverse=True))

#################### EMA SCORES ######################

def computePHQ9Score(answers):
	s1 = 0
	s2 = 0
	s3 = 0

	for i in range(3, 12):
		if answers[i] == 'Several days':
			s1 = s1 + 1
		elif answers[i] == 'More than half the days':
			s2 = s2 + 2
		elif answers[i] == 'Nearly every day':
			s2 = s3 + 3

	return s1 + s2 + s3

def getStudentLifePHQ9Scores():
	scores = {}

	for user in goodUsers:
		pre = extractor.getStudentLifeUserPHQ9(user, True)
		post = extractor.getStudentLifeUserPHQ9(user, False)
		scores[user] = {}

		if pre != None:
			scores[user]['pre'] = computePHQ9Score(pre)
		else:
			scores[user]['pre'] = None


		if post != None:
			scores[user]['post'] = computePHQ9Score(post)
		else:
			scores[user]['post'] = None

	return OrderedDict(sorted(scores.items(), reverse=True))

#period => corr
#metrics and scores sorted by user id
def computeCorrelation(metrics, scores, freq):
	# metrics, scores = removeStudentLifeUsers(metrics, scores, freq)
	# print 'metrics ' + str(len(metrics))
	# print 'scores ' + str(len(scores)) 
	preCorrelations = {}
	postCorrelations = {}

	scoresArrays = {}
	scoresArrays['pre'] = []
	scoresArrays['post'] = []

	metricsArrays = {}
	metricsArrays['total_distance'] = []
	metricsArrays['max_distance'] = []
	metricsArrays['radius_gyration'] = []
	metricsArrays['std_deviation_displacement'] = []
	metricsArrays['max_distance_home'] = []

	for user in scores:
		print user
		scoresArrays['pre'].append(scores[user]['pre'])
		scoresArrays['post'].append(scores[user]['post'])

	totalDistanceArray = {}
	maxDistanceArray = {}
	gyrationArray = {}
	deviationArray = {}
	maxDistanceHomeArray = {}
	differentPlacesArray = {}

	for user in metrics:
		print user
		# print user
		met = metrics[user]
		met = met['metrics']
		for period in met:
			# print h
			if not(period in totalDistanceArray):
				totalDistanceArray[period] = []
				maxDistanceArray[period] = []
				gyrationArray[period] = []
				deviationArray[period] = []
				maxDistanceHomeArray[period] = []
				differentPlacesArray[period] = []
			totalDistanceArray[period].append(met[period]['total_distance'])
			maxDistanceArray[period].append(met[period]['max_distance'])
			gyrationArray[period].append(met[period]['radius_gyration'])
			deviationArray[period].append(met[period]['std_deviation_displacement'])
			maxDistanceHomeArray[period].append(met[period]['max_distance_home'])
			differentPlacesArray[period].append(met[period]['different_places_visited'])


	totalDistanceArray = OrderedDict(sorted(totalDistanceArray.items(), key= lambda x: time.strptime(x[0].split(",")[0], "%Y-%m-%d")))
	maxDistanceArray = OrderedDict(sorted(maxDistanceArray.items(), key= lambda x: time.strptime(x[0].split(",")[0], "%Y-%m-%d")))
	gyrationArray = OrderedDict(sorted(gyrationArray.items(), key= lambda x: time.strptime(x[0].split(",")[0], "%Y-%m-%d")))
	deviationArray = OrderedDict(sorted(deviationArray.items(), key= lambda x: time.strptime(x[0].split(",")[0], "%Y-%m-%d")))
	maxDistanceHomeArray = OrderedDict(sorted(maxDistanceHomeArray.items(), key= lambda x: time.strptime(x[0].split(",")[0], "%Y-%m-%d")))
	differentPlacesArray = OrderedDict(sorted(differentPlacesArray.items(), key= lambda x: time.strptime(x[0].split(",")[0], "%Y-%m-%d")))
	for p in totalDistanceArray:
		print p
	for i in totalDistanceArray:
		if not(i in preCorrelations):
			preCorrelations[i] = {}

		totDist = []
		maxDist = []
		gyr = []
		dev = []
		home = []
		diff = []
		pre = []
		post = []

		# print "-----"
		# print len(scoresArrays['pre'])
		# print len(totalDistanceArray[i])
		for j in range(0, len(scoresArrays['pre'])):
			if scoresArrays['pre'][j] != None:
				pre.append(scoresArrays['pre'][j])
				totDist.append(totalDistanceArray[i][j])
				maxDist.append(maxDistanceArray[i][j])
				gyr.append(gyrationArray[i][j])
				dev.append(deviationArray[i][j])
				home.append(maxDistanceHomeArray[i][j])
				diff.append(differentPlacesArray[i][j])

		print "a"
		print totDist
		print "b"
		print maxDist
		print "c"
		print gyr
		print "d"
		print dev
		print "e"
		preCorrelations[i]['total_distance'] = pearsonr(pre, totDist)
		preCorrelations[i]['max_distance'] = pearsonr(pre, maxDist)
		preCorrelations[i]['radius_gyration'] = pearsonr(pre, gyr)
		preCorrelations[i]['std_deviation_displacement'] = pearsonr(pre, dev)
		preCorrelations[i]['max_distance_home'] = pearsonr(pre, home)
		preCorrelations[i]['different_places_visited'] = pearsonr(pre, diff)

		if not(i in postCorrelations):
			postCorrelations[i] = {}

		totDist = []
		maxDist = []
		gyr = []
		dev = []
		home = []
		diff = []
		pre = []
		post = []

		for j in range(0, len(scoresArrays['post'])):
			if j != 9 and j != 10:
				if scoresArrays['post'][j] != None:
					post.append(scoresArrays['post'][j])
					totDist.append(totalDistanceArray[i][j])
					maxDist.append(maxDistanceArray[i][j])
					gyr.append(gyrationArray[i][j])
					dev.append(deviationArray[i][j])
					home.append(maxDistanceHomeArray[i][j])
					diff.append(differentPlacesArray[i][j])


		postCorrelations[i]['total_distance'] = pearsonr(post, totDist)
		postCorrelations[i]['max_distance'] = pearsonr(post, maxDist)
		postCorrelations[i]['radius_gyration'] = pearsonr(post, gyr)
		postCorrelations[i]['std_deviation_displacement'] = pearsonr(post, dev)
		postCorrelations[i]['max_distance_home'] = pearsonr(post, home)
		postCorrelations[i]['different_places_visited'] = pearsonr(post, diff)

	preCorrelations = OrderedDict(sorted(preCorrelations.items(), key= lambda x: time.strptime(x[0].split(",")[0], "%Y-%m-%d")))

	postCorrelations = OrderedDict(sorted(postCorrelations.items(), key= lambda x: time.strptime(x[0].split(",")[0], "%Y-%m-%d")))

	return (preCorrelations, postCorrelations, scores)

def createAnalysisFolders():
	users = goodUsers

	for u in users:
		if not os.path.exists(output + "/" + u):
			os.makedirs(output + "/" + u)



def weeklyAnalysis():
	global goodUsers
	global frequency
	frequency = 'overlap'
	if db == 'Memotion':
		metrics = memotionWeeklyMovementAnalysis()

		scores = memotionPHQ8Scores()
		correlations = computeCorrelation(metrics, scores, frequency)

		writeOut(metrics, correlations, frequency)
	else:
		#user => dict('fullData' : fullData, 'data' : weeklyData, 'clusters' : fullClusters, 'fullTrace' : fullTrace, 'metrics' : metrics)
		#metrics => dict {'period' => trace, metrics...,
		metrics = studentLifeWeeklyMovementAnalysis(frequency)
		saveResults(metrics)
		# print "========="
		# for m in metrics:
		# 	print m
		scores = getStudentLifePHQ9Scores()
		print scores

		# print "========="
		# for m in scores:
		# 	print m
		correlations = computeCorrelation(metrics, scores, frequency)
		print "Removed users"
		print removedUsers
		print "Good users"
		print goodUsers
		writeOut(metrics, correlations, frequency)


def getDailyDataForSubject(subject):
	print subject
	dailyData = {}
	firstDate = extractor.getFirstDayDate(subject)

	if firstDate != None:
		lastDate = extractor.getLastDayDate(subject)
		days = getDays(firstDate, lastDate)
		print len(days)
		for day in days:
			daysKey = day[0].strftime("%Y-%m-%d") + "," + day[1].strftime("%Y-%m-%d")

			data = getUserData(['ts', 'lat', 'lon'], subject, (day[0].strftime("%Y-%m-%d"), day[1].strftime("%Y-%m-%d")))

			dailyData[daysKey] = data

		return OrderedDict(sorted(dailyData.items(), key= lambda x: time.strptime(x[0].split(",")[0], "%Y-%m-%d")))

def getMemotionMetricsForSubject(subject):
	fullData = getData(['ts', 'lat', 'lon'], subject, None)
	fullData = fullData[subject]
	print fullData
	if len(fullData) > 0:
		
		#array(clusters)
		fullClusters = DBScan.getClusters(fullData)
		#array(stayPoints)
		fullTrace = DBScan.getTraceWithClusters(fullData, fullClusters)
		#week => array(data)
		dailyData = getDailyDataForSubject(subject)

		if dailyData != None:
			#week => array(metrics)
			metrics = computeMetrics(dailyData, fullClusters, fullTrace, subject)

			# if checkMetrics(metrics, user):
			# 	results[user] = {'fullData' : fullData, 'data' : weeklyData, 'clusters' : fullClusters, 'fullTrace' : fullTrace, 'metrics' : metrics}
			# 	goodUsers.append(user)
	else:
		removedUsers.append(user)
		print "Removing user " + user + ". No data."

	return metrics

def writeMetrics(metrics, user):
	totDist = []
	maxDist = []
	gyr = []
	dev = []
	maxDistHome = []
	diff = []
	rev = []

	for period in metrics:
		totDist.append(metrics[period]['total_distance'])
		maxDist.append(metrics[period]['max_distance'])
		gyr.append(metrics[period]['radius_gyration'])
		dev.append(metrics[period]['std_deviation_displacement'])
		maxDistHome.append(metrics[period]['max_distance_home'])
		diff.append(metrics[period]['different_places_visited'])
		rev.append(metrics[period]['relevant_places_visited'])

	Plot.plotMetrics([1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18], [totDist, maxDist, gyr, dev, maxDistHome, diff, rev], ['total_distance', 'max_distance', 'radius_gyration', 'std_deviation_displacement', 'max_distance_home', 'different_places_visited', 'relevant_places_visited'], output + "/" + user )

def saveResults(metrics):
	content = ""
	for user in metrics:
		uMetrics = metrics[user]['metrics']
		periodString = ""
		for period in uMetrics:
			upMetrics = uMetrics[period]
			mString = ""
			for m in upMetrics:
				mString = mString + m + ":" + str(upMetrics[m]) + ","

			periodString = periodString + period + ":{" + mString[0:len(mString)-1] + "}" + ","
		content = content + user + "|" + periodString + "\n"

	content = content[0:len(content)-1]
	f = open(output + "dbScan_metrics.txt", "wb")
	f.write(content)
	f.close()

def dailyMovementAnalysis():
	
	metrics = getMemotionMetricsForSubject(subject)
	writeMetrics(metrics, subject)
	return 

def startAnalysis():
	analysis = None
	global frequency
	frequency = "weekly"
	if frequency == "daily":
		dailyMovementAnalysis()
	else:
		weeklyAnalysis()

	# printMovementAnalysis(analysis)
	# writeOut(analysis)

getCmdArguments()
extractor.useDb(db)
startAnalysis()
		