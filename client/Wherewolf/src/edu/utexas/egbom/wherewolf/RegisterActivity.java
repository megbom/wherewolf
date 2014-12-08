package edu.utexas.egbom.wherewolf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	private static final String TAG = "registeractivity";
	public void stopRegistering()
	{
		Log.v(TAG, "closing the register screen");
		new RegisterTask().execute();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register_layout);
	
		final Button button = (Button) findViewById(R.id.register_user_button);

		View.OnClickListener jim = new View.OnClickListener() {
			public void onClick(View v) {
				Log.v(TAG, "TESTTTTTTTTTT");
				SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				EditText textusername = (EditText) findViewById(R.id.usernameEditText); 
				String strusername = textusername.getText().toString();
				EditText textpassword = (EditText) findViewById(R.id.passwordEditText1); 
				String strpassword = textpassword.getText().toString();
				editor.putString("username", strusername);
				editor.putString("password", strpassword);
				editor.commit();
				stopRegistering();
			}
		};
		
		button.setOnClickListener(jim);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	class RegisterTask extends AsyncTask<Void, Integer, CreateUserResponse> {

		@Override
		protected CreateUserResponse doInBackground(Void... request) {

			final EditText nameTV = (EditText) findViewById(R.id.usernameEditText);
			final EditText passTV = (EditText) findViewById(R.id.passwordEditText1);
			final EditText pass2TV = (EditText) findViewById(R.id.passwordEditText2);
			final EditText firstnameTV = (EditText) findViewById(R.id.firstEditText);
			final EditText lastnameTV = (EditText) findViewById(R.id.lastEditText);

			Log.i(passTV.getText().toString(), pass2TV.getText().toString());

			if ((passTV.getText().toString()).equals(pass2TV.getText().toString())){
				String username = nameTV.getText().toString();
				String password = passTV.getText().toString();
				String firstname = firstnameTV.getText().toString();
				String lastname = lastnameTV.getText().toString();

				Log.i("Register_Activity","Before Create User Request is called");

				CreateUserRequest CreateUserRequest = new CreateUserRequest(username, password, firstname, lastname);

				Log.i("Register_Activity","After Create User Request is called");

				return CreateUserRequest.execute(new WherewolfNetworking());
			}else{

				return null;
			}

		}

		protected void onPostExecute(CreateUserResponse result) {

			Log.v("TAG", "Registered");
			Log.i("REGISTER_ACTIVITY",result.getStatus());

			if (result.getStatus().equals("success")) {

				final EditText nameTV = (EditText) findViewById(R.id.usernameEditText);
				final EditText passTV = (EditText) findViewById(R.id.passwordEditText1);

				WherewolfPreferences pref = new WherewolfPreferences(RegisterActivity.this);
				pref.setCreds(nameTV.getText().toString(), passTV.getText().toString());

				//TextView error = (TextView) findViewById(R.id.error_text);
				//error.setText("");

				Log.v("TAG", "Registering");
				Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
				startActivity(intent);

			} else {

				
			}

		}


	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private class SigninTask extends AsyncTask<Void, Integer, CreateUserResponse> {

	      @Override
	      protected CreateUserResponse doInBackground(Void... request) {

	    	  final EditText nameTV = (EditText) findViewById(R.id.usernameEditText);
	          final EditText passTV = (EditText) findViewById(R.id.passwordEditText1);
	          final EditText firstTV=(EditText) findViewById(R.id.firstEditText);
	          final EditText lastTV=(EditText) findViewById(R.id.lastEditText);
	          
 	          
	    
	          String username = nameTV.getText().toString();
	          String password = passTV.getText().toString();
	          String firstname= firstTV.getText().toString();
	          String lastname= lastTV.getText().toString();
	          
	          CreateUserRequest CreateUserRequest = new CreateUserRequest(username, password, firstname, lastname);
	          
	          return CreateUserRequest.execute(new WherewolfNetworking());
	      }
	      }
	    	  
	          
	      
	      

	      protected void onPostExecute(CreateUserResponse result) {

	          Log.v(TAG, "Signed in user has player id " + result.getPlayerID());
	          Context context = getApplicationContext();
	          
	          
	          
	          
	          //final TextView errorText = (TextView) findViewById(R.id.error_text);
	          
	          if (result.getStatus().equals("success")) {
	                          
	              final EditText nameTV = (EditText) findViewById(R.id.usernameEditText);
	              final EditText passTV = (EditText) findViewById(R.id.passwordEditText1);
	              
	              WherewolfPreferences pref = new WherewolfPreferences(RegisterActivity.this);
	              pref.setCreds(nameTV.getText().toString(), passTV.getText().toString());

	            
	              Log.v(TAG, "Signing in");
	              Intent intent = new Intent(RegisterActivity.this, GameSelectActivity.class);
	              startActivity(intent);

	          } else {
	              // do something with bad password
	        	  CharSequence text = result.getErrorMessage();
	        	  Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	          }

	      }

	      

	  }
