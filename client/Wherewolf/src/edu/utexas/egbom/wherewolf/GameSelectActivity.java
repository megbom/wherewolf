package edu.utexas.egbom.wherewolf;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class GameSelectActivity extends Activity {
	private static final String TAG = "gameselectactivity";
	int clickedGameId;
	
	private void joinGameLobbyActivity()
	{
		Log.v(TAG, "User pressed the create game button");
		Intent create = new Intent(this, GameLobbyActivity.class);
		startActivity(create);
	}
	private int startCreateGameActvity()
	{
		Log.v(TAG, "User pressed the create game button");
		Intent create = new Intent(this, CreateGameActivity.class);
		startActivity(create);
		return 5;
	}
	protected void populateGames (String gamelist){
	System.out.println("got to games");
	String fixedString = gamelist.replaceAll("[{\\[\\]}]", "");
	System.out.println(fixedString);
	String[] gamesSplit = fixedString.split(",|:");
	
	ArrayList<Game> allGames = new ArrayList<Game>();

	GameAdapter adapter = new GameAdapter(this, allGames);
	
	Log.i("Game selection","called getgamestask");
	//new GetGamesTask().execute();
	
	
	for (int i = 0; i < gamesSplit.length; i = i +6){
		System.out.println("status is " + gamesSplit[i + 1] + " game id is " + gamesSplit[i + 3]
				+ " and finally " + gamesSplit[i + 5]);
		String gamenum= gamesSplit[i+3].replace(" ", "");
		allGames.add(new Game( gamesSplit[i + 1], Integer.parseInt(gamenum), gamesSplit[i + 5]));
	}
		
	final ListView gameLV = (ListView) findViewById(R.id.gameList);
	gameLV.setAdapter(adapter);
	
	gameLV.setOnItemClickListener(new OnItemClickListener(){
		public void onItemClick(AdapterView<?>parent, View view, int position, long id){
			Log.v("TAG","Selected game to join");
			
			Game clickedGame = (Game) gameLV.getItemAtPosition(position);
			
			Log.i("GAMESELECTION","after clickedGame");
			System.out.println("clicked game id: "+clickedGame.getGameId());
			System.out.println("game name is : "+clickedGame.getGameName());
			System.out.println("clicked game status: "+clickedGame.getGameStatus());
			clickedGameId = clickedGame.getGameId();
			
			Log.i("GAME SELECTION","BEfore task is called");
			new GameSelectionTask().execute();
			
			//Log.i("GAME_ID",clickedGameId);
	
		}
	});
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.select_layout);
		

		/*ArrayList<Game> arrayOfGames = new ArrayList<Game>();
		arrayOfGames.add(new Game("Kill 'em All", ""));
		arrayOfGames.add(new Game("Ride the Lightning", ""));
		arrayOfGames.add(new Game( "Master of Puppets", "Abigail"));
		arrayOfGames.add(new Game("And Justice For All", "Martha"));
		// Create the adapter to convert the array to views
		
		GameAdapter adapter = new GameAdapter(this, arrayOfGames);
		// Attach the adapter to a ListView
		*/
		
		
		final Button joinbutton = (Button) findViewById(R.id.joinGame);
		final Button createbutton = (Button) findViewById(R.id.createGame);
		
		View.OnClickListener otherclicker = new View.OnClickListener() {
			public void onClick(View v) {
				startCreateGameActvity();
			}
		};
		View.OnClickListener clicker = new View.OnClickListener() {
			public void onClick(View v) {
				joinGameLobbyActivity();
			}
		};
		createbutton.setOnClickListener(otherclicker);
		joinbutton.setOnClickListener(clicker);
		
	}
	class GameSelectionTask extends AsyncTask<Void, Integer, JoinGameResponse> {

        @Override
        protected JoinGameResponse doInBackground(Void... request) {
           
            WherewolfPreferences pref = new WherewolfPreferences(GameSelectActivity.this);
            
            Log.i("GAME_SELECTION",pref.getUsername());
            Log.i("GAME_SELECTION", "password: "+pref.getPassword());
            
            JoinGameRequest JoinGameRequest = new JoinGameRequest(pref.getUsername(), 
            		pref.getPassword(),clickedGameId);
            	
            	Log.i("GAME_SELECTION","RIGHT BEFORE RETURN");
            	return JoinGameRequest.execute(new WherewolfNetworking());
        }
	protected void onPostExecute(JoinGameResponse result) {
    	
    	Log.i("SELECTION","GOT TO POST EXECUTE");

    	Log.i("SELECTION STATUS",result.getStatus());
    	Log.i("SELECTION MESSAGE",result.getErrorMessage());
        
        if (result.getStatus().equals("success")) {
            
            WherewolfPreferences pref = new WherewolfPreferences(GameSelectActivity.this);
            pref.setCreds(pref.getUsername(), pref.getPassword());

            
            Log.v("TAG", "GameSelected");
            Intent intent = new Intent(GameSelectActivity.this, GameLobbyActivity.class);
            startActivity(intent);

        } 

    }
}
	class GetGamesTask extends AsyncTask<Void, Integer, GetGamesResponse> {

        @Override
        protected GetGamesResponse doInBackground(Void... request) {
        	
        	Log.i("GAME SELECTION","Got to do in Background");
           
            WherewolfPreferences pref = new WherewolfPreferences(GameSelectActivity.this);
            
            Log.i("GAME_SELECTION",pref.getUsername());
            Log.i("GAME_SELECTION", "password: "+pref.getPassword());
            
            GetGamesRequest GetGamesRequest = new GetGamesRequest(pref.getUsername(), 
            		pref.getPassword());
            	
            	Log.i("GAME_SELECTION","RIGHT BEFORE RETURN");
            	return GetGamesRequest.execute(new WherewolfNetworking());
        }
        
	

        protected void onPostExecute(GetGamesResponse result) {

        	Log.i("SELECTION STATUS",result.getStatus());
        	Log.i("SELECTION MESSAGE",result.getErrorMessage());
            
            if (result.getStatus().equals("success")) {

            	Log.i("Get games","get games was successful");
            	String games = result.getGames();
            	Log.i("GAMES STRING",games);
            	
            	populateGames(games);
            	
            }
            } 

    }
    
}