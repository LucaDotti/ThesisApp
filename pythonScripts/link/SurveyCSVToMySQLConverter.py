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
	sys.exit("usage: CSVToMySQLConverter <db_information> <path_to_root_data_dir>")