import os
import sys
import csv
import datetime
from Database_Handler import Database_Handler
import DataExtractor as extractor


dbInformation = None
rootDirPath = None
dataFile = "PHQ-9.csv"
dbHandler = None
currentDb = 'StudentLife'

phq9Columns = ['id_phq9', 'user_id', 'type', 'question1', 'question2', 'question3', 'question4', 'question5', 'question6', 'question7', 'question8', 'question9', 'response']

dbsInfo = {
	'StudentLife': {
		'IP': '127.0.0.1',
		'port': '8080',
		'name': 'StudentLife_locations',
		'user': 'root',
		'password': ''
	},
}

def getCommandLineArgs():
	global rootDirPath
	global dbHandler

	if len(sys.argv) < 2:
		sys.exit("usage: SurveyCSVtoMySQLConverterStudentLife.py <path_to_root_data_dir>")

	rootDirPath = sys.argv[1]
	dbHandler = Database_Handler(dbsInfo[currentDb]['IP'], int(dbsInfo[currentDb]['port']), dbsInfo[currentDb]['user'], dbsInfo[currentDb]['password'], dbsInfo[currentDb]['name'])


def extract():
	path = rootDirPath + "/" + dataFile

	reader = open(path, "r")
	content = reader.read()

	content = content.split("\n")
	content = content[0:len(content)-1]
	
	content = content[1:]

	for c in content:
		c = c.split(",")

		record =['']
		uId = c[0]
		uId = int(uId[uId.find("u")+1:]) + 1
		type = c[1]
		answers = c[2:]

		record.append(uId)
		record.append(type)
		
		for a in answers:
			if a.find("\r") > 0:
				a = a[:-1]
			record.append(a)
		print phq9Columns
		print record
		dbHandler.insert("phq9", phq9Columns, record, 1)
		record = []

getCommandLineArgs()
extract()





















