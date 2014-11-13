package edu.utexas.egbom.wherewolf;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainGameActivity extends Activity{
	CircadianWidgetView circadianWidget;
	int currentTime;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_layout);
		
		ArrayList<Player> arrayOfPlayers = new ArrayList<Player>();
		
		arrayOfPlayers.add(new Player(1, "Dinner", "", 5));
		arrayOfPlayers.add(new Player(2,  "Post-Dinner","", 3));
		arrayOfPlayers.add(new Player(3, "Supper","", 1));
		arrayOfPlayers.add(new Player(4, "Appetizer", "", 0));
		
		//Create the adapter to convert the array to views
		PlayerAdapter adapter = new PlayerAdapter(this, arrayOfPlayers);
		// Attach the adapter to a ListView

		ListView playerListView = (ListView) findViewById(R.id.mainplayers);
		playerListView.setAdapter(adapter);
		circadianWidget = (CircadianWidgetView) findViewById(R.id.circadian);
		//Log.i(TAG, "made the widget");
		final SeekBar seekbar = (SeekBar) findViewById(R.id.daytime_seekbar);
		
		
		//get current time
		Calendar cal = Calendar.getInstance();
		currentTime = (cal.get(Calendar.HOUR_OF_DAY)*100) + cal.get(Calendar.MINUTE);
		//Log.i(TAG, "current time is "+currentTime);
		seekbar.setMax(2399);
		seekbar.setProgress(currentTime);
		seekbar.setOnSeekBarChangeListener(mySeekBarChangeListener());
	};
	
	private OnSeekBarChangeListener mySeekBarChangeListener(){
		return new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				//get time to change circadian widget
				double time = (double) progress/100;
				currentTime = progress;
				circadianWidget.changeTime(time);
				//Log.i(TAG, "current time is "+time);
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub	
			}
		};
	}
}
