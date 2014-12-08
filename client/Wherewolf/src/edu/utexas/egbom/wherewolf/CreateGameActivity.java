package edu.utexas.egbom.wherewolf;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateGameActivity extends Activity{
	
	
	private static final String TAG = "creating game activity";
	public void stopGameCreate()
	{
		Log.v(TAG, "closing the create game screen");
		new CreateGameTask().execute();
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.create_layout);
		
		final Button creategamebutton = (Button) findViewById(R.id.creategamebutton);
		View.OnClickListener clicking = new View.OnClickListener() {
			public void onClick(View v) {
				Log.v(TAG, "TESTTTTTTTTTT");
				stopGameCreate();
			}
		};
		creategamebutton.setOnClickListener(clicking);
	}
	class CreateGameTask extends AsyncTask<Void, Integer, CreateGameResponse> {

        @Override
        protected CreateGameResponse doInBackground(Void... request) {

            WherewolfPreferences pref = new WherewolfPreferences(CreateGameActivity.this);
        	
            final EditText game_nameTV = (EditText) findViewById(R.id.game_name);
            final EditText descriptionTV = (EditText) findViewById(R.id.description);
            
            String game_name = game_nameTV.getText().toString();
            String description = descriptionTV.getText().toString();
            
            CreateGameRequest CreateGameRequest = new CreateGameRequest(pref.getUsername(), pref.getPassword(),
            		game_name, description);
            
            Log.i("Create game stuff",game_name);
            Log.i("Create game stuff",description);
            return CreateGameRequest.execute(new WherewolfNetworking());
            
        }

        @Override
    	protected void onPostExecute(CreateGameResponse result) {
            
            //final TextView errorText = (TextView) findViewById(R.id.error_text);
            
            if (result.getStatus().equals("success")) {
         
                Log.v("TAG", "CreateGame to GameSelection");
                Intent intent = new Intent(CreateGameActivity.this, GameLobbyActivity.class);
                startActivity(intent);

            } else {
                // do something with bad password
               Toast.makeText(CreateGameActivity.this, "Error", Toast.LENGTH_LONG).show();
            }

        }
	}
}
