from datetime import date
from datetime import datetime
from pony.orm import *

import logging

logging.basicConfig(filename='sprint5.log', format='%(asctime)s %(levelname)-8s %(message)s', level=logging.DEBUG)

db = Database()

class Crawler_cid(db.Entity):
	sid = Required(int)
	cid = Required(int)
	PrimaryKey(sid, cid)

class Source(db.Entity):
	id = PrimaryKey(int, auto=True)
	name = Required(str)
	products = Set('Product')
	sellers = Set('Seller')

class Category(db.Entity):
	id = PrimaryKey(int, auto=True)
	name = Required(str)
	products = Set('Product')
	prices = Set('Prices')
	saless = Set('Sales')
	
class Product(db.Entity):
	pid = PrimaryKey(int, auto=True)
	name = Required(str)
	price = Required(int)
	sale = Required(int)
	url = Required(str)
	picture = Required(str)
	update_time = Required(date, default=lambda: date.today())
	category = Required(Category)
	sid = Required(Source)
	seller = Required('Seller')
	prices = Optional('Prices')
	sales = Optional('Sales')

class User(db.Entity):
	id = PrimaryKey(int, auto=True)
	username = Required(str)
	password = Required(str)

class Seller(db.Entity):
	id = PrimaryKey(int, auto=True)
	raw_id = Required(int, size=64)
	name = Required(str)
	score = Required(float)
	source = Required(Source)
	products = Set(Product)
	
class RMB_rate (db.Entity):
	_table_ = 'RMB_rate'
	rate = Required(float)
	update_time = Required(date, default=lambda: date.today())

class Sales(db.Entity):
	pid = PrimaryKey(Product)
	cid = Required(Category)
	day1 = Required(int, sql_default=0)
	day2 = Required(int, sql_default=0)
	day3 = Required(int, sql_default=0)
	day4 = Required(int, sql_default=0)
	day5 = Required(int, sql_default=0)
	day6 = Required(int, sql_default=0)
	day7 = Required(int, sql_default=0)
	day8 = Required(int, sql_default=0)
	day9 = Required(int, sql_default=0)
	day10 = Required(int, sql_default=0)

class Prices(db.Entity):
	pid = PrimaryKey(Product)
	cid = Required(Category)
	day1 = Required(int, sql_default=0)
	day2 = Required(int, sql_default=0)
	day3 = Required(int, sql_default=0)
	day4 = Required(int, sql_default=0)
	day5 = Required(int, sql_default=0)
	day6 = Required(int, sql_default=0)
	day7 = Required(int, sql_default=0)
	day8 = Required(int, sql_default=0)
	day9 = Required(int, sql_default=0)
	day10 = Required(int, sql_default=0)
	
db.bind(provider='mysql', host='localhost', user='wing', passwd='qwert12345', db='sprint5')

sql_debug(True)

db.generate_mapping()