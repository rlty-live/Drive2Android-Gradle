#!/usr/bin/python

#author: deea nitescu
#modified by: jerome caudoux

# Android Strings parser/creator

#Creates the Localizable strings folders and files based on a csv file (compatible with Google sheets)
# The file should look like this:
# code,fr,en, ***another country iso code***, etc
# /* Login */,/* Login */,/* Login */
# this_is_my_key,the is my value in english,this is my value in french,this is my value in another language,etc
#
# This script automatically creates all necessary folders : values-fr, values-en or values-***ANOTHER COUNTRY ISO CODE***
# It fills each folder values-* with the corresponding string.xml filled with the translated strings.

import csv
import glob
import sys
import os

reload(sys)
sys.setdefaultencoding('utf8')

if len(sys.argv) != 2:
	print "Usage: " + sys.argv[0] + " FILE.csv"
	sys.exit()

with open(sys.argv[1], 'rb') as csvfile:
	spamreader = csv.reader(csvfile, delimiter=',', quotechar='"')
	sfs = []
	headers = []
        
	for head in spamreader:
		for i in range(1, len(head)):
			sfs.append("")
			headers.append(head[i].lower())
		break

	for row in spamreader:
		if row[0][0] == '/':
			print("this is a comment")
			for i in range(1,len(row)):
				sfs[i-1] += "\n\n" + row[0] + "\n"
		else:
			for i in range(1, len(row)):
                                key = row[0]
                                value = row[i]
                                print key + " -> " + value
                                if ("%" in value):
				        sfs[i-1] += "<string formatted=\"false\" name=\"" + key + "\">\"" + value + "\"</string>\n"
                                else:
                                        sfs[i-1] += "<string name=\"" + key + "\">\"" + value + "\"</string>\n"
                                        
	i=0
	for sf in sfs:
                filename = headers[i] + "/strings.xml"
                if not os.path.exists(os.path.dirname(filename)):
                        try:
                                os.makedirs(os.path.dirname(filename))
                        except OSError as exc: # Guard against race condition
                                if exc.errno != errno.EEXIST:
                                        raise
		f = open(filename, 'w' )

                sfs[i] = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n" + sfs[i] + "</resources>"
		f.write(sfs[i])
		f.close()
		i=i+1
