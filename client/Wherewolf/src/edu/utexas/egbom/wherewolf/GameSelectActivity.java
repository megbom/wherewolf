package edu.utexas.egbom.wherewolf;

import java.util.ArrayList;

import edu.utexas.egbom.wherewolf.Game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

public class GameSelectActivity extends Activity {
	private static final String TAG = "gameselectactivity";
	
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.select_layout);
		

		ArrayList<Game> arrayOfGames = new ArrayList<Game>();
		arrayOfGames.add(new Game("Kill 'em All", ""));
		arrayOfGames.add(new Game("Ride the Lightning", ""));
		arrayOfGames.add(new Game( "Master of Puppets", "Abigail"));
		arrayOfGames.add(new Game("And Justice For All", "Martha"));
		// Create the adapter to convert the array to views
		
		GameAdapter adapter = new GameAdapter(this, arrayOfGames);
		// Attach the adapter to a ListView

		ListView playerListView = (ListView) findViewById(R.id.gameList);
		playerListView.setAdapter(adapter);
		
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
}