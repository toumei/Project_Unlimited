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
	# return 403 instead of 401 can prevent browsers from displaying the default auth dialog

#HTTPTokenAuth
@token_auth.verify_token
def verify_token(token):
    #g.user = None
    try:
        data = serializer.loads(token)
    except:
        return False
    if 'username' in data:
        #g.user = data['username']
        return True
    return False
	
# Connection Test
@app.route("/v1.0")
@basic_auth.login_required
def check():
	return "Connected to API server"
	
# Connection Test
@app.route("/api/v1.0/token")
@basic_auth.login_required
def generate_token():
	#users = ['John', 'Susan']
	#for user in users:
	#user = 'project'
	token = serializer.dumps({'username': 'project'})
		#print('Token for {}: {}\n'.format(user, token))
	return jsonify({'token': token.decode('ascii'), 'duration': 1800})
	
@app.route("/api/v1.0/find_cheapest", methods=['GET'])
@token_auth.login_required
def find_cheapest():
	# Get parameter from call
	if request.args.get("search") is None : 
		return 'parameter "search" is missing !'
	else : 
		search = request.args.get("search")
	if "+" in search : 
		search = search.split("+")
	
	source = request.args.get("source", default = "")
	
	# Build a connection to DB
	cnx = mysql.connector.connect(user = 'wing', password = 'qwert12345',host = 'localhost',database = 'project')
	cursor = cnx.cursor()

	# Send query to Get at most 10 item's data with specificed kind which provide lowest price
	tmp = ""
	if type(search) is list :
		for i in range(len(search)):
			if i != 0 :
				tmp += "AND "
			tmp += "WHERE product LIKE '%{}%' ".format(search[i])
	else : 
		tmp += "WHERE product LIKE '%{}%' ".format(search)
	
	query = "Select product, price, url, source, picture, update_time FROM test " + tmp + "AND price in (SELECT MIN(price) as minimum FROM test " + tmp
	
	if source is not "":
		query += "AND source = '{}'".format(source)
	query += ") Limit 10"
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
	
	if "+" in search : 
		search = search.split("+")
	
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
	query += "when price BETWEEN {0[%d"%(region - 2) + "]} AND {0[%d"%(region - 1) + "]} THEN {0[%d"%(region - 2) + "]} WHEN price > {0[%d"%(region - 1) + "]} THEN {0[%d"%(region - 1) + "]} END AS price_lower_limit FROM test "
	query = query.format(lower_limit_list)

	if type(search) is list :
		for i in range(len(search)):
			if i != 0 :
				query += "AND "
			query += "WHERE product LIKE '%{}%' ".format(search[i])
	else : 
		query += "WHERE product LIKE '%{}%' ".format(search)
		
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

app.wsgi_app = ProxyFix(app.wsgi_app)
if __name__ == "__main__" :
	app.run(host=os.getenv('IP', '0.0.0.0'),port=int(os.getenv('PORT', 8080)),debug=True)