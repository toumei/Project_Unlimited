from datetime import date
from pony.orm import *
import logging

logging.basicConfig(filename='ponyAPI.log', format='%(asctime)s %(levelname)-8s %(message)s', level=logging.DEBUG)

db = Database()

db.bind(provider='mysql', host='localhost', user='', passwd='', db='project')

sql_debug(True)
	
class ViewAll (db.Entity):
	_table_ = 'viewAll'
	id = PrimaryKey(int)
	product = Required(str)
	category = Required(str)
	price = Required(int)
	url = Required(str)
	source = Required(str)
	picture = Required(str)
	update_time = Required(date)

class User (db.Entity):
	_table_ = 'user'
	id = PrimaryKey(int, auto =True)
	username = Required(str)
	password = Required(str)
	
class RMB_rate (db.Entity):
	_table_ = 'exchangeRate'
	rate = Required(float)
	update_time = Required(date)

db.generate_mapping()
	
