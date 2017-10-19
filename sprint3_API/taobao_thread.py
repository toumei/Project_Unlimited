#爬蟲
import requests
from bs4 import BeautifulSoup
import json
import re

#Multi-processing
from multiprocessing.dummy import Pool as ThreadPool

#連接資料庫
import mysql.connector

import time
import math

from exchange_rate import get_current_rate
from hanziconv import HanziConv

def taobao_3C_crawler(search):
	sortParam = ["", "default", "renqi-desc", "sale-desc", "credit-desc", "price-asc", "price-desc", "total-asc", "total-desc"]
	taobaoUrl = "https://s.taobao.com/list?vlist=1&app=vproduct&cps=yes&cd=false&v=auction&tab=all&from_type=3c&q={0}&sort={1}"
	searchSimp = HanziConv.toSimplified(search) 
	
	url = taobaoUrl.format(searchSimp,sortParam[0])
	firstUrl = requests.get(url)

	dataString = re.search('g_page_config =(.*?)\};', firstUrl.text)
	jsonDict = json.loads(dataString.group(1)+'}')
	totalPage = jsonDict['mods']['pager']['data']['totalPage']
	pageSize = jsonDict['mods']['pager']['data']['pageSize']
	
	searchUrls = [ (taobaoUrl.format(searchSimp,sort) + "&s=" + str(page)) for sort in sortParam for page in range(0,pageSize*totalPage+1,pageSize) ]
	
	pool = ThreadPool(4)
	resource = pool.map(requests.get,searchUrls)
	taobaoWeb = [res.text for res in resource]
	pool.close()
	pool.join()
	
	rate = get_current_rate()
	results = [extract_data(search, rawData, rate) for rawData in taobaoWeb]
	
	return results
	
def extract_data(search, res, rate):
	cleanData = []
	
	dataString = re.search('g_page_config =(.*?)\};', res)
	jsonDict = json.loads(dataString.group(1)+'}')
	usefulData = jsonDict['mods']['itemlist']['data']['auctions']
	
	for item in usefulData : 
		# Convert the title into traditional chinese
		title = HanziConv.toTraditional(item['raw_title'])
		# Convert price into TWD and round-up the price
		price = int(math.ceil( float(item['view_price']) / rate))
		data = (title, search, price, item['detail_url'],"淘寶", item['pic_url'])
		cleanData.append(data)
	
	return cleanData

def insert_data(tableName, fieldName, dataList):
	# Construct the SQL commend
	sql = "INSERT IGNORE INTO " + tableName + "("
	for field in fieldName : 
		if field is not fieldName[len(fieldName)-1]:
			sql += field + ","
		else:
			sql += field + ") VALUES (" + "%s, " * (len(fieldName)-1) + "NOW())"
			
	# Insert data in the list of data
	for data in dataList :
		cursor.execute(sql,data)
	cnx.commit()
	return True

if __name__ == '__main__':
	searchKey = ["手機", "平板電腦", "相機", "單反", "台式機", "3C數碼配件"]
	print ("Start : ",time.asctime( time.localtime()))
	
	insertionData = [taobao_3C_crawler(search) for search in searchKey]
	
	print("web crawling finish & insertion start : ", time.asctime( time.localtime()))
	
	# Build a connection to DB
	cnx = mysql.connector.connect(user = 'wing', password = 'qwert12345',host = 'localhost',database = 'project')
	cursor = cnx.cursor()

	# Insert to DB
	tableName = "testingMulti"
	fieldName = ["product", "category", "price", "url", "source", "picture", "update_time"]
	[insert_data(tableName,fieldName,data) for data in insertionData[0]]

	# Close object
	cursor.close()
	cnx.close()
	
	print("Taobao Crawling Success : " , time.asctime( time.localtime()))