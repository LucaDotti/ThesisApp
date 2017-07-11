from Database_Handler import Database_Handler
import plotly.plotly as py
import plotly.graph_objs as go
import sys
import os
from datetime import date, datetime, timedelta
from Plotter import BarChartPlotter
from Plotter import DotChartPlotter
import userIdToId as u
import Plot
import DataExtractor as extractor

# plotly.tools.set_credentials_file(username='dottil', api_key='u3929azb0b')
dbInformation = None
mode = None
dbHandler = None
tableName = None
subject = None
savePath = None
format = None

def getCommandLineArgs():
	global dbInformation
	global dbHandler
	global tableName
	global savePath
	global subject
	global mode
	global format

	cmdArguments = sys.argv
	cmdArgumentsLenght = len(sys.argv)
	
	# get db information used to open the connection
	dbInformation = cmdArguments[1].split(",")
	tableName = cmdArguments[2]
	mode = cmdArguments[3]
	if mode != "all" or mode != "grouped":
		subject = mode
	savePath = cmdArguments[4]
	format = cmdArguments[5]
	
	dbHandler = Database_Handler(dbInformation[0], int(dbInformation[1]), dbInformation[2], dbInformation[3], dbInformation[4])

def executeQuery(userID):
	# user = dbHandler.select("SELECT id_user FROM `user` WHERE uid= \"" + str(userID) + "\"")
	# print userID
	query = "SELECT count(*) as Counter, user_id as User, DATE_FORMAT(time, '%Y-%m-%d') as Day FROM `" + tableName + "` WHERE user_id = " + str(userID) + " GROUP BY user_id, DATE_FORMAT(time, '%Y-%m-%d')"
	# print tableName
	result = dbHandler.select(query)

	return result

def executeQueryForTimes():
	query = "SELECT max(time), min(time) FROM `location`"
	result = dbHandler.select(query)
	return result[0][1].strftime("%Y-%m-%d"), result[0][0].strftime("%Y-%m-%d")

def checkDate(date, data):
	for d in data:
		if date == d[2]:
			return d[0]
	return 0	

def buildDaysAxis(fromDate, toDate):
	 return [result.strftime("%Y-%m-%d") for result in perdelta(datetime.strptime(fromDate, "%Y-%m-%d"), datetime.strptime(toDate, "%Y-%m-%d"), timedelta(days=1))]

# def buildCountAxis(userData, daysAxis):
# 	return [{"value" : checkDate(date, userData), "color" : "black"} for date in daysAxis]

def buildCountAxis(userData, daysAxis):
	return [checkDate(date, userData) for date in daysAxis]


def plotChart():
	fromDate, toDate = executeQueryForTimes()
	daysAxis = buildDaysAxis(fromDate, toDate)
	userCount = dbHandler.select("SELECT COUNT(*) FROM `users`")
	userCount = userCount[0][0]
	if mode == "all":
		for i in range(1, userCount):
			print "processing user " + u.getMemotionUserUid(i)
			countAxis = executeQuery(i)
			cAxis = []
			found = False
			for d in daysAxis:
				for c in countAxis:
					if d == c[2]:
						cAxis.append(c[0])
						found = True
				if not(found):
					cAxis.append(0)
				found = False
			plotter = BarChartPlotter("user" + i, tableName, daysAxis, cAxis, savePath, format)
			plotter.plot()
	elif mode == "grouped":
		data = []
		u = extractor._getUsers()
		users = []
		for i in range(1, userCount):
			print "porcessing user" + str(i)
			users.append("user" + str(i))
			d = executeQuery(i)
				

			data.append(buildCountAxis(d, daysAxis))

		Plot.plotDataCountHeatMap(users, daysAxis, data)

	else:
		print "processing user " + str(subject)
		countAxis = buildCountAxis(executeQuery(subject), daysAxis)
		plotter = BarChartPlotter(subject, tableName, daysAxis, countAxis, savePath, format)
		plotter.plot()

# http://stackoverflow.com/questions/10688006/generate-a-list-of-datetimes-between-an-interval
def perdelta(start, end, delta):
    curr = start
    while curr <= end:
        yield curr
        curr += delta


getCommandLineArgs()
if not(os.path.exists(savePath)):
	os.makedirs(savePath)

if not(os.path.exists(savePath + "/" + tableName + "_count")):
	os.makedirs(savePath + "/" + tableName + "_count")

plotChart()