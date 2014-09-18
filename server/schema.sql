-- turn off the foreign keys so we can drop tables without 
-- it complaining

PRAGMA foreign_keys = OFF;

DROP TABLE IF EXISTS user;
CREATE TABLE user (
	userid 		INTEGER PRIMARY KEY AUTOINCREMENT,
	firstname	TEXT NOT NULL,
	lastname	TEXT NOT NULL,
	created_at	DATE DEFAULT CURRENT_TIMESTAMP,
	username	TEXT UNIQUE NOT NULL,
	password	TEXT NOT NULL,
	current_player	INTEGER
);

DROP TABLE IF EXISTS player;
CREATE TABLE player (
   	playerid	INTEGER PRIMARY KEY AUTOINCREMENT,
    userid		INTEGER NOT NULL,
    is_dead		INTEGER NOT NULL,
    lat			REAL	NOT NULL,
    lng			REAL	NOT NULL,
	is_werewolf	INTEGER NOT NULL DEFAULT 0,
	num_gold	INTEGER NOT NULL DEFAULT 0,
	gameid		INTEGER NOT NULL REFERENCES game
);

DROP TABLE IF EXISTS playerlook;
CREATE TABLE playerlook (
	playerlookid 	INTEGER PRIMARY KEY AUTOINCREMENT,
	playerid		INTEGER NOT NULL,
	picture			TEXT NOT NULL
);

DROP TABLE IF EXISTS game;
CREATE TABLE game (
	gameid 	INTEGER PRIMARY KEY AUTOINCREMENT,
	adminid INTEGER NOT NULL REFERENCES user,
	status 	INTEGER NOT NULL DEFAULT 0,
	name	TEXT NOT NULL
);


DROP TABLE IF EXISTS achievement;
CREATE TABLE achievement (
	achievementid	INTEGER PRIMARY KEY AUTOINCREMENT,
	name			TEXT NOT NULL,
	description		TEXT NOT NULL
);

DROP TABLE IF EXISTS user_achievement;
CREATE TABLE user_achievement (
	userid			INTEGER,
	achievementid	INTEGER,
	created_at		DATE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

DROP TABLE IF EXISTS item;
CREATE TABLE item (
	itemid 		INTEGER PRIMARY KEY AUTOINCREMENT,
	name 		TEXT NOT NULL,
	description TEXT
);

DROP TABLE IF EXISTS inventory;
CREATE TABLE inventory (
	playerid 	INTEGER REFERENCES user,
	itemid 		INTEGER REFERENCES item,
	quantity 	INTEGER,
	primary key (playerid, itemid)
);

-- used to store number of kills in a game --
DROP TABLE IF EXISTS player_stat;
CREATE TABLE player_stat (
	playerid 	INTEGER NOT NULL REFERENCES player,
	numKills	INTEGER,
	numgold 	INTEGER,
	potionused	INTEGER
);

-- used to store number of kills historically
DROP TABLE IF EXISTS user_stat;
CREATE TABLE user_stat (
	userid 		INTEGER NOT NULL,
	totalKills	INTEGER,
	totalDeaths INTEGER,
	totalGames	INTEGER,
	timesWolf	INTEGER
);

--stores points of intrest locations
DROP TABLE IF EXISTS points;
CREATE TABLE points (
	pointid INTEGER PRIMARY KEY AUTOINCREMENT,
	lat 	INTEGER NOT NULL,
	long 	INTEGER NOT NULL,
	radius 	INTEGER NOT NULL,
	type	INTEGER NOT NULL DEFAULT 0

);

--stores loot for treasure locations
DROP TABLE IF EXISTS treasure;
CREATE TABLE treasure(
	pointid INTEGER REFERENCES points,
	itemid  INTEGER REFERENCES item,
	quantity INTEGER NOT NULL DEFAULT 0
);
-- creates a cascade delete so that all inventory items for the player
-- are automatically deleted

CREATE TRIGGER delete_inventory
BEFORE DELETE ON player
for each row
begin
	delete from inventory where playerid = 	old.playerid;
END;

CREATE INDEX playerindex ON inventory(playerid);
-- insert some data

PRAGMA foreign_keys = ON;

INSERT INTO user (userid, firstname, lastname, created_at, username, password) VALUES (1, 'Robert', 'Dickerson', '2014-08-30', 'rfdickerson', 'f96af09d8bd35393a14c456e2ab990b6');
INSERT INTO user (userid, firstname, lastname, created_at, username, password) VALUES (2, 'Abraham', 'Van Helsing', '2014-08-30', 'vanhelsing', 'be121740bf988b2225a313fa1f107ca1');

INSERT INTO game VALUES(2,1,5,"t");
INSERT INTO game VALUES(3,1,5,"s");

INSERT INTO player (playerid, userid, is_dead, lat, lng, gameid) VALUES (1, 1, 1, 38, 78,2);
INSERT INTO player (playerid, userid, is_dead, lat, lng, gameid) VALUES (2, 2, 1, 37, 77,3);

INSERT INTO achievement VALUES (1, 'Hair of the dog', 'Survive an attack by a werewolf');
INSERT INTO achievement VALUES (2, 'Top of the pack', 'Finish the game as a werewolf and receive the top number of kills');
INSERT INTO achievement VALUES (3, 'Children of the moon', 'Stay alive and win the game as a werewolf');
INSERT INTO achievement VALUES (4, 'It is never Lupus', 'Vote someone to be a werewolf, when they were a townsfolk');
INSERT INTO achievement VALUES (5, 'A hairy situation', 'Been near 3 werewolves at once.');
INSERT INTO achievement VALUES (6, 'Call in the Exterminators', 'Kill off all the werewolves in the game');



INSERT INTO user_achievement (userid, achievementid) VALUES (1, 1);
INSERT INTO user_achievement (userid, achievementid) VALUES (1, 2);

INSERT INTO item VALUES (1, 'Wolfsbane Potion', 'Protects the drinker from werewolf attacks');
INSERT INTO item VALUES (2, 'Blunderbuss', 'A muzzle-loading firearm with a short, large caliber barrel.');
INSERT INTO item VALUES (3, 'Invisibility Potion', 'Makes the imbiber invisible for a short period of time.');
INSERT INTO item VALUES (4, 'Silver Knife', 'A blade made from the purest of silvers');

INSERT INTO inventory VALUES (1, 2, 1);
INSERT INTO inventory VALUES (2, 1, 1);
