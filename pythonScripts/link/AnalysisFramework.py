import sys
import DataExtractor as extractor
import DBScan as DBScan
from collections import OrderedDict
import os
import MovementMetrics as metrics
import datetime
import time

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

## Destination
## Default pwd
script = os.path.realpath(__file__)
output = script[0: script.rfind("/")]

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

def getUserData(user, cols):
	
	if interval != None:
		return extractor.getUserLocationDataInterval(user, getDate(interval[0], True), getDate(interval[1], False), cols)

	if date != None:
		return extractor.getUserLocationDataInterval(user, getDate(date, True), getDate(date, False), cols)

	if fullStudy:
		return extractor.getUserLocationData(user, cols)
	
def getData(cols):
	data = {}
	if subject != "all":
		data[subject] = getUserData(subject, cols)
	else:
		users = extractor.getUsers()
		for user in users:
			data[user] = getUserData(user, cols)

	return data

def getWeeks(firstDay, lastDay):
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

########################## OUT #########################

def printMovementAnalysis(analysis):
	for user in analysis:
		print "+++++++++++++++++++++++++++"
		print "+++++++++++++++++++++++++++"
		print "Subject: " + user
		print ""
		for a in analysis[user][1]:
			print ""
			print "----" + a + "----"
			for m in analysis[user][1][a]:
				print m + ": " + str(analysis[user][1][a][m])
def writePlots(analysis):
	return

def writeAnalysisResults(analysis):
	return
def writeOut(analysis):
	return

################## MOVEMENT ANALYSIS ###################

def getDailyMovementResults(user, data, days):
	daysTraces = {}
	daysMetrics = {}

	fullTrace = getFullTrace(user)
	# for f in fullTrace:
	# 	print f
	for day in days:
		if not(day in daysMetrics):
			daysMetrics[day] = {}

		trace = DBScan.getTrace(days[day])

		daysTraces[day] = trace
		daysMetrics[day]['total_distance'] = metrics.metricTotalDistance(trace)
		daysMetrics[day]['max_distance'] = metrics.metricMaxDistanceBetweenPoints(trace)
		daysMetrics[day]['radius_gyration'] = metrics.metricRadiusOfGyration(trace)
		daysMetrics[day]['std_deviation_displacement'] = metrics.metricStdDeviationDisplacement(trace)
		daysMetrics[day]['max_distance_home'] = metrics.metricMaximumDistanceFromHome(fullTrace, trace)
		daysMetrics[day]['different_places_visited'] = metrics.metricNumberOfVisitedPlaces(trace)
		daysMetrics[day]['relevant_places'] = metrics.metricRelevantPlaces(fullTrace)

	return OrderedDict(sorted(daysTraces.items())), OrderedDict(sorted(daysMetrics.items())), data

def getDailyMovementAnalysisForUser(user, data):
	days = divideInDays(data)
	return getDailyMovementResults(user, data, days)

def dailyMovementAnalysis():
	usersAnalysis = {}
	data = getData(['ts', 'lat', 'lon'])


	for user in data:
		usersAnalysis[user] = getDailyMovementAnalysisForUser(user, data[user])

	return usersAnalysis

def getWeeklyMovementResults(user, data):
	weeksTraces = {}
	weeksMetrics = {}

	fullTrace = getFullTrace(user)

	for week in data:
		if not(week in weeksMetrics):
			weeksMetrics[week] = {}
		
		if len(data[week]) > 0:
			trace = DBScan.getTrace(data[week])

			weeksTraces[week] = trace
			weeksMetrics[week]['total_distance'] = metrics.metricTotalDistance(trace)
			weeksMetrics[week]['max_distance'] = metrics.metricMaxDistanceBetweenPoints(trace)
			weeksMetrics[week]['radius_gyration'] = metrics.metricRadiusOfGyration(trace)
			weeksMetrics[week]['std_deviation_displacement'] = metrics.metricStdDeviationDisplacement(trace)
			weeksMetrics[week]['max_distance_home'] = metrics.metricMaximumDistanceFromHome(fullTrace, trace)
			weeksMetrics[week]['different_places_visited'] = metrics.metricNumberOfVisitedPlaces(trace)
			weeksMetrics[week]['relevant_places'] = metrics.metricRelevantPlaces(fullTrace)

	return OrderedDict(sorted(weeksTraces.items())), OrderedDict(sorted(weeksMetrics.items())), data

def getWeeklyDataForSubject(user):
	global interval

	weeksData = {}
	firstDate = extractor.getFirstDayDate(user)

	if firstDate != None:
		lastDate = extractor.getLastDayDate(user)
		weeks = getWeeks(firstDate, lastDate)
		for week in weeks:
			weekKey = week[0].strftime("%Y-%m-%d") + "," + week[1].strftime("%Y-%m-%d")
			interval = (week[0].strftime("%Y-%m-%d"), week[1].strftime("%Y-%m-%d"))

			weeksData[weekKey] = getUserData(user, ['ts', 'lat', 'lon'])
		
		return OrderedDict(sorted(weeksData.items(), key= lambda x: time.strptime(x[0].split(",")[0], "%Y-%m-%d")))

def getFullTrace(user):
	fullStudy = True
	interval = None
	data = getData(['ts', 'lat', 'lon'])

	trace = DBScan.getTrace(data[user])
	fullStudy = False
	return trace

def weeklyMovementAnalysis():
	usersAnalysis = {}
	users = extractor.getUsers()

	if subject != "all":
		fullTrace = getFullTrace(subject)
		weeklyData = getWeeklyDataForSubject(subject)
		usersAnalysis[subject] = getWeeklyMovementResults(subject, weeklyData)
	else:
		for user in users:
			weeklyData = getWeeklyDataForSubject(user)
			if weeklyData != None:
				usersAnalysis[user] = getWeeklyMovementResults(user, weeklyData)

	return usersAnalysis

def startAnalysis():
	analysis = None
	if frequency == "daily":
		analysis = dailyMovementAnalysis()
	else:
		analysis = weeklyMovementAnalysis()

	printMovementAnalysis(analysis)
	writeOut(analysis)

getCmdArguments()
extractor.useDb(db)
startAnalysis()
		