package edu.utexas.egbom.wherewolf;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

public class GameLobbyActivity extends Activity
{
	
	private static final String TAG = "lobbyactivity";
	
	private void startMainGameActivity()
	{
		Log.v(TAG, "User pressed the create game button");
		Intent create = new Intent(this, MainGameActivity.class);
		startActivity(create);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lobby_layout);
		
		ArrayList<Player> arrayOfPlayers = new ArrayList<Player>();
		
		arrayOfPlayers.add(new Player(1, "Dinner", "", 0));
		arrayOfPlayers.add(new Player(2,  "Post-Dinner","", 0));
		arrayOfPlayers.add(new Player(3, "Supper","", 0));
		arrayOfPlayers.add(new Player(4, "Appetizer", "", 0));
		
		//Create the adapter to convert the array to views
		PlayerAdapter adapter = new PlayerAdapter(this, arrayOfPlayers);
		// Attach the adapter to a ListView

		ListView playerListView = (ListView) findViewById(R.id.lobbyplayerlist);
		playerListView.setAdapter(adapter);
		
		final Button button = (Button) findViewById(R.id.startbutton);
		
		View.OnClickListener jim = new View.OnClickListener() {
			public void onClick(View v) {
				Log.v(TAG, "TESTTTTTTTTTT");
				startMainGameActivity();
			}
			
			
		 };
		 button.setOnClickListener(jim);
		
		
	}
}
	

