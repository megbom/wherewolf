from flask import Flask, request, jsonify
import psycopg2
from wherewolfdao import UserAlreadyExistsException, NoUserExistsException,BadArgumentsException,WherewolfDao
from random import randint
import time
import datetime
import random
import math
app = Flask(__name__)



dao=WherewolfDao("wherewolf","postgres","")

def attack_calc(wolfhp,wolfac, villhp, villac):
    while wolfhp>=0 or villhp>=0:
        wolfattack=randint(0,(wolfac))
        villhp=villhp-wolfattack
        if villhp<=0:
             break
        villattack=randint(0,(villac))
        wolfhp=wolfhp-villattack
        if wolfhp<=0:
            break
    if wolfhp>0:
        return "Wolf"
    else:
        return "Villager"

@app.route('/')
def home_page():
    print 'hello'
    return 'Hello'
@app.route('/healthcheck')
def health_check():
    return 'healthy'
    
@app.route('/v1/register', methods=["POST"])
def create_user():
    print 'server got a registration request'
    username=request.form["username"]
    print username
    password=request.form["password"]
    lastname=request.form["lastname"]
    firstname=request.form["firstname"]
    response={}
    response["status"]="failure"
    if len(password)>6:
            try :
            	dao.create_user(username,password,firstname,lastname)
            	response["status"]="success"
            except UserAlreadyExistsException:
            	response["status"]="failure"

    return jsonify(response)

#def isNight(time):
#if 
#@app.route
@app.route("/v1/get_games", methods=["GET"])
def get_games():
    auth = request.authorization
    username=auth.username
    password=auth.password
    response={}
    response["status"]="failure"

    if not dao.get_games():
        response["status"] ="There are no games"
        response["games"] = "None"
    else:
        response["status"] = "success"
        response["games"] = str(dao.get_games())

    return jsonify(response)
@app.route("/v1/game", methods=["POST"])
def create_game():
    description=request.form["description"]
    game_name=request.form["game_name"]
    auth=request.authorization
    username=auth.username
    password=auth.password
    response={}
    response["status"]="failure"
    if dao.check_password(username, password)==True:
    	try:
    		dao.create_game(username,game_name, description)
    		print "test"
        	response["status"]="success"
        	response[""]
        	return jsonify(response)
    	except:

   			return jsonify(response)
   	return jsonify(response)




@app.route("/v1/game/<game_id>", methods=["DELETE"])
def leave_game(game_id):
	auth=request.authorization
	username=auth.username
	password=auth.password
	response={}
	response["status"]="failure"
	if dao.check_password(username, password):
		dao.leave_game(username)
		response["status"]="success"
		return jsonify(response)
	else:
		return jsonify(response)







@app.route("/v1/game/<game_id>/lobby", methods=["POST"])
def join_game(game_id):
	#print game
	auth=request.authorization
	username=auth.username
	password=auth.password
	response={}
	print password
	response["status"]="failure"
	print dao.check_password(username, password)
	if dao.check_password(username, password)==True:
		print 5
		try:
			dao.join_game(username,int(game_id))
			response["status"]="success"
			return jsonify(response)
		except:
			return jsonify(response)
	else:
		return jsonify(response)


@app.route( '/v1/game/<game_id>/start/lobby', methods = ['POST'])
def start_game( game_id):
	#db = WherewolfDao("wherewolf",'postgres','')

	print "START GAME"
	print game_id

	auth = request.authorization

	username = auth.username
	password = auth.password

	response = {}

	#check authorization
	if not dao.check_password( username, password):
		response["results"] = "failture: bad auth"
		return jsonify(response)

	#check to make sure the user is the admin
	admin_id = dao.game_info(game_id)['admin_id']
	user_id = dao.get_user_id(username)

	if int(admin_id) != int(user_id):

		response["results"] = "user is not admin"
		return jsonify(response)
	
	print 'game id test'
	print isinstance( game_id, str)
	dao.set_game_status( game_id, 1)

	#Once the game is started, assigne werewolves
	#Get all the player_ids for the game
	print "****************************** game id: ", game_id
	players = dao.get_players_in_game(int(game_id))

	print "****************************** players in game", players

	num_players = len(players)
	num_werewolves = math.ceil(0.3*num_players)
	

	for player in players:
		dao.set_werewolf(player[0])

	print("**********************************")	
	response["results"] = "success: we're the best"

	print response

	return jsonify(response)


@app.route("/v1/game/<game_id>/status", methods=["PUT"])
def set_game_status(game_id):
	auth=request.authorization
	username=auth.username
	password=auth.password
	gameid=int(request.form["game_id"])
	status=request.form["game_status"]
	response={}
	print gameid
	response["status"]="failure"
	if dao.check_password(username,password) == True and dao.check_admin(game_id, username) == True:
		print status
		dao.set_game_status(game_id, status)
		response["status"]="success"
		return jsonify(response)
	else:
		return jsonify(response)

def end_game(game_id):
	auth=request.authorization
	username=auth.username
	password=auth.password
	gameid=int(request.form["game_id"])
	response={}
	response["status"]="failure"
	if dao.check_password(username,password) == True and dao.check_admin(game_id, username) == True:
		dao.set_game_status(game_id, 2)
		response["status"]="success"
		return response
	else:
		return response


@app.route("/v1/game/<game_id>/update", methods=["PUT"])
def game_update(game_id):
	auth=request.authorization
	username=auth.username
	password=auth.password
	lat=int(request.form["lat"])
	lng=int(request.form["lng"])
	response={}
	response["status"]="failure"
	try:
		dao.set_location(username,lat,lng)
		nearby=dao.get_alive_nearby(username, int(game_id), .5)
		print nearby
		response.update(nearby)
		response["status"]="success"
		return jsonify(response)
	except:
		return jsonify(response)

@app.route("/v1/game/<game_id>/werewolf", methods=["PUT"])
def set_werewolf(game_id):
	username=request.form["username"]
	game_id=request.form["game_id"]
	response={}
	response["status"]="failure"
	try:
		dao.set_werewolf(gameid,username)
		response["status"]="success"
		return jsonify(response)
	except:
		return jsonify(response)


@app.route("/v1/game/<game_id>/vote", methods=["POST"])
def cast_vote(game_id):
	auth=request.authorization
	username=auth.username
	password=auth.password
	player_id=request.form["player_id"]
	target_id=request.form["target_id"]
	response={}
	response["status"]="failure"
	if dao.check_password(username, password)==True:
		print player_id
		dao.vote(game_id,player_id,target_id)
		response["status"]="success"
		return jsonify(response)
	else:
		return jsonify(response)

	if target_id == 0:
		dao.award_achievement(username, "It is never Lupus")

@app.route("/v1/game/<game_id>/vote", methods=["GET"])
def tally_vote(game_id):
	response={}
	response["status"]="failure"
	try:
		r=dao.tally_vote(game_id)
		response["status"]="success"
		return jsonify(r)
	except:
		return jsonify(response)

def end_vote(game_id):
	response={}
	response["response"]="failure"
	x = tally_vote(game_id)
	player=max(x,key=x.get)
	#print player
	dao.set_dead(player)
	response["status"]="success"
	return response
@app.route( '/v1/game/<gameid>/nightfall/lobby')
def nightfall(gameid):

	response = {}


	#change the day status for to night
	dao.set_night(gameid)

	response["test_resp"] = "temp_response"


	#at the end of the night count the vote

	endvote()

	return jsonify( response)


@app.route("/v1/game/<game_id>/attack", methods=["POST"])
def attack(game_id):#is_werewolf broken get working is_werewolf
	vusername=request.form["vusername"]
	game_id=request.form["game_id"]
	auth=request.authorization
	username=auth.username
	password=auth.password
	response={}
	response["status"]="failure"
	rank=dao.is_werewolf(username)
	print rank
	villhp=10
	villac=1
	wolfhp=0
	wolfac=0
	if rank ==1:
		wolfhp=10
		wolfac=3
	elif rank ==2:
		wolfhp=15
		wolfac=5
	elif rank ==3:
		wolfhp=20
		wolfac=7
	else:
		print "you aint a werewolf"
	print wolfhp
	print wolfac
	items=dao.get_items(vusername)
	print items
	for item in items:
		villhp=int(item["defense"])+villhp
		villac=int(item["attack"])+villac


	if dao.check_password(username, password)==True:
		winner=attack_calc(wolfhp, wolfac, villhp, villac)
		if winner=="Wolf":
			dao.set_dead(vusername)
			dao.upgrade_werewolf(username)	
			print "another one bites the dust"
			response["status"]="success"
		else:
			dao.set_dead(username)
			dao.award_achievement(vusername, "Hair of the dog")
			#print "achievement unlocked"
			#print "The werewolf is dead. Long live the werewolf."

			response["status"]="success"
		return jsonify(response)
	else:
		return jsonify (response)



#def get_attack_stats(wherewolf_id,villager_id):

#payload = {'game_id': game_id, 'minValue': minValue, 'maxValue': maxValue, 'radius': radius, 'num_landmark': num_landmark}
@app.route("/v1/game/<game_id>/landmark", methods=["POST"])
def set_random_location(game_id):
	game_id=request.form["game_id"]
	minValue=request.form["minValue"]
	maxValue=request.form["maxValue"]
	radius=request.form["radius"]
	response={}
	response["response"]="failure"
	num_landmark=request.form["num_landmark"]
	for x in range(int(num_landmark)):
		print num_landmark
		lat=random.uniform(float(minValue),float(maxValue))
		lng=random.uniform(float(minValue),float(maxValue))
		land_type=randint(0,1)
		quantity=0
		itemid=0
		print land_type
		if land_type == 1:
			quantity=randint(1,6)
			itemid=randint(1,4)
		else:
			print "safe zone"
		print quantity
		try:
			dao.set_landmark(game_id,lat,lng,radius,land_type,quantity,itemid)
			print "landmark "+str(lat)+str(lng)+str(land_type)
		except:
			print "landmark failure"
		response["status"]="success"
	return jsonify(response)


def daybreak():
	print('It is now a beutiful morning and you are still alive')


	


if __name__ == "__main__":
    app.run(debug=True, port=5100)
