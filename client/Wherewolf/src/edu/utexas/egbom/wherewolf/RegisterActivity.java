package edu.utexas.egbom.wherewolf;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends Activity {

	private static final String TAG = "registeractivity";
	public void stopRegistering()
	{
		Log.v(TAG, "closing the register screen");
		this.finish();
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
}