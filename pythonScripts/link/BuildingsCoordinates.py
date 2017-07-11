from Database_Handler import Database_Handler
import sys

dbInformation = None
dbHandler = None
user = 53

def getCmdArguments():
	global dbInformation
	global dbHandler

	cmdArguments = sys.argv
	dbInformation = cmdArguments[1].split(",")
	dbHandler = Database_Handler(dbInformation[0], int(dbInformation[1]), dbInformation[2], dbInformation[3], dbInformation[4])

def getWifiUserData():
	query = "SELECT * FROM `wifi_location` WHERE user_id = " + str(user) + " and location LIKE 'in%'"
	return dbHandler.select(query)

def getInBuildingsRecords(data):
	inData = []
	for record in data:
		if record[3].startswith("in"):
			inData = inData + [record]
	return inData

def inferCoordinates(data):
	for record in 

def getBuildingsCoordinates():
	wifiData = getWifiUserData()
	print wifiData



getCmdArguments()
getBuildingsCoordinates()
