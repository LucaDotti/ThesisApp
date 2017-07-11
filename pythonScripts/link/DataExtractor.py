from Database_Handler import Database_Handler
import numpy as numpy
import sys

####### Setters #######
def setDbInfo(id, ip, port, name, user, password):
	dbsInfo[id] = {
		'IP': ip,
		'port': port,
		'name': name,
		'user': user,
		'password': password
	}

def setCsvPath(path):
	csvPath = path

def loadUsers():
	global usersIdToName
	global usersNameToId

	usersIdToName = {}
	usersNameToId = {}

	data = dbHandler.select("SELECT * FROM users")

	for d in data:
		usersIdToName[d[0]] = d[1]
		usersNameToId[d[1]] = d[0]

def useDb(name):
	global dbHandler
	global currentDb

	currentDb = name
	dbHandler = Database_Handler(dbsInfo[currentDb]['IP'], int(dbsInfo[currentDb]['port']), dbsInfo[currentDb]['user'], dbsInfo[currentDb]['password'], dbsInfo[currentDb]['name'])
	loadUsers()
	
#######################

####### Database #######
dbsInfo = {
	'Memotion': {
		'IP': '127.0.0.1',
		'port': '8080',
		'name': 'Memotion',
		'user': 'root',
		'password': ''
	},
	'StudentLife': {
		'IP': '127.0.0.1',
		'port': '8080',
		'name': 'StudentLife_locations',
		'user': 'root',
		'password': ''
	},
}

locationColumns = {
	'Memotion': {
		'table': 'location',
		'id': 'id_location',
		'user': 'user_id',
		'ts': 'time',
		'provider': 'provider',
		'lat': 'latitude',
		'lon': 'longitude'
	},
	'StudentLife': {
		'table': 'gps',
		'id': 'id_gps',
		'user': 'user_id',
		'ts': 'time',
		'provider': 'provider',
		'lat': 'latitude',
		'lon': 'longitude'
	},
}

currentDb = None
dbHandler = None
usersIdToName = None
usersNameToId = None
useDb('Memotion')

#######################

######### CSV #########
csvPath = None
#######################

def toMatrix(data):
	data = list(data)

	for i in range(0, len(data)):
		data[i] = list(data[i])
	
	# arr = numpy.array(data)
	return numpy.asarray(data)

def getFirstDayDate(user):
	ts = dbHandler.select("SELECT " + locationColumns[currentDb]['ts'] + " FROM " + locationColumns[currentDb]['table'] + " WHERE " + locationColumns[currentDb]['user'] + " = " + str(usersNameToId[user]) + " ORDER BY " + locationColumns[currentDb]['ts'] + " ASC LIMIT 1")

	if len(ts) != 0:
		return ts[0][0]

def getLastDayDate(user):
	ts = dbHandler.select("SELECT " + locationColumns[currentDb]['ts'] + " FROM " + locationColumns[currentDb]['table'] + " WHERE " + locationColumns[currentDb]['user'] + " = " + str(usersNameToId[user]) + " ORDER BY " + locationColumns[currentDb]['ts'] + " DESC LIMIT 1")

	if len(ts) != 0:
		return ts[0][0]

def getUsers():
	return usersNameToId

def _getUsers():
	return usersIdToName

def getUserLocationData(user, cols = None):
	userId = usersNameToId[user]
	if cols == None:
		select = "SELECT *"
	else:
		select = "SELECT "
		for c in cols:
			select = select + locationColumns[currentDb][c] + " as " + c + ", "
		select = select[0:len(select)-2]
		
	data = dbHandler.select(select + " FROM " + locationColumns[currentDb]['table'] + " WHERE " + locationColumns[currentDb]['user'] + " = " + str(userId) + " ORDER BY ts ASC")

	return toMatrix(data)

def getUserLocationDataInterval(user, tStart, tEnd, cols = None):
	userId = usersNameToId[user]

	if cols == None:
		select = "SELECT *"
	else:
		select = "SELECT "
		for c in cols:
			select = select + locationColumns[currentDb][c] + " as " + c + ", "
		select = select[0:len(select)-2]

	data = dbHandler.select(select + " FROM " + locationColumns[currentDb]['table'] + " WHERE " + locationColumns[currentDb]['user'] + " = " + str(userId) + " AND " + locationColumns[currentDb]['ts'] + " >= \"" + tStart + "\" AND " + locationColumns[currentDb]['ts'] + " <= \"" + tEnd + "\"" + " ORDER BY ts ASC")
	data = toMatrix(data)
	return data

def getStudentLifeUserPHQ9(user, pre):
	if pre:
		p = 'pre'
	else:
		p = 'post'


	userId = usersNameToId[user]
	data = dbHandler.select("SELECT * FROM phq9 WHERE user_id = " + str(userId) + " AND type = \"" + p + "\"")

	if len(data) == 0:
		return None

	return data[0]
