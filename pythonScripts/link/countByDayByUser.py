from Database_Handler import Database_Handler
import plotly.plotly as py
import plotly.graph_objs as go
import sys
from datetime import date, datetime, timedelta
from Plotter import BarChartPlotter
from Plotter import DotChartPlotter
import DataExtractor as extractor
import Plot


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
	query = "SELECT count(*) as Counter, user_id as User, DATE_FORMAT(time, '%Y-%m-%d') as Day FROM `" + tableName + "` WHERE user_id = " + str(userID) + " GROUP BY user_id, DATE_FORMAT(time, '%Y-%m-%d')"
	print query
	result = dbHandler.select(query)
	return result

def executeQueryForTimes():
	query = "SELECT max(time), min(time) FROM `gps`"
	result = dbHandler.select(query)
	return result[0][1].strftime("%Y-%m-%d"), result[0][0].strftime("%Y-%m-%d")

def checkDate(date, data):
	for d in data:
		if date == d[2]:
			return d[0]
	return 0	

def buildDaysAxis(fromDate, toDate):
	 return [result.strftime("%Y-%m-%d") for result in perdelta(datetime.strptime(fromDate, "%Y-%m-%d"), datetime.strptime(toDate, "%Y-%m-%d"), timedelta(days=1))]

def buildCountAxis(userData, daysAxis):
	return [checkDate(date, userData) for date in daysAxis]
	# return [{"value" : checkDate(date, userData), "color" : "black"} for date in daysAxis]

def plotChart():
	fromDate, toDate = executeQueryForTimes()
	daysAxis = buildDaysAxis(fromDate, toDate)
	userCount = dbHandler.select("SELECT COUNT(*) FROM `users`")
	userCount = userCount[0][0]

	if mode == "all":
		for i in range(1, 61):
			print "processing user " + str(i-1)
			countAxis = executeQuery(i)
			plotter = BarChartPlotter(i-1, tableName, daysAxis, countAxis, savePath, format)
			plotter.plot()
	elif mode == "grouped":
		data = []
		u = extractor._getUsers()
		users = []
		print daysAxis
		for i in range(1, userCount+1):
			print "porcessing user" + str(i)
			users.append(u[i])
			d = executeQuery(i)
				

			data.append(buildCountAxis(d, daysAxis))

		Plot.plotDataCountHeatMap(users, daysAxis, data)
		# data = []
		# for i in range(1, 61):
		# 	print "porcessing user" + str(i)
		# 	data.insert(i-1, buildCountAxis(executeQuery(i), daysAxis))
		# plotter = DotChartPlotter(tableName, daysAxis, data, savePath, format)
		# plotter.plot()

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


extractor.useDb('StudentLife')
getCommandLineArgs()
plotChart()

