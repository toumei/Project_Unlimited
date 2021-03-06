from flask import Flask,request,make_response,jsonify
from werkzeug.contrib.fixers import ProxyFix
from datetime import timedelta,datetime
import os

from db_orm import *
from jwtAuth import *

# initialization
app = Flask(__name__)
app.debug = True

app.wsgi_app = db_session(app.wsgi_app)
app.wsgi_app = ProxyFix(app.wsgi_app)

# JWT Auth
app.config['SECRET_KEY'] = 'secret key here'
app.config['JWT_AUTH_URL_RULE'] = '/api/auth'
app.config['JWT_EXPIRATION_DELTA'] = timedelta(seconds=1800)

jwt = JWT(app, authenticate, identify)

# Phasing keywords into list
def wordPhasing(keywords) : 
	symbol = ['"',"'"]
	for s in symbol :
		if keywords.startswith(s) and keywords.endswith(s):
			keywords = keywords.replace(s, "")
			keywords = [keywords]
			return keywords
	if " " in keywords : 
		keywords = keywords.split(" ")
	else :
		keywords = [keywords]
		
	return (keywords)

@app.route("/api/v1.2/getinfo", methods=['GET'])
def reorganize() : 
	q = request.args.get('q')
	
	if q == 'find_cheapest':
		return (find_cheapest())
	elif q == 'price_data':
		return (price_data())
	elif q == 'get_rmb_rate':
		return (get_rmb_rate())
	elif q == 'demand,price':
		return (demand_price())
	
@app.route("/api/v1.2/demand&price", methods=['GET'])
@jwt_required()
def demand_price():
	# Get parameter from call
	pid = request.args.get("pid")
	cid = request.args.get("cid")
	if pid is None and cid is None : 
		return jsonify("parameter (pid/cid) is missing !")
	elif pid is not None :
		demand = select(i for i in Sales if i.pid == Product[pid]).first()
		demand = demand.to_dict()
		
		price = select(i for i in Prices if i.pid == Product[pid]).first()
		price = price.to_dict()
	else :
		day = ['day1', 'day2', 'day3', 'day4', 'day5', 'day6', 'day7', 'day8', 'day9', 'day10']
		
		result = select((sum(i.day1), sum(i.day2), sum(i.day3), sum(i.day4), sum(i.day5), sum(i.day6), sum(i.day7), sum(i.day8), sum(i.day9), sum(i.day10)) for i in Sales if i.cid == Category[cid])[:]
		demand = dict(zip(day,result[0]))
		
		result = select((sum(i.day1), sum(i.day2), sum(i.day3), sum(i.day4), sum(i.day5), sum(i.day6), sum(i.day7), sum(i.day8), sum(i.day9), sum(i.day10)) for i in Prices if i.cid == Category[cid])[:]
		price = dict(zip(day,result[0]))
	
	return jsonify({'demand':demand, 'price':price})
	
@app.route("/api/v1.2/find_cheapest", methods=['GET'])
@jwt_required()
def find_cheapest():
	# Get parameter from call
	search = request.args.get("search")
	if search is None : 
		return "parameter (search) is missing !"
	
	search = wordPhasing(search)
	source = request.args.get("source", default = "")
	
	query = Product.select()
	for s in search :
		query = query.filter(lambda p : s in p.name)
		
	if source is not "" :
		source = select(a for a in Source if a.name == source).first()
		query = query.filter(lambda p : p.sid == source)
		
	result = query.order_by(Product.price, desc(Product.update_time))[:10]
	
	resultList = [i.to_dict(["pid", "category", "name", "price", "url", "sid", "picture", "update_time"], related_objects=True) for i in result]

	for i in resultList : 
		i['update_time'] = ("{:%Y/%m/%d}").format(i['update_time'])
		i['sid'] = i['sid'].name
		i['source'] = i.pop('sid')
		i['cid'] = i.pop('category')
		i['cid'] = i['cid'].id
	
	# Return JSON array
	return jsonify(resultList)

@app.route("/api/v1.2/price_data", methods=['GET'])
@jwt_required()
def price_data():
	# Get parameters from call
	search = request.args.get("search")
	if search is None : 
		return "parameter (search) is missing !"
	
	search = wordPhasing(search)
	unit = request.args.get("unit", default = 100, type = int)
	min = request.args.get("min", default = 0, type = int)
	max = request.args.get("max", default = 1000, type = int)
	source = request.args.get("source", default = "")
	
	# Develop a list of data for query execution
	lower_limit_list = range(min, max+1, unit)
	region = len(lower_limit_list)
	
	# Send query to DB
	query = "SELECT COUNT(*) , CASE " 
	for i in range(region - 2):
		query += "when price >= {0[%d"%i + "]} AND price < {0[%d"%(i+1) + "]} Then {0[%d"%i + "]} "
	query += "when price BETWEEN {0[%d"%(region - 2) + "]} AND {0[%d"%(region - 1) + "]} THEN {0[%d"%(region - 2) + "]} WHEN price > {0[%d"%(region - 1) + "]} THEN {0[%d"%(region - 1) + "]} END AS price_lower_limit FROM product WHERE "
	query = query.format(lower_limit_list)
	
	for i in range(len(search)):
		if i != 0 :
			query += "AND "
		query += "name LIKE '%{}%' ".format(search[i])
	
	if source is not "":
		source = select(a for a in Source if a.name == source).first()
		query += "AND sid = {} ".format(source.get_pk())
		
	query += "GROUP BY price_lower_limit"
	
	cursor = db.execute(query)
	
	results = {}
	for num in cursor : 
		results[num[1]] = num[0]

	query = ("SELECT MIN(price), MAX(price), AVG(price) FROM product WHERE ")
	for i in range(len(search)):
		if i != 0 :
			query += "AND "
		query += "name LIKE '%{}%' ".format(search[i])
		
	cursor = db.execute(query)
	for data in cursor : 
		stat = dict(zip(('min', 'max', 'avg'),(data[0], data[1], float(data[2]))))
	
	return jsonify({'distribution':results,'statistic':stat})
	
@app.route("/api/v1.2/get_rmb_rate", methods=['GET'])
@jwt_required()
def get_rmb_rate() :
	start = request.args.get("from")
	end = request.args.get("to")
	if start is None or end is None:
		return (jsonify("parameter (from & to) is missing !"))
	start = datetime.strptime(start, '%Y-%m-%d').date()
	end = datetime.strptime(end, '%Y-%m-%d').date()
	
	result = select(i for i in RMB_rate if i.update_time >= start and i.update_time <= end)[:]
	resultList = [i.to_dict(exclude='id') for i in result]
	
	for i in resultList : 
		i['update_time'] = ("{:%Y/%m/%d}").format(i['update_time'])
	
	# Return JSON array
	return jsonify(resultList)
	
@app.route("/api/v1.2/predict", methods=['GET'])
@jwt_required()
def get_prediction() :
	# Get parameters from call
	search = request.args.get("search")
	category = request.args.get("category")
	if search is None and category is None: 
		return "parameter (search / category) is required !"
		
	if category is not None :
		result = select(i for i in Prediction if i.category == category and i.update_time == date.today()).first()
		result = result.to_dict(exclude='id')
		# Return JSON array
		return jsonify(result)
		
	else : 
		search = wordPhasing(search)
		query = Product.select()
		for s in search :
			query = query.filter(lambda p : s in p.name)
			
		cate = query.order_by(Product.price, desc(Product.update_time))[:1]
		for i in cate : 
			cate = i.to_dict('category')

		result = select(p for p in Prediction if p.category == cate['category'] and p.update_time == date.today()).first()
		result = result.to_dict(exclude='id')
		
		categories = select(p.category for p in Product if search in p.name)
		categories = list(categories)
		
		result['sugguest_category'] = categories
	
		# Return JSON array
		return jsonify(result)
	
if __name__ == "__main__" :
	app.run(host = os.getenv('IP', '0.0.0.0'), port = int(os.getenv('PORT', 8080)))