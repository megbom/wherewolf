# Wherewolf game DAO
# Abstraction for the SQL database access.

import sqlite3
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

    def __init__(self, dbname):
        print 'Created the DAO'
        self.dbname = dbname
        self.conn = sqlite3.connect('wherewolf.db')
 
    def create_player(self, username, password, firstname, lastname):
        """ registers a new player in the system """
        conn = sqlite3.connect(self.dbname)
        with conn:
            c = self.conn.cursor()
            c.execute('SELECT COUNT(*) from user WHERE username=?',(username,))
            n = int(c.fetchone()[0])
            print 'num of rfdickersons is ' + str(n)
            if n == 0:
                hashedpass = md5.new(password).hexdigest()
                c.execute('INSERT INTO user (username, password, firstname, lastname) VALUES (?,?,?,?)', (username, hashedpass, firstname, lastname))
                self.conn.commit()
            else:
                raise UserAlreadyExistsException('{} user already exists'.format((username)) )
        
    def checkpassword(self, username, password):
        """ return true if password checks out """
        conn = sqlite3.connect(self.dbname)
        with conn:
            c = self.conn.cursor()
            results = c.execute('SELECT password FROM user WHERE username=?',(username,))
            hashedpass = md5.new(password).hexdigest()
            #print hashedpass
            return results.fetchone()[0] == hashedpass
        
    def set_location(self, username, lat, lng):
        conn = sqlite3.connect(self.dbname)
        with conn:
            c = self.conn.cursor()
            c.execute("SELECT userid FROM user WHERE username=?", (username,))
            userid=int(c.fetchone()[0])
            c.execute("SELECT playerid FROM player WHERE userid=?", (userid,))
            playerid=int(c.fetchone()[0])
            c.execute("UPDATE player SET lat=? , lng=? WHERE playerid=?", (lat, lng, playerid))
            
            self.conn.commit()
        
    def get_location(self, username):
        conn = sqlite3.connect(self.dbname)
        with conn:
            c = self.conn.cursor()
            c.execute("SELECT userid FROM user WHERE username=?", (username,))
            userid=int(c.fetchone()[0])
            c.execute("SELECT playerid FROM player WHERE userid=?", (userid,))
            playerid=int(c.fetchone()[0])
            c.execute("SELECT lat, lng FROM player WHERE playerid=?", (playerid,))
            location=c.fetchone()
            locationdic={"latitude":location[0],"longitude":location[1]}
            return locationdic
            
        
    def get_alive_nearby(self, username):
        """ returns a list of players nearby """
        conn = sqlite3.connect(self.dbname)
        with conn:
            c = self.conn.cursor()
            c.execute("SELECT userid FROM user WHERE username=?", (username,))
            userid=int(c.fetchone()[0])
            c.execute("SELECT playerid FROM player WHERE userid=?", (userid,))
            playerid=int(c.fetchone()[0])
            c.execute("SELECT lat, lng FROM player WHERE playerid=?", (playerid,))
            coord=c.fetchone()
            c.execute("SELECT gameid FROM player WHERE playerid=?", (playerid,))
            gameid=c.fetchone()[0]
            c.execute("SELECT playerid FROM player WHERE lat BETWEEN lat=? - .005 AND lng=? - .005 AND is_dead =0 \
                      EXCEPT SELECT playerid FROM player WHERE playerid=?", (coord[0], coord[1], playerid))
            nearby=c.fetchall()
            if len(nearby)==1:
                return nearby[0]
            else:
                return nearby
        
    def add_item(self, username, itemname):
        """ adds a relationship to inventory and or increments quantity by 1"""
        #pass
        conn = sqlite3.connect(self.dbname)
        with conn:
            c = self.conn.cursor()
            c.execute("SELECT itemid FROM item WHERE name=?", (itemname,))
            itemid=int (c.fetchone()[0])
            c.execute("SELECT userid FROM user WHERE username=?", (username,))
            userid=int(c.fetchone()[0])
            c.execute("SELECT playerid FROM player WHERE userid=?", (userid,))
            playerid=int(c.fetchone()[0])
            c.execute("SELECT * FROM inventory WHERE playerid=? AND itemid=?", (playerid, itemid))
            num=str(c.fetchone())
            if num == "None":
                c.execute("INSERT INTO inventory VALUES(?,?,1)",(playerid, itemid))
                c.execute("SELECT * FROM inventory ORDER BY playerid ASC")
                self.conn.commit()
            else:
                c.execute("UPDATE inventory SET quantity=quantity+1 WHERE playerid=? AND itemid=?", (playerid, itemid))
                self.conn.commit()
            
            
            
        
    def get_items(self, username):
        """ get a list of items the user has"""
        conn = sqlite3.connect(self.dbname)
        with conn:
            c = self.conn.cursor()
            c.execute("SELECT userid FROM user WHERE username=?", (username,))
            userid=int(c.fetchone()[0])
            c.execute("SELECT playerid FROM player WHERE userid=?", (userid,))
            playerid=int(c.fetchone()[0])
            c.execute("SELECT * FROM inventory WHERE playerid=?", (playerid,))
            itemlist=c.fetchall()
            returnlist=[]
            for i in range(len(itemlist)):
                c.execute("SELECT name, description FROM item WHERE itemid=?", (itemlist[i][1],))
                ident=c.fetchone()
                templist=[]
                description =str(ident[1])
                name=str(ident[0])
                itemdic={"Name":name, "Description": description, "Quantity": itemlist[i][2]}
                returnlist.append(itemdic)
            print returnlist
            return returnlist
        
        
    def award_achievement(self, username, achievementname):
        """ award an achievement to the user """
        conn = sqlite3.connect(self.dbname)
        with conn:
            c = self.conn.cursor()
            c.execute("SELECT achievementid FROM achievement WHERE name=?", (achievementname,))
            achievementid=int (c.fetchone()[0])
            c.execute("SELECT userid FROM user WHERE username=?", (username,))
            userid=int(c.fetchone()[0])
            c.execute("SELECT * FROM user_achievement WHERE userid=? AND achievementid=?", (userid, achievementid))
            num=str(c.fetchone())
            if num == "None":
                c.execute("INSERT INTO user_achievement(userid, achievementid) VALUES(?,?)", (userid, achievementid))
                self.conn.commit()
            
    def get_achievements(self, username):
        """ return a list of achievements for the user """
        conn = sqlite3.connect(self.dbname)
        with conn:
            c = self.conn.cursor()
            c.execute("SELECT userid FROM user WHERE username=?", (username,))
            userid=int(c.fetchone()[0])
            c.execute("SELECT * FROM user_achievement WHERE userid=?", (userid,))
            itemlist=c.fetchall()
            returnlist=[]
            for i in range(len(itemlist)):
                c.execute("SELECT name, description FROM achievement WHERE achievementid=?", (itemlist[i][1],))
                achievement=c.fetchone()
                achievedic={"Name": str(achievement[0]),"Description":str(achievement[1])}
                returnlist.append(achievedic)
            print returnlist
            return returnlist
        
    def set_dead(self, username):
        """ set a player as dead """
        conn = sqlite3.connect(self.dbname)
        with conn:
            c = self.conn.cursor()
            c.execute("SELECT userid FROM user WHERE username=?", (username,))
            userid=int(c.fetchone()[0])
            c.execute("SELECT playerid FROM player WHERE userid=?", (userid,))
            playerid=int(c.fetchone()[0])
            c.execute("UPDATE player SET is_dead=1 WHERE playerid=?", (playerid,))
            self.conn.commit()
        
    def get_players(self, gamename):
        """ get information about all the players currently in the game """
        conn = sqlite3.connect(self.dbname)
        with conn:
            c = self.conn.cursor()
            c.execute("SELECT gameid FROM game WHERE name=?",(gamename,))
            gameid=int(c.fetchone()[0])
            c.execute("SELECT playerid FROM player WHERE gameid=?",(gameid,))
            playerlist=c.fetchall()
            print playerlist
            dictlist=[]     
            for i in range(len(playerlist)):
                playerdic={}
                c.execute("SELECT is_dead, lat, lng, is_werewolf,num_gold FROM player WHERE playerid=?",(playerlist[i]))
                playercond=c.fetchone()
                print playercond
                namelist=["Dead","latitude","longitude","Werewolf","gold"]
                for j in range(len(namelist)):
                    playerdic[namelist[j]]=playercond[j]
                dictlist.append(playerdic)
            print playerdic
            return playerdic
                    
                          
        
    def get_user_stats(self, username):
        """ return a list of all stats for the user """
        conn = sqlite3.connect(self.dbname)
        with conn:
            c = self.conn.cursor()
            c.execute("SELECT userid FROM user WHERE username=?", (username,))
            userid=int(c.fetchone()[0])
            c.execute("SELECT totalKills, totalDeaths, totalGames, timesWolf FROM user_stat WHERE userid=?",(userid,))
            statlist=c.fetchone()
            namelist=["Total Kills", "Total Deaths", "Total Games", "Times Wherewolf"]
            statdic={}
            for i in range(len(statlist)):
                statdic[namelist[i]]=statlist[i]
            return statdic
                
        
    def get_player_stats(self, username):
        """ return a list of all stats for the player """
        conn = sqlite3.connect(self.dbname)
        with conn:
            c = self.conn.cursor()
            c.execute("SELECT userid FROM user WHERE username=?", (username,))
            userid=int(c.fetchone()[0])
            c.execute("SELECT playerid FROM player WHERE userid=?", (userid,))
            playerid=int(c.fetchone()[0])
            c.execute("SELECT numKills, numgold, potionused FROM player_stat WHERE playerid=?",(playerid,))
            statlist=c.fetchone()
            namelist=["Kills", "Gold", "Potions Used"]
            statdic={}
            for i in range(len(statlist)):
                statdic[namelist[i]]=statlist[i]
            return statdic
        
    # game methods    
    def join_game(self, username, gameid):
        """ makes a player for a user. adds player to a game """
        conn = sqlite3.connect(self.dbname)
        with conn:
            c = self.conn.cursor()
            c.execute("SELECT userid FROM user WHERE username=?", (username,))
            userid=int(c.fetchone()[0])
            c.execute("INSERT INTO player(userid,is_dead,gamid) VALUES(?,?,?)", (userid, 0, gameid))
            self.conn.commit()
    
    def leave_game(self, username):
        """ deletes player for user. removes player from game"""
        conn = sqlite3.connect(self.dbname)
        with conn:
            c = self.conn.cursor()
            c.execute("SELECT userid FROM user WHERE username=?", (username,))
            userid=c.fetchone()[0]
            c.execute("SELECT playerid FROM player WHERE userid=?", (userid,))
            playerid=int(c.fetchone()[0])
            c.execute("DELETE FROM player WHERE playerid=?", (playerid,))
            self.conn.commit()
        
    def create_game(self, username):
        """ creates a new game """
        conn = sqlite3.connect(self.dbname)
        with conn:
            c = self.conn.cursor()
            c.execute("SELECT userid FROM user WHERE username=?", (username,))
            userid=c.fetchone()[0]
            c.execute("INSERT INTO game(adminid,status,name) VALUES(?,?,?)", (userid,0,username))
            self.conn.commit()
            
    def start_game(self, gameid):
        """ set the game as started """
        conn = sqlite3.connect(self.dbname)
        with conn:
            c = self.conn.cursor()
            c.execute("UPDATE game SET status=1 WHERE gameid=?", (gameid,))
            self.conn.commit()
            return
    def end_game(self, gameid):
        """ delete all players in the game, set game as completed """
        conn = sqlite3.connect(self.dbname)
        with conn:
            c = self.conn.cursor()
            c.execute("DELETE FROM player WHERE gameid=?", (gameid,))
            self.conn.commit()
            c.execute("UPDATE game SET status=0 WHERE gameid=?", (gameid,))
            self.conn.commit()
if __name__ == "__main__":
    dao = WherewolfDao("wherewolf.db")
    try:
        dao.create_player('rfdickerson', 'furry', 'Robert', 'Dickerson')
        print 'Created a new player!'
    except UserAlreadyExistsException as e:
        print e
    except Exception:
        print 'General error happened'
        
    username = 'rfdickerson'
    correct_pass = 'furry'
    incorrect_pass = 'scaley'
    print 'Logging in {} with {}'.format(username, correct_pass)
    print 'Result: {} '.format( dao.checkpassword(username, correct_pass ))
    
    print 'Logging in {} with {}'.format(username, incorrect_pass)
    print 'Result: {} '.format( dao.checkpassword(username, incorrect_pass ))
  
    #dao.add_item('rfdickerson',"Blunderbuss")
    #dao.add_item('rfdickerson',"Silver Knife")
    #dao.get_items('rfdickerson')
    #dao.get_players("t")
    #dao.get_location('rfdickerson')
    #dao.end_game(2)
    #dao.start_game(2)
    #dao.leave_game('rfdickerson')
    #dao.set_location('rfdickerson',51,65)
    #dao.create_game('rfdickerson')
    #dao.award_achievement('rfdickerson','A hairy situation')
    #dao.get_achievements('rfdickerson')
    #dao.get_user_stats('rfdickerson')
    print dao.get_alive_nearby('rfdickerson')
