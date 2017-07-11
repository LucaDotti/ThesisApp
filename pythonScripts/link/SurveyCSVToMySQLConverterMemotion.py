import os
import sys
import csv
import datetime
from Database_Handler import Database_Handler
import DataExtractor as extractor

pams = {}
pwbs = {}
terms = {}

users = extractor.getUsers()

dbInformation = None
rootDirPath = None
dataDirs = ['pam', 'pwb', 'termSurvey']
dbHandler = None
currentDb = 'Memotion'

dbsInfo = {
	'Memotion': {
		'IP': '127.0.0.1',
		'port': '8080',
		'name': 'Memotion',
		'user': 'root',
		'password': ''
	},
}

pamColumns = ["id_pam", "user_id", "ts", "completed", "pam_period", "pam_image_id", "pam_q_stress", "pam_q_sleep", "pam_q_location", "pam_q_transportation", "pam_q_activities", "pam_q_workload", "pam_q_social"]
pwbColumns = ["id_pwb", "user_id", "ts", "completed", "pwb_question1", "pwb_question2", "pwb_question3", "pwb_question4", "pwb_question5", "pwb_question6", "pwb_question7", "pwb_question8"]
termColumns = ["id_term", "user_id", "ts", "completed", "shs_question1", "shs_question2", "shs_question3", "shs_question4", "pss_question1", "pss_question2", "pss_question3", "pss_question4", "pss_question5", "pss_question6", "pss_question7", "pss_question8", "pss_question9", "pss_question10", "phq8_question1", "phq8_question2", "phq8_question3", "phq8_question4", "phq8_question5", "phq8_question6", "phq8_question7", "phq8_question8", "swls_question1", "swls_question2", "swls_question3", "swls_question4", "swls_question5"]

def getCommandLineArgs():
	global rootDirPath
	global dbHandler

	if len(sys.argv) < 2:
		sys.exit("usage: SurveyCSVtoMySQLConverterMemotion.py <path_to_root_data_dir>")

	rootDirPath = sys.argv[1]
	dbHandler = Database_Handler(dbsInfo[currentDb]['IP'], int(dbsInfo[currentDb]['port']), dbsInfo[currentDb]['user'], dbsInfo[currentDb]['password'], dbsInfo[currentDb]['name'])

def processDataFile(user, path):
	global pams
	global pwbs
	global terms
	reader = open(path, "r")
	content = reader.read()

	content = content.split("\n")
	content = content[0:len(content)-1]
	
	content = content[1:]
	isPam = False
	d = None
	for c in content:
		c = c.split(",")
		c = c[:-1]
		t = c[7]
		t = t.strip()
		id = c[0]

		if t == 'pam':
			d = pams[user]
			isPam = True
		elif t == 'pwb':
			d = pwbs[user]
		elif t == 'grouped_sspp':
			d = terms[user]


		time = c[1].strip()
		
		if time != '0' and not(time in d):
			print time
			if isPam:
				record = formatPamRecord(c, user)
			else:
				record = formatRecord(c, user)
			d[time] = record

		isPam = False
		d = None

def formatPamRecord(row, user):
	timePosition = 1
	completedPosition = 3
	answersPosition = 9

	answers = row[9:]

	completed = row[completedPosition].strip()

	time = row[timePosition]
	
	time = datetime.datetime.fromtimestamp(float(int(time)/1000)).strftime('%Y-%m-%d %H:%M:%S')

	record = ["", str(users[user[0:user.rfind(".")]]), time, True]

	for a in answers:
		a = a.strip()
		if a == "no answer" or a == "null":
			record.append("")
		else:
			record.append(a)

	if len(record) != len(pamColumns):
		diff = len(record) - len(pamColumns)
		r1 = record[:9]
		r2 = record[9:9+diff]
		r3 = record[10+diff:]

		s = ""
		for ii in r2:
			s = s + ii + ","
		s = s[0:len(s)-1]

		return r1 + [2] + r3

	return record
	

def formatRecord(row, user):
	timePosition = 1
	completedPosition = 3
	answersPosition = 9

	answers = row[9:]

	completed = row[completedPosition].strip()

	time = row[timePosition]
	
	time = datetime.datetime.fromtimestamp(float(int(time)/1000)).strftime('%Y-%m-%d %H:%M:%S')

	record = ["", str(users[user[0:user.rfind(".")]]), time, completed]


	for a in answers:
		a = a.strip()
		if a == "no answer" or a == "null":
			record.append("")
		else:
			record.append(a)

	return record

def transfertData():
	records = []

	

	for user in pams:
		d = pams[user]

		for i in d:
			d[i][0] = ''
			dbHandler.insert("pam", pamColumns, d[i], 1)

	for user in pwbs:
		d = pwbs[user]
		for i in d:
			d[i][0] = ''
			dbHandler.insert("pwb", pwbColumns, d[i], 1)

	print terms["240262d85b37317c.csv"]
	print "---"
	for user in terms:
		d = terms[user]
		for i in d:
			d[i][0] = ''
			dbHandler.insert("term", termColumns, d[i], 1)

def extract():
	global pams
	global pwbs
	global terms

	for directory in dataDirs:
		path = rootDirPath + "/" + directory
		dataFileNames = os.listdir(path)

		for user in dataFileNames:
			if not(user in pams):
				pams[user] = {}
			if not(user in pwbs):
				pwbs[user] = {}
			if not(user in terms):
				terms[user] = {}

			processDataFile(user, path + "/" + user)

	transfertData()


getCommandLineArgs()
extract()