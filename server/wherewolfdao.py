# Wherewolf game DAO
# Abstraction for the SQL database access.

import psycopg2
import md5


class UserAlreadyExistsException(Exception):
    def __init__(self, err):
        self.err = err
    def __str__(self):
        return 'Exception: ' + self.err
        
class NoUserExistsException(Exception):
    def __init__(self, err):
        self.err = err
    def __str__(self):
        return 'Exception: ' + self.err
        
class BadArgumentsException(Exception):
    """Exception for entering bad arguments"""
    def __init__(self, err):
        self.err = err
    def __str__(self):
        return 'Exception: ' + self.err

class WherewolfDao:

    def __init__(self, dbname='wherewolf', pgusername='postgres', pgpasswd='abcdUSPS'):
        self.dbname = dbname
        self.pgusername = pgusername
        self.pgpasswd = pgpasswd
        print ('connection to database {}, user: {}, password: {}'.format(dbname, pgusername, pgpasswd))

    def get_db(self):
        
        return psycopg2.connect(database=self.dbname,user=self.pgusername,password=self.pgpasswd)

    def create_user(self, username, password, firstname, lastname):
        """ registers a new player in the system """
        conn = self.get_db()
        with conn:
            c = conn.cursor()
            c.execute('SELECT COUNT(*) from gameuser WHERE username=%s',(username,))
            n = int(c.fetchone()[0])
            # print 'num of rfdickersons is ' + str(n)
            if n == 0:
                hashedpass = md5.new(password).hexdigest()
                c.execute('INSERT INTO gameuser (username, password, firstname, lastname) VALUES (%s,%s,%s,%s) ', 
                          (username, hashedpass, firstname, lastname))
                conn.commit()
            else:
                raise UserAlreadyExistsException('{} user already exists'.format((username)) )
        
    def check_password(self, username, password):
        """ return true if password checks out """
        conn = self.get_db()
        with conn:
            c = conn.cursor()
            sql = ('select password from gameuser where username=%s')
            c.execute(sql,(username,))
            hashedpass = md5.new(password).hexdigest()
            u = c.fetchone()
            if u == None:
                raise NoUserExistsException(username)
            print 'database contains {}, entered password was {}'.format(u[0],hashedpass)
            return u[0] == hashedpass
        
    def set_location(self, username, lat, lng):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            print lat
            print lng
            sql = ('update player set lat=%s, lng=%s '
                   'where player_id=(select current_player from gameuser '
                   'where username=%s)')
            cur.execute(sql, (lat, lng, username))
            conn.commit()


    def get_location(self, username):
        conn = self.get_db()
        result = {}
        with conn:
            c = conn.cursor()
            sql = ('select player_id, lat, lng from player, gameuser '
                   'where player.player_id = gameuser.current_player '
                   'and gameuser.username=%s')
            c.execute(sql, (username,))
            row = c.fetchone()
            result["playerid"] = row[0]
            result["lat"] = row[1]
            result["lng"] = row[2]
        return result



    def get_alive_nearby(self, username, game_id, radius): 
        ''' returns all alive players near a player '''
        conn = self.get_db()
        result = []
        with conn:
            c = conn.cursor()
            sql_location = ('select lat, lng from player, gameuser where '
                           'player.player_id = gameuser.current_player '
                           'and gameuser.username=%s')
            c.execute(sql_location, (username,))
            location = c.fetchone()

            if location == None:
                return result

            # using the radius for lookups now
            sql = ('select player_id, '
                   'earth_distance( ll_to_earth(player.lat, player.lng), '
                   'll_to_earth(%s,%s) ) '
                   'from player where '
                   'earth_box(ll_to_earth(%s,%s),%s) '
                   '@> ll_to_earth(player.lat, player.lng) '
                   'and game_id=%s '
                   'and is_werewolf = 0 '
                   'and is_dead = 0')

            # sql = ('select username, player_id, point( '
            #       '(select lng from player, gameuser '
            #       'where player.player_id=gameuser.current_player '
            #       'and gameuser.username=%s), '
            #       '(select lat from player, gameuser '
            #       'where player.player_id=gameuser.current_player '
            #       'and gameuser.username=%s)) '
            #       '<@> point(lng, lat)::point as distance, '
            #       'is_werewolf '
            #       'from player, gameuser where game_id=%s '
            #       'and is_dead=0 '
            #       'and gameuser.current_player=player.player_id '
            #       'order by distance')
            # print sql

            c.execute(sql, (location[0], location[1], 
                            location[0], location[1], 
                            radius, game_id))
            for row in c.fetchall():
                d = {}
                d["player_id"] = row[0]
                d["distance"] = row[1]
                #d["distance"] = row[1]
                #d["is_werewolf"] = row[2]
                result.append(d)
        return result
                   
        
    def add_item(self, username, itemname):
        conn = self.get_db()
        with conn:
            cur=conn.cursor()

            cmdupdate = ('update inventory set quantity=quantity+1'
                         'where itemid=(select itemid from item where name=%s)' 
                         'and playerid='
                         '(select current_player from gameuser where username=%s);')
            cmd = ('insert into inventory (playerid, itemid, quantity)' 
                   'select (select current_player from gameuser where username=%s) as cplayer,'
                   '(select itemid from item where name=%s) as item,' 
                   '1 where not exists' 
                   '(select 1 from inventory where itemid=(select itemid from item where name=%s)' 
                   'and playerid=(select current_player from gameuser where username=%s))')
            cur.execute(cmdupdate + cmd, (itemname, username, username, itemname, itemname, username))

            conn.commit()

 
    def remove_item(self, username, itemname):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd = ('update inventory set quantity=quantity-1 where ' 
                   'itemid=(select itemid from item where name=%s) and ' 
                   'playerid=(select current_player from gameuser where username=%s);')
            cmddelete = ('delete from inventory where itemid=(select itemid from item where name=%s)' 
                         'and playerid=(select current_player from gameuser where username=%s) '
                         'and quantity < 1;')
            cur.execute(cmd + cmddelete, (itemname, username, itemname, username))
            conn.commit()


    def get_items(self, username):
        conn = self.get_db()
        items = []
        with conn:
            c = conn.cursor()
            sql = ('select item.name, item.description, quantity, '
                   'item.attack, item.defense from item, inventory, gameuser where '
                   'inventory.itemid = item.itemid and '
                   'gameuser.current_player=inventory.playerid and '
                   'gameuser.username=%s')
            c.execute(sql, (username,))
            for item in c.fetchall():
                d = {}
                d["name"] = item[0]
                d["description"] = item[1]
                d["quantity"] = item[2]
                d["attack"]= item[3]
                d["defense"]=item[4]
                items.append(d)
        return items

    def set_night(gameid):
        conn = self.get_db()
        with conn:
            c = conn.cursor()
            cmd = '''UPDATE game set is_night=1 where gameid=%s'''
            c.execute(cmd,(gameid,))
            conn.commit()

        
    def award_achievement(self, username, achievementname):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd = ('insert into user_achievement (user_id, achievement_id, created_at) '
                   'values ((select user_id from gameuser where username=%s), '
                   '(select achievement_id from achievement where name=%s), now());')
            cur.execute(cmd, (username, achievementname))
            conn.commit()

        
    def get_achievements(self, username):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd = ('select name, description, created_at from achievement, user_achievement '
                   'where achievement.achievement_id = user_achievement.achievement_id '
                   'and user_achievement.user_id = '
                   '(select user_id from gameuser where username=%s);')
            cur.execute(cmd, (username,))
            achievements = []
            for row in cur.fetchall():
                d = {}
                d["name"] = row[0]
                d["description"] = row[1]
                d["created_at"] = row[2]
                achievements.append(d)
        return achievements

    def set_dead(self, username):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd = ('update player set is_dead=1 '
                   'where player_id='
                   '(select current_player from gameuser where username=%s);')
            cur.execute(cmd, (username,))
            conn.commit()
    def set_player_dead(self,player_id):
        onn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd("UPDATE player SET is_dead=1 WHERE player_id=%s")
            curn.execute(cmd, (player_id,))
            conn.commit()

    def get_players(self, gameid):
        conn = self.get_db()
        players = []
        with conn:
            cur = conn.cursor()
            cmd = ('select player_id, is_dead, lat, lng, is_werewolf from player '
                   ' where game_id=%s;')
            cur.execute(cmd, (gameid,))
            for row in cur.fetchall():
                p = {}
                p["playerid"] = row[0]
                p["is_dead"] = row[1]
                p["lat"] = row[2]
                p["lng"] = row[3]
                p["is_werewolf"] = row[4]
                players.append(p)
        return players


    def get_alive(self, gameid):
        conn = self.get_db()
        players = []
        with conn:
            cur = conn.cursor()
            cmd = ('select player_id, is_dead, lat, lng, is_werewolf from player '
                   ' where game_id=%s AND is_dead=0 AND is_werewolf=0;')
            cur.execute(cmd, (gameid,))
            for row in cur.fetchall():
                p = {}
                p["playerid"] = row[0]
                players.append(p)
        return len(players)
    def get_werewolf(self, gameid):
        conn = self.get_db()
        players = []
        with conn:
            cur = conn.cursor()
            cmd = ('select player_id from player '
                   ' where game_id=%s AND is_dead=0 AND is_werewolf=1 OR is_werewolf=2 OR is_werewolf=3;')
            cur.execute(cmd, (gameid,))
            for row in cur.fetchall():
                p = {}
                p["playerid"] = row[0]
                players.append(p)
        return len(players)
    def set_werewolf(self,username):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd=("UPDATE player SET is_werewolf=1 WHERE player_id=(select current_player from gameuser where username=%s);")
            cur.execute(cmd, (username,)) 
            conn.commit()

    def is_werewolf(self,username):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            sql=("SELECT is_werewolf FROM player WHERE player_id =(select current_player from gameuser where username=%s);")
            cur.execute(sql, (username,))
            rank=cur.fetchone()[0]
            print rank
            return rank
        

    def upgrade_werewolf(self,username):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd1=("SELECT is_werewolf From player WHERE player_id=(select current_player from gameuser where username=%s);")
            cur.execute(cmd1, (username,))
            rank=cur.fetchone()[0]
            print rank
            if rank==3:
                rank=3
            else:
                rank=rank+1
            cmd2=("UPDATE player SET is_werewolf=%s WHERE player_id=(select current_player from gameuser where username=%s);")
            cur.execute(cmd2, (rank,username)) 
            conn.commit()
    
            
    def get_user_stats(self, username):
        pass
	    
        
    def get_player_stats(self, username):
        conn=self.get_db()
        with conn:
            cur=conn.cursor()
            cmd

    # game methods    
    def join_game(self, username, gameid):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd = ('INSERT INTO player ( is_dead, lat, lng, game_id) '
                   'VALUES ( %s, %s, %s, %s) returning player_id')
            cmd2 = ('update gameuser set current_player=%s where username=%s')
            cur.execute(cmd,( 0, 0, 0, gameid))
            cur.execute(cmd2, (cur.fetchone()[0], username));
            conn.commit()
	





    def leave_game(self, username):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            # cmd1=("SELECT player_id From player WHERE player_id=(select current_player from gameuser where username=%s);")
            # cmd1 = '''DELETE FROM player WHERE username=%s'''
            cmd1 = '''UPDATE gameuser set current_player = null where username=%s'''
            # cmd1 = '''UPDATE gameuser set current_player = null where username=%s'''
            cur.execute(cmd1, (username,)) 
            conn.commit()
	    
        





    def create_game(self, username, gamename,description):
        ''' returns the game id for that game '''
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd = ('INSERT INTO game (admin_id, name, description) VALUES ( '
                   '(SELECT user_id FROM gameuser where username=%s), '
                   '%s, %s) returning game_id')
            cur.execute(cmd,(username, gamename, description))
            game_id = cur.fetchone()[0]
            conn.commit()
            return game_id

    def check_admin(self, game_id,username):
        conn=self.get_db()
        with conn:
            cur = conn.cursor()
            cmd=("SELECT admin_id FROM game WHERE game_id=%s")
            cur.execute(cmd, (game_id,))
            admin=cur.fetchone()[0]
            cmd=("SELECT user_id FROM gameuser WHERE username=%s")
            cur.execute(cmd,(username,))
            user=cur.fetchone()[0]
            return admin==user

    def get_admin_location(self, game_id):
        conn=self.get_db()
        with conn:
            cur = conn.cursor()
            cmd=("SELECT admin_id FROM game WHERE game_id=%s;")
            cur.execute(cmd, (game_id,))
            admin=cur.fetchone()[0]
            cmd=("SELECT current_player FROM gameuser WHERE user_id=%s;")
            cur.execute(cmd,(admin,))
            player=cur.fetchone()[0]
            cmd=("SELECT lat, lng FROM player WHERE player_id=%s;")
            cur.execute(cmd,(player,))
            row = cur.fetchone()
            d = {}
            d["lat"]=row[0]
            d["lng"]=row[1]
            return d


    def start_game(self,game_id):
        conn=self.get_db()
        with conn:
            cur = conn.cursor()
            cmd=("UPDATE game SET status==1 WHERE game_id=%s")
            cur.execute(cmd,(game_id,))
            conn.commit()
    
    def end_game(self,game_id):
        conn=self.get_db()
        with conn:
            cur=conn.cursor()
            cmd=("UPDATE game SET status==2 WHERE game_id=%s")
            cur.execute(cmd,(game_id,))
            conn.commit()

    def game_info(self, game_id):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd = '''SELECT game_id, admin_id, status, name from game where game_id=%s'''
            cur.execute(cmd, (game_id,))
            row = cur.fetchone()
            d = {}
            d["game_id"] = row[0]
            d["admin_id"] = row[1]
            d["status"] = row[2]
            d["name"] = row[3]
            return d

    def get_games(self):
        conn = self.get_db()
        games = []
        with conn:
            cur = conn.cursor()
            cmd = ('SELECT game_id, name, status from game')
            cur.execute(cmd)
            for row in cur.fetchall():
                d = {}
                d["game_id"] = row[0]
                d["name"] = row[1]
                d["status"] = row[2]
                games.append(d)
            return games

    def get_time(self, game_id):
        conn = self.get_db()
        games = []
        with conn:
            cur = conn.cursor()
            cmd=("SELECT game_time FROM game WHERE game_id=%s;")
            cur.execute(cmd,(game_id,))
            time=cur.fetchone()[0]
            return time

    def set_time(self, game_id):
        conn = self.get_db()
        games = []
        with conn:
            cur = conn.cursor()
            cmd("UPDATE game SET game_time=%s WHERE game_id=%s")
            cur.execute(cmd,(game_id,))
            conn.commit()

    def set_game_status(self, game_id, status):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd = ('UPDATE game SET status=%s '
                   'where game_id=%s')
            print cmd
            cur.execute(cmd, (status, game_id))
            conn.commit()





    def game_info(self, game_id):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd = '''SELECT game_id, admin_id, status, name from game where game_id=%s'''
            cur.execute(cmd, (game_id,))
            row = cur.fetchone()
            d = {}
            d["game_id"] = str(row[0])
            d["admin_id"] = str(row[1])
            d["status"] = str(row[2])
            d["name"] = str(row[3])
            return d


    def get_players_in_game( self, game_id):
        # use the game_id to return a list of all the player_ids
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd = '''SELECT player_id from player where game_id=%s'''
            cur.execute( cmd,(game_id,))
            player_ids = cur.fetchall()
        return player_ids


    def get_user_id(self, username):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd = '''SELECT user_id FROM gameuser where username=%s'''
            cur.execute(cmd,(username,))
            user_id = cur.fetchone()[0]
        return user_id

    def set_game_status(self, game_id, status):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd = ('UPDATE game set status=%s '
                   'where game_id=%s')
            cur.execute(cmd, (game_id, status))


    def set_werewolf(self,player_id):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            cmd = ('UPDATE player set is_werewolf=1 where player_id=%s')
            cur.execute(cmd,(player_id,))
            conn.commit()



















    def vote(self, game_id, username, targetname):
        conn = self.get_db()
        with conn:
            cur = conn.cursor()
            print username
            sql = ('insert into vote '
                   '(game_id, player_id, target_id, cast_date) '
                   'values ( %s,'
                   '(select current_player from gameuser where username=%s), '
                   '(select current_player from gameuser where username=%s), '
                   'now())')

            print sql
            cur.execute(sql, (game_id, username, targetname))
            conn.commit()
            
    def tally_vote(self,game_id):
        votes=[]
        with conn:
            cur = conn.cursor()
            cmd=("SELECT COUNT(target_id) FROM vote WHERE game_id=%s;")
            cur.execute(cmd, (game_id,))
            for row in cur.fetchall():
                p = {}
                p["playerid"] = row[0]
                p["tally"] = row[1]
                votes.append(p)
            return votes


    def set_landmark(self, game_id, lat ,lng, radius,lan_type, quantity,item_id):
        print "start dao"
        conn = self.get_db()
        print "using dao"
        with conn:
            cur = conn.cursor()
            cmd=("INSERT INTO landmark(lat,lng,radius,type,game_id,quantity, itemid) VALUES "
                "(%s,%s,%s,%s,%s,%s,%s);")
            print cmd
            cur.execute(cmd, (lat,lng,radius,lan_type,game_id,quantity, item_id))
            print "landmark success"
            conn.commit()

    def clear_tables(self):
        conn = self.get_db()
        with conn:
            c = conn.cursor()
            c.execute('truncate gameuser cascade')
            c.execute('truncate player cascade')
            c.execute('truncate user_achievement cascade')
            conn.commit()

           
if __name__ == "__main__":
    dao = WherewolfDao('wherewolf','postgres','abcdUSPS')

    #dao.clear_tables()
    dao.join_game("dwight",25)
    dao.create_user('rfdickerson', 'awesome', 'Robert', 'Dickerson')
    dao.create_user('oliver','furry','Oliver','Cat')
    dao.create_user('vanhelsing', 'van', 'Van', 'Helsing')
       
    username = 'rfdickerson'
    correct_pass = 'awesome'
    incorrect_pass = 'scaley'
    print 'Logging in {} with {}'.format(username, correct_pass)
    print 'Result: {} '.format( dao.check_password(username, correct_pass ))
    
    print 'Logging in {} with {}'.format(username, incorrect_pass)
    print 'Result: {} '.format( dao.check_password(username, incorrect_pass ))

    game_id = dao.create_game('rfdickerson', 'TheGame',"asdf")
    print dao.check_admin( game_id,"rfdickerson")
    print game_id
    dao.create_game('oliver', 'AnotherGame',"ads")
    
    dao.join_game('oliver', game_id)
    dao.join_game('rfdickerson', game_id)
    dao.join_game('vanhelsing', game_id)

    print "Adding some items..."
    dao.add_item('rfdickerson', 'Silver Knife')
    dao.add_item('rfdickerson', 'Blunderbuss')
    dao.add_item('rfdickerson', 'Blunderbuss')
    dao.add_item('rfdickerson', 'Blunderbuss')
    dao.add_item('oliver', 'Blunderbuss')
    dao.remove_item('rfdickerson', 'Blunderbuss')

    print
    print 'rfdickerson items'
    print '--------------------------------'
    items = dao.get_items("rfdickerson")
    for item in items:
        print item["name"] + "\t" + str(item["quantity"])
    print

    #location stuff
    dao.set_location('rfdickerson', 30.202, 97.702)
    dao.set_location('oliver', 30.201, 97.701)
    dao.set_location('vanhelsing', 30.2, 97.7) 
    loc = dao.get_location('rfdickerson')
    loc2 = dao.get_location('oliver')
    dao.set_werewolf('rfdickerson')
    print "rfdickerson at {}, {}".format(loc["lat"], loc["lng"]) 
    print "oliver at {}, {}".format(loc2["lat"], loc2["lng"]) 

    dao.award_achievement('rfdickerson', 'Children of the moon')
    dao.award_achievement('rfdickerson', 'A hairy situation')
    achievements = dao.get_achievements("rfdickerson")

    print
    print 'rfdickerson\'s achievements'
    print '--------------------------------'
    for a in achievements:
        print "{} ({}) - {}".format(a["name"],a["description"],a["created_at"].strftime('%a, %H:%M'))
    print
    
    nearby = dao.get_alive_nearby('rfdickerson', game_id, 700000)
    print ('Nearby players: ')
    for p in nearby:
        print "{} is {} meters away".format(p["player_id"],p["distance"])

    dao.vote(game_id, 'rfdickerson', 'oliver')
    dao.vote(game_id, 'oliver', 'vanhelsing')
    dao.vote(game_id, 'vanhelsing', 'oliver')
    print dao.get_players(1)
    
    dao.set_dead('rfdickerson')
