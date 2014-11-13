package edu.utexas.egbom.wherewolf;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class CreateGameActivity extends Activity{
	
	
	private static final String TAG = "creating game activity";
	public void stopGameCreate()
	{
		Log.v(TAG, "closing the create game screen");
		this.finish();
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
}
