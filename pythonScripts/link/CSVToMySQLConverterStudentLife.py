# This ad-hoc script reads data from csv files and insert it by executing sql queries to the specified mysql db. 
# The target dataset is StudentLife, which is organized in the following way:
# dataset
#    |-user_info.csv
#    |-sensing
#   	 |-activity
#   	 |-audio
#   	 |-conversation
#   	 |-bluetooth
#   	 |-dark
#   	 |-gps
#   	 |-phonecharge
#	   	 |-phonelock
#   	 |-wifi
#	   	 |-wifi_location
#    |-EMA
#    |-education
#    |-survey
#
# We are particularly interested in gps and wifi_location data. The script assumes that the database 
# already contains a table listing all the users and all the tables we want to load the data in. 
# Assume we want to load the data contained in <data_directory_name> directory, the database must 
# contain a table called  <data_directory_name> with all the  necessary columns (first row of each 
# csv file) plus two extra columns called "id_<data_directory_name>" and "user_id", respectively.
# The first column is the primary key of each record, while the second is the foreign key containing
# the primary key of the user table. 
# 
# SELECT count(*) as Counter, user_id as User, DATE_FORMAT(time, '%Y-%m-%d') as Day FROM `gps` GROUP BY user_id, DATE_FORMAT(time, '%Y-%m-%d')

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
			"    <db_information>: comma-separated list with the following field <db_host, db_port, db_user, db_user_password, db_name>\n" +
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

# checks whether the file represented by the given path is csv
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
		processDataDirectory(dataDirpath)

# augment the given row record by adding the primary key id_<table_name> 
# and the foreign key to the users table
def getRecord(row, userID, index):
	row.insert(0, userID)
	row.insert(0, index)
	return row

# process the data file 
def processDataFile(path, userID, index):
	print "Processing file " + path
	# get the csv reader
	reader = getCsvReader(path)
	# get column names (first row)
	columns = reader.next()
	# extract the table name from the data directory name
	tableName = path[path.rfind("/")+1:path.rfind("_")]
	# add to the columns the primary key id_<table_name> and the foreign key to users table
	columns = ["id_" + tableName, "user_id"] + columns
	# get the position of the time column
	timePosition = columns.index('time')

	records = []
	# collect each record in a big list
	for row in reader:
		# consider only not empty rows
		if len(row) > 1:
			# insert in the records list the current record. Remove last element since it is parsed
			# as an empty column (each row finishes with ',')
			if len(row[len(row)-1]) == 0:
				row = row[:len(row)-1]
			records.insert(len(records), formatRecord(timePosition, row, userID, index))
			# increase the primary key
			index = index + 1
		if len(records) >= 1000:
			dbHandler.insertMany(tableName, columns, records, 1)
			records = []
	# insert all the records of the given file
	dbHandler.insertMany(tableName, columns, records, 1)
	return index

# format the record to convert the long time representation to time stamp
def formatRecord(timePosition, row, userID, index):
	row = getRecord(row, userID, index)
	time = row[timePosition]
	time = datetime.datetime.fromtimestamp(float(time)).strftime('%Y-%m-%d %H:%M:%S')
	row[timePosition] = time
	return row

# process data directory by inserting the data of each data file into db
def processDataDirectory(path):
	# get all file names
	dataFileNames = os.listdir(path)
	# the first record primary key starts from 0
	index = 0
	for dataFileName in dataFileNames:
		dataFilePath = path + "/" + dataFileName
		if checkExtension(dataFilePath):
			# get the userID from the file name and add one since the users table starts from 1
			userID = int(dataFileName[dataFileName.rfind(".")-2:dataFileName.rfind(".")])+1
			# process the data file and get the primary key of the last inserted record + 1
			index = processDataFile(dataFilePath, userID, index)

getCommandLineArgs()
processDataDirectories()