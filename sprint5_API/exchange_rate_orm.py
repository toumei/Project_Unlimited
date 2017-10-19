import requests
from bs4 import BeautifulSoup
import mysql.connector
from datetime import datetime
import re

from db_orm import *

def crawling_rate() :
	res = requests.get("http://www.google.com/finance?q=TWDCNY")
	soup = BeautifulSoup(res.text, "lxml")
	rawData = soup.select('.bld')[0]
	rate = re.search('(.*?) CNY', rawData.text)
	return (rate.group(1))

def get_current_rate() : 
	with db_session : 
		result = select(i for i in RMB_rate if i.update_time == date.today()).first()
		
		if (result is None):
			rate = crawling_rate()
			RMB_rate(rate = float(rate))
		else:
			rate = float(result.rate)
	return(rate)

if __name__ == '__main__':
	crawling_rate()