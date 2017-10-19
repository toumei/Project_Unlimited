import requests
from bs4 import BeautifulSoup
import mysql.connector
from datetime import datetime
import re

def crawling_rate() :
	res = requests.get("http://www.google.com/finance?q=TWDCNY")
	soup = BeautifulSoup(res.text, "lxml")
	rawData = soup.select('.bld')[0]
	rate = re.search('(.*?) CNY', rawData.text)
	return (rate.group(1))

def get_current_rate() : 
	#Build a connection to DB
	cnx = mysql.connector.connect(user = 'wing', password = 'qwert12345',host = 'localhost',database = 'project')
	cursor = cnx.cursor()

	#Send an query to DB looking for today's currency exchangr rate (TWD:RMB)
	query = ("SELECT rate FROM exchangeRate WHERE update_time = '%s' ") % (datetime.now().date())
	cursor.execute(query)

	rate = cursor.fetchall()
	if rate == [] :
		#Crawl data from Google Finance
		rate = crawling_rate()

		#Insert rate to DB
		sql = ("Insert Into exchangeRate (rate, update_time) Values (%s,NOW())") % rate
		cursor.execute(sql)
		cnx.commit()
		
		#Convert rate from string to float
		rate = float(rate)
	else :
		rate = rate[0][0]

	#Close object
	cursor.close()
	cnx.close()

	return (rate)

if __name__ == '__main__':
	crawling_rate()