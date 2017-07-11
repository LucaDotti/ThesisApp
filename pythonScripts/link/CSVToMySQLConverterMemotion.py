import os
import sys
import csv
import datetime
from Database_Handler import Database_Handler

dbInformation = None
rootDirPath = None
dataDirs = None
dbHandler = None

def getCommandLineArgs():
	global dbInformation
	global rootDirPath
	global dataDirs
	global dbHandler

	cmdArguments = sys.argv
	cmdArgumentsLenght = len(sys.argv)

	if cmdArgumentsLenght < 4  :
		sys.exit("usage: CSVToMySQLConverter <db_information> <path_to_root_data_dir> <dir_names>\n" + 
			"    <db_information>: comma-separated list with the following fields <db_host, db_port, db_user, db_user_password, db_name>\n" +
			"    <path_to_root_data_dir>: path to root directory\n" +
			"    <dir_names>: comma-separated list of the name of the directories where their data must be extracted")
	
	# get db information used to open the connection
	dbInformation = cmdArguments[1].split(",")
	# the path of root directory containing the data directories
	rootDirPath = cmdArguments[2]
	# names of the data directories we want to extract the data from
	dataDirs = cmdArguments[3].split(",")
	# the handler to perform the queries to populate the MySQL db
	dbHandler = Database_Handler(dbInformation[0], int(dbInformation[1]), dbInformation[2], dbInformation[3], dbInformation[4])

def getUsers():
	dataDirs = os.listdir(rootDirPath)
	users = {}
	for d in dataDirs:
		if os.path.isdir(rootDirPath + "/" + d):
			usrs = os.listdir(rootDirPath + "/" + d)
			for u in usrs:
				users[u[0:u.find(".")]] = 1

	return users

def insertUsers():
	result = dbHandler.select("SELECT COUNT(*) FROM users")
	if result[0][0] == 0:
		users = getUsers()
		records = []
		record = []
		i = 1
		for user in users:
			record.append(str(i))
			record.append(user)
			records.append(record)
			record = []
			i = i + 1
		
		dbHandler.insertMany("users", ["id_user","uid"], records, 1)

def checkExtension(path):
	fileName, fileExtension = os.path.splitext(path)
	return fileExtension == ".csv"

# get the csv reader of the file represented by the given path
def getCsvReader(path):
	csvFile = open(path, "rb");
	return csv.reader(csvFile, delimiter=',')

# process each data directory
def processDataDirectories():
	for directory in dataDirs:
		# concat the root directory path with the name of the data directory
		dataDirpath = rootDirPath + "/" + directory
		processDataDirectory(directory, dataDirpath)

# augment the given row record by adding the primary key id_<table_name> 
# and the foreign key to the users table
def getRecord(row, userID, index):
	# print userID
	c = dbHandler.select("SELECT id_user FROM `users` WHERE uid= \"" + userID + "\"")
	row.insert(0, c[0][0])
	row.insert(0, index)
	return row

# process the data file 
def processDataFile(directory, path, userID, index):
	print "Processing file " + path
	# get the csv reader
	# reader = getCsvReader(path)
	reader = open(path, "r")
	content = reader.read()
	content = content.split("\n")
	content = content[0:len(content)-1]
	
	# get column names (first row)
	columns = content[0]
	content = content[1:]

	
	# extract the table name from the data directory name
	tableName = directory
	# add to the columns the primary key id_<table_name> and the foreign key to users table
	columns = columns.split(",")
	columns = columns[1:]

	columns = ["id_" + tableName, "user_id"] + columns
	columns[2] = "time"
	

	# get the position of the time column
	timePosition = columns.index('time')

	records = []


	# print content
	# collect each record in a big list
	for i in range(0, len(content)):
		row = content[i]
		# print row
		row = row.split(",")
		row = row[1:]

		# consider only not empty rows
		if len(row) > 1:
			# insert in the records list the current record. Remove last element since it is parsed
			# as an empty column (each row finishes with ',')
			# if len(row[len(row)-1]) == 0:
			# 	row = row[:len(row)-1]

			rec = formatRecord(timePosition, row, userID, index)
			records.insert(len(records), rec)
			# increase the primary key
			index = index + 1
		if len(records) >= 1000:
			dbHandler.insertMany(tableName, columns, records, 1)
			records = []

	print records
	# insert all the records of the given file
	dbHandler.insertMany(tableName, columns, records, 1)
	return index

# format the record to convert the long time representation to time stamp
def formatRecord(timePosition, row, userID, index):
	
	row = getRecord(row, userID, index)

	time = row[timePosition]
	
	row[timePosition] = datetime.datetime.fromtimestamp(float(int(time)/1000)).strftime('%Y-%m-%d %H:%M:%S')

	return row

# process data directory by inserting the data of each data file into db
def processDataDirectory(directory, path):
	# get all file names
	dataFileNames = os.listdir(path)
	# the first record primary key starts from 0
	index = 0
	for dataFileName in dataFileNames:
		dataFilePath = path + "/" + dataFileName
		if checkExtension(dataFilePath):
			# get the userID from the file name and add one since the users table starts from 1
			userID = dataFileName[0:dataFileName.find(".")]
			# process the data file and get the primary key of the last inserted record + 1
			index = processDataFile(directory, dataFilePath, userID, index)


getCommandLineArgs()
insertUsers()
processDataDirectories()
