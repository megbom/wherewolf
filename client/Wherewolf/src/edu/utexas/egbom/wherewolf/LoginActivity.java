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


public class LoginActivity extends Activity {

	private static final String TAG = "loginactivity";
	private int numberOfPresses;
	
	private void startGameSelectActivity()
	{
		Log.v(TAG, "User pressed the login button");
		Intent login = new Intent(this, GameSelectActivity.class);
		EditText textusername = (EditText) findViewById(R.id.usernameText); 
		String strusername = textusername.getText().toString();
		EditText textpassword = (EditText) findViewById(R.id.passwordText); 
		String strpassword = textpassword.getText().toString();
		
		if  (strusername.equals("") && strpassword.equals("")) {
		}else{
			new SigninTask().execute();
		}
	}
	
	private void startRegisterActivity()
	{
		Log.v(TAG, "User pressed the register button");
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		Log.i(TAG, "created the login activity");
		final Button button = (Button) findViewById(R.id.registerButton);
		final Button buttonlogin = (Button) findViewById(R.id.loginButton);
		
		View.OnClickListener bob = new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startGameSelectActivity();
			}
		};
		
		View.OnClickListener jim = new View.OnClickListener() {
			public void onClick(View v) {
				startRegisterActivity();
			}
		};
		buttonlogin.setOnClickListener(bob);
		button.setOnClickListener(jim);

		/*
		 * button.setOnClickListener(new View.OnClickListener() { public void
		 * onClick(View v) { // Perform action on click } });
		 */
	}
	 class SigninTask extends AsyncTask<Void, Integer, SigninResponse> {

	        @Override
	        protected SigninResponse doInBackground(Void... request) {

	            final EditText nameTV = (EditText) findViewById(R.id.usernameText);
	            final EditText passTV = (EditText) findViewById(R.id.passwordText);
	            
	            String username = nameTV.getText().toString();
	            String password = passTV.getText().toString();
	            
	            SigninRequest signinRequest = new SigninRequest(username, password);
	            
	            return signinRequest.execute(new WherewolfNetworking());
	            
	        }

	        @Override
	    	protected void onPostExecute(SigninResponse result) {

	            Log.v("TAG", "Signed in user has player id " + result.getPlayerID());
	            
	            //final TextView errorText = (TextView) findViewById(R.id.error_text);
	            
	            if (result.getStatus().equals("success")) {
	         
	                final EditText nameTV = (EditText) findViewById(R.id.usernameText);
	                final EditText passTV = (EditText) findViewById(R.id.passwordText);
	                
	                Log.i("MAIN_ACTIVITY: password test", passTV.getText().toString());
	                
	                WherewolfPreferences pref = new WherewolfPreferences(LoginActivity.this);
	                pref.setCreds(nameTV.getText().toString(), passTV.getText().toString());

	                Log.v("TAG", "Signing in");
	                Intent intent = new Intent(LoginActivity.this, GameSelectActivity.class);
	                startActivity(intent);

	            } else {
	                // do something with bad password
	               Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_LONG).show();
	            }

	        }

	        

	    }

	@Override
	protected void onStart() {
		numberOfPresses = 0;
		Log.i(TAG, "started the login activity");
		super.onStart();
	}

	@Override
	protected void onRestart() {
		Log.i(TAG, "restarted the login activity");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.i(TAG, "resumed the login activity");
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "pause the login activity");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "stopped the login activity");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "destroyed the login activity");
		super.onDestroy();
	}
}
