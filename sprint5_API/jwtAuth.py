# Reference : https://pythonhosted.org/Flask-JWT/

from flask_jwt import JWT, jwt_required, current_identity
from werkzeug.security import generate_password_hash, check_password_hash
from db_orm import User

def authenticate(username, password):
	user = User.get(username = username)
	if check_password_hash(user.password, password):
		return user

def identify(payload):
	return User.get(id = payload['identity'])	
