from flask import Flask,request,make_response,jsonify
from flask_httpauth import HTTPBasicAuth,HTTPTokenAuth
from itsdangerous import TimedJSONWebSignatureSerializer as Serializer
from werkzeug.security import generate_password_hash, check_password_hash
from werkzeug.contrib.fixers import ProxyFix
import os
import mysql.connector

# initialization
app = Flask(__name__)
app.config['SECRET_KEY'] = 'secret key here'
serializer = Serializer(app.config['SECRET_KEY'], expires_in=1800)

basic_auth = HTTPBasicAuth()
token_auth = HTTPTokenAuth(scheme='Bearer')

users = [
	{'username': 'project', 'password': generate_password_hash('project')},
	{'username': 'testing', 'password': generate_password_hash('testing')}
]

# HTTPBasicAuth
@basic_auth.verify_password
def verify_password(username, password):
	for user in users:
		if user['username'] == username:
			if check_password_hash(user['password'], password):
				return True
	return False

@basic_auth.error_handler
def unauthorized():
	return make_response(jsonify( { 'error': 'Unauthorized access' } ), 401)

#HTTPTokenAuth
@token_auth.verify_token
def verify_token(token):
	try:
		data = serializer.loads(token)
	except:
		return False
	if 'username' in data:
		return True
	return False
	
# Connection Test
@app.route("/")
def check():
	return "Connected to API server"

# Connection Test
@app.route("/api/v1.0")
@basic_auth.login_required
def check_basic_auth():
	return "Connected to API server"
	
# Connection Test
@app.route("/api/v1.0/token")
@basic_auth.login_required
def generate_token():
	token = serializer.dumps({'username': 'project'})
	return jsonify({'token': token.decode('ascii'), 'duration': 1800})
	
@app.route("/api/v1.0/find_cheapest", methods=['GET'])
@token_auth.login_required
def find_cheapest():
	# Get parameter from call
	if request.args.get("search") is None : 
		return 'parameter "search" is missing !'
	else : 
		search = request.args.get("search")
		
	source = request.args.get("source", default = "")
	
	# Build a connection to DB
	cnx = mysql.connector.connect(user = 'wing', password = 'qwert12345',host = 'localhost',database = 'project')
	cursor = cnx.cursor()

	# Send query to Get at most 10 item's data with specificed kind which provide lowest price
	query = "Select product, price, url, source, picture, update_time FROM test WHERE product LIKE '%{0}%' AND price in (SELECT MIN(price) as minimum FROM test WHERE product LIKE '%{0}%') ".format(search)
	if source is not "":
		query += "AND source = '{}'".format(source)
	query += "Limit 10"
	cursor.execute(query)
	
	# Get data from cursor
	results = []
	for (product, price, url, source, picture, update_time) in cursor :
		update_time = ("{:%d %b %Y}").format(update_time)
		row = dict(zip(("p_name", "price", "url", "source", "imagePath", "update_time"),(product, price, url, source, picture, update_time)))
		results.append(row)
		
	# Close object
	cursor.close()
	cnx.close()
	
	# Produce and return JSON array
	return jsonify(results)

@app.route("/api/v1.0/price_data", methods=['GET'])
@token_auth.login_required
def price_data():
	# Get parameters from call
	if request.args.get("search") is None : 
		return 'parameter "search" is missing !'
	else : 
		search = request.args.get("search")
		
	# Optional parameters
	unit = request.args.get("unit", default = 100, type = int)
	min = request.args.get("min", default = 0, type = int)
	max = request.args.get("max", default = 1000, type = int)
	source = request.args.get("source", default = "")
	
	# Build a connection to DB
	cnx = mysql.connector.connect(user = 'wing', password = 'qwert12345',host = 'localhost',database = 'project')
	cursor = cnx.cursor()

	# Develop a list of data for query execution
	lower_limit_list = range(max + 1)[min : (max+1) : unit]
	region = len(lower_limit_list)
	
	# Send query to DB
	query = "SELECT COUNT(*) AS num , CASE " 
	for i in range((region - 2)):
		query += "when price >= {0[%d"%i + "]} AND price < {0[%d"%(i+1) + "]} Then {0[%d"%i + "]} "
	query += "when price BETWEEN {0[%d"%(region - 2) + "]} AND {0[%d"%(region - 1) + "]} THEN {0[%d"%(region - 2) + "]} WHEN price > {0[%d"%(region - 1) + "]} THEN {0[%d"%(region - 1) + "]} END AS price_lower_limit FROM test WHERE product LIKE '%{1}%' "
	query = query.format(lower_limit_list,search)
	if source is not "":
		query += "AND source = '{}'".format(source)
	query += "GROUP BY price_lower_limit"

	cursor.execute(query)
	
	# Take out the data from cursor
	results = {}
	for num in cursor : 
		results[num[1]] = num[0]
	
	query = ("SELECT MIN(price), MAX(price), AVG(price) FROM test WHERE product LIKE '%{}%'".format(search))
	cursor.execute(query)
	
	statistic = {}	
	for data in cursor : 
		statistic = dict(zip(('min', 'max', 'avg'),(data[0], data[1], float(data[2]))))
	
	# Close object
	cursor.close()
	cnx.close()
	
	return jsonify({'distribution':results,'statistic':statistic})
	
@app.route("/api/v1.0/prediction", methods=['GET'])
@token_auth.login_required
def predict_data():
	# Get parameters from call
	category = request.args.get("category")
	search = request.args.get("search")
	# Optional parameters
	source = request.args.get("source", default = "")
	
	if category is not None :
		#Build a connection to DB
		cnx = mysql.connector.connect(user = 'wing', password = 'qwert12345',host = 'localhost',database = 'project')
		cursor = cnx.cursor()
		
		query = "SELECT predict_price FROM `predictionTest` WHERE category ='{}' AND predict_date = CURRENT_DATE+1".format(category)
		cursor.execute(query)

		for result in cursor :
			prediction = result[0]

		return jsonify({"category":category,"predict_price":prediction})

	elif search is not None: 
		query = "SELECT DISTINCT kind FROM test WHERE product LIKE '%{0}%' AND price in (SELECT MIN(price) as minimum FROM test WHERE product LIKE '%{0}%'".format(search)
		if source is not "":
			query += "AND source = '{}'".format(source)
		query += ") Limit 5"
		cursor.execute(query)
		possibleCat = [i for i in cursor]
		category = possibleCat[0]
		suggestion = possibleCat[1:]
		
		query = "SELECT predict_price FROM `predictionTest` WHERE category ='{}' AND predict_date = CURDATE()+1".format(category)
		cursor.execute(query)
		
		prediction = [result for result in cursor]
		
		output = {"category":category,"predict_price":prediction,"suggest_type":suggestion}
		
		return jsonify(output)

# API for SPRINT 2
@app.route("/api/find_cheapest", methods=['GET'])
def sprint2_find_cheapest():
	#Get parameter from call
	if request.args.get("kind") is None : 
		return 'parameter "kind" is missing !'
	else : 
		kind = request.args.get("kind")
	
	#Build a connection to DB
	cnx = mysql.connector.connect(user = 'wing', password = 'qwert12345',host = 'localhost',database = 'project')
	cursor = cnx.cursor()

	#Send query to Get at most 10 item's data with specificed kind which provide lowest price
	query = (("SELECT product, price, url, website, picture, update_time FROM test WHERE kind='%s' AND price IN (SELECT MIN(price) AS minimum FROM test WHERE kind = '%s') LIMIT 10") % (kind, kind))
	cursor.execute(query)
	
	#Get data from cursor
	results = []
	for (product, price, url, website, picture, update_time) in cursor :
		update_time = ("{:%d %b %Y}").format(update_time)
		row = dict(zip(("p_name", "price", "url", "source", "imagePath", "update_time"),(product, price, url, website, picture, update_time)))
		results.append(row)
		
	#Close object
	cursor.close()
	cnx.close()
	
	#Produce and return JSON array
	return jsonify(results)

@app.route("/api/price_data", methods=['GET'])
def sprint2_price_data():
	#Get parameters from call
	if request.args.get("kind") is None : 
		return 'parameter "kind" is missing !'
	else : 
		kind = request.args.get("kind")

	unit = request.args.get("unit", default = 100, type = int)
	min = request.args.get("min", default = 0, type = int)
	max = request.args.get("max", default = 1000, type = int)
	
	#Build a connection to DB
	cnx = mysql.connector.connect(user = 'wing', password = 'qwert12345',host = 'localhost',database = 'project')
	cursor = cnx.cursor()

	#Develop a list of data for query execution
	query_list = []
	lower_limit_list = range(max + 1)[min : (max+1) : unit]
	region_num = len(lower_limit_list)
	for i in lower_limit_list :
		query_list.append(i)
		if i != max :
			query_list.append(i+unit)
		query_list.append(i)

	query_list.append(kind)
	
	#Send query to DB
	sql_string = "SELECT COUNT(*) AS num , CASE " + "WHEN price >= %d AND price < %d THEN %d " * (region_num - 2) + "WHEN price BETWEEN %d AND %d THEN %d WHEN price > %d THEN %d END AS price_lower_limit FROM test WHERE kind = '%s' GROUP BY price_lower_limit"

	query = (sql_string % tuple(query_list))
	cursor.execute(query)
	
	#Take out the data from cursor
	results = {}
	for num in cursor : 
		results[num[1]] = num[0]
	
	#Close object
	cursor.close()
	cnx.close()
	
	return jsonify(results)

app.wsgi_app = ProxyFix(app.wsgi_app)
if __name__ == "__main__" :
	app.run(host=os.getenv('IP', '0.0.0.0'),port=int(os.getenv('PORT', 5000)))