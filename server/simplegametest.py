import requests
import json
import random

hostname = 'http://127.0.0.1:5100'
# user = 'rfdickerson'
# password = 'awesome'
game_id = 0

rest_prefix = "/v1"

''' Important functions
create a game
leave a game
update game state with location
cast a vote
'''

def create_user(username, password, firstname, lastname):
    payload = {'username': username, 'password': password, 'firstname': firstname, 'lastname': lastname}
    url = "{}{}{}".format(hostname, rest_prefix, "/register")
    print url
    print payload
    r = requests.post(url, data=payload)
    print r
    response = r.json()
    print response["status"]

def create_game(username, password, game_name, description):
    payload = {'game_name': game_name, 'description': description}
    url = "{}{}{}".format(hostname, rest_prefix, "/game")
    print 'sending {} to {}'.format(payload, url)
    r = requests.post(url, auth=(username, password), data=payload)
    print r
    response = r.json()
    #rjson = json.loads(response)
    #print rjson["status"]
    print response["status"]
    #return response["results"]["game_id"]


def leave_game(username, password, game_id):
    r = requests.delete(hostname + rest_prefix + "/game/" + str(game_id), 
                        auth=(username, password))
    
    response = r.json()
    print response


def set_game_status(username,password, game_id, game_status):
    payload = {'username': username, 'game_id': game_id, 'game_status': game_status}
    r=requests.put(hostname + rest_prefix + "/game/" + str(game_id) +"/status", auth=(username, password), data=payload )
    print r.json()


def update_game(username, password, game_id, lat, lng):
    """ reports to the game your current location, and the game 
    returns to you a list of players nearby """

    payload = {'lat': lat, 'lng': lng}
    url = "{}{}/game/{}/update".format(hostname, rest_prefix, game_id)
    r = requests.put(url, auth=(username, password), data=payload)
    print r
    response = r.json()

    print response


def game_info(username, password, game_id):
    ''' returns all the players, the time of day, and other options for the game '''
    r = requests.get(hostname + rest_prefix + "/game/" + str(game_id), auth=(username, password))
    response = r.json()
    print response

def cast_vote(username, password, game_id, player_id,target_id):
    payload = {'player_id': player_id,"target_id":target_id,"game_id":game_id}
    r = requests.post(hostname + rest_prefix + "/game/" + str(game_id) + "/vote",auth=(username, password),data=payload)
    response = r.json()

    print response

def set_game_time(game_time):
    ''' allows you to override the current time to a user specified one'''
    payload = {'game_id': game_id, 'current_time': game_time}
    r = requests.post(hostname + rest_prefix + "/game/admin")
    r = r.json()
    print response

def join_game(username, password, game_id):
    print 'Joining game id {}'.format(game_id)
    payload = {'game_id': game_id}
    url = "{}{}/game/{}/lobby".format(hostname, rest_prefix, game_id)
    r = requests.post(url, auth=(username, password))
    r = r.json()
    print r


def attack(username, password, game_id, vusername):
    payload = {'game_id': game_id, 'vusername': vusername}
    r = requests.post(hostname + rest_prefix + "/game/" + str(game_id) + "/attack",auth=(username, password), data=payload)
    response = r.json()
    print response

def get_games(username, password):
    r = requests.get(hostname + rest_prefix + "/game")
    r = r.json()
    return r["results"]
def set_random_landmark(game_id):
    minValue = 9.9
    maxValue = 10.1
    radius = 2000  # 2000 meters is quite far away
    num_landmark = random.randint(3,5)  # 3 to 5 landmark
    payload = {'game_id': game_id, 'minValue': minValue, 'maxValue': maxValue, 'radius': radius, 'num_landmark': num_landmark}
    requests.post(hostname + rest_prefix + "/game/" + str(game_id) +"/landmark", data=payload )
    return num_landmark

def create_users():
    create_user('michael', 'paperlkasfdj', 'Michael', 'Scott')
    create_user('dwight', 'paperlkasfdj', 'Dwight', 'Schrute')
    create_user('jim', 'paperlkasfdj', 'Jim', 'Halpert')
    create_user('pam', 'paperlkasfdj', 'Pam', 'Beesly')
    create_user('ryan', 'paperlkasfdj', 'Ryan', 'Howard')
    create_user('andy', 'paperlkasfdj', 'Andy', 'Bernard')
    create_user('angela', 'paperlkasfdj', 'Angela', 'Martin')
    create_user('toby', 'paperlkasfdj', 'Toby', 'Flenderson')

def werewolf_winning_game():
    game_id = create_game('michael', 'paper', 'NightHunt', 'A test for werewolf winning')
    games = get_games('michael', 'paper')
    for game in games:
        print "Id: {},\tName: {}".format(game["game_id"], game["name"])
    
    join_game('dwight', 'paperlkasfdj', game_id)
    join_game('jim', 'paperlkasfdj', game_id)
    join_game('pam', 'paperlkasfdj', game_id)
    join_game('ryan', 'paperlkasfdj', game_id)
    join_game('andy', 'paperlkasfdj', game_id)
    join_game('angela', 'paperlkasfdj', game_id)
    join_game('toby', 'paperlkasfdj', game_id)
    set_game_status('michael', 'paperlkasfdj', game_id)
    
    leave_game('micheal', 'paperlkasfdj', game_id)
    
def set_werewolf(game_id, username):
    payload = {'username': username, 'game_id': game_id}
    r=requests.post(hostname + rest_prefix + "/game/" + str(game_id) +"/werewolf", data=payload )
    print r


def start_game(username, password, game_id):
    url = "{}{}/game/{}/start/lobby".format(hostname, rest_prefix, game_id)
    r = requests.post(url, auth=(username, password))
    response = r.json()
    print response['results']
    return response['results']



def end_vote(game_id):
    payload = {"game_id":game_id}
    r = requests.post(hostname + rest_prefix + "/game/" + str(game_id) + "/vote",data=payload)
    response = r.json()
    print response

def nightfall(gameid):
    url = "{}{}/game/{}/nightfall/lobby".format(hostname, rest_prefix, gameid)
    r = requests.post(url)
    response = r.json()
    print response
    return response





if __name__ == "__main__":
    game_id=1

    create_users()
    create_game('michael', "paperlkasfdj", 'NightHunt', 'A test for werewolf winning')
    #set_random_landmark(game_id)
    set_werewolf(1,"jim")
    join_game('dwight',"paperlkasfdj", 1)#join_game('dwight', 'paperlkasfdj', game_id)
    join_game('jim', 'paperlkasfdj', game_id)
    join_game('pam', 'paperlkasfdj', game_id)
    join_game('ryan', 'paperlkasfdj', game_id)
    join_game('andy', 'paperlkasfdj', game_id)
    start_game('michael','paperlkasfdj',1)
    join_game('angela', 'paperlkasfdj', game_id)
    join_game('toby', 'paperlkasfdj', game_id)
    set_game_status('michael', 'paperlkasfdj', game_id, 1)

    print "NIGHTFALL TEST"

    nightfall(game_id)

    attack("jim",'paperlkasfdj',game_id,"dwight")
    
    #leave_game('michael', 'paperlkasfdj', game_id)
    #leave_game('dwight',"paperlkasfdj", 1)
    #leave_game('andy',"paperlkasfdj", 1)
    #update_game('dwight',"paperlkasfdj",1,1,5)
    #werewolf_winning_game()
    #create_game('michael', 'paperlkasfdj', 'NightHunt', 'A test for werewolf winning')
   # create_game('rfdickerson', 'awesome', 'NightHunt', 'A game in Austin')
   # update_game('rfdickerson', 'awesome', 80, 20)
   # game_info('rfdickerson', 'awesome', 22)
   # leave_game('rfdickerson', 'awesome', 302)
    cast_vote('dwight', 'paperlkasfdj', game_id, "dwight", "jim")
    cast_vote('pam', 'paperlkasfdj', game_id, "pam", "jim")
    cast_vote('ryan', 'paperlkasfdj', game_id, "ryan", "jim")
    cast_vote('jim', 'paperlkasfdj', game_id, "jim", "dwight")
    tally_vote(game_id)

