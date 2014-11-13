package edu.utexas.egbom.wherewolf;

/*package edu.utexas.boukhonine.wherewolf;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class MainScreenActivity extends Activity {

	private static final String TAG = "mainscreenactivity";
	
	ArrayList<Player> arrayOfPlayers;
	PlayerAdapter adapter;
	int currentTime;
	CircadianWidgetView circadianWidget;
	
	/** Called when the activity is first created. 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
		Log.i(TAG, "in main screen");
		arrayOfPlayers = new ArrayList<Player>();

		// Create the adapter to convert the array to views
		adapter = new PlayerAdapter(this, arrayOfPlayers);
		
		// Attach the adapter to a ListView
		ListView playerListView = (ListView) findViewById(R.id.players_list);
		playerListView.setAdapter(adapter);
		View header = (View)getLayoutInflater().inflate(R.layout.main_activity_header, null);
		playerListView.addHeaderView(header);
		
		//populate array with fake data
		addPlayer(1, "malevillager3", "Tom", 5);
		addPlayer(2, "malevillager3", "George", 3);
		addPlayer(3, "femalevillager", "Abigail", 1);
		addPlayer(4, "femalevillager", "Martha", 0);
		
		circadianWidget = (CircadianWidgetView) findViewById(R.id.circadian);
		Log.i(TAG, "made the widget");
		final SeekBar seekbar = (SeekBar) findViewById(R.id.daytime_seekbar);
		
		
		//get current time
		Calendar cal = Calendar.getInstance();
		currentTime = (cal.get(Calendar.HOUR_OF_DAY)*100) + cal.get(Calendar.MINUTE);
		Log.i(TAG, "current time is "+currentTime);
		seekbar.setMax(2399);
		seekbar.setProgress(currentTime);
		
		//set on change listener for seek bar
		seekbar.setOnSeekBarChangeListener(mySeekBarChangeListener());

		// create player listview listener
		playerListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// add attack and vote capabilities
				
                Player p = adapter.getItem((int) id);
                Log.i(TAG, "clicked on player " + p.getName());
                if (isNight()==1){ 
                	Log.i(TAG, "It's night");
                	// add werewolf attack
                	Toast.makeText(getApplicationContext(), "Attacking!!!",Toast.LENGTH_LONG).show();
                } else {
                	Log.i(TAG, "It's day");
                	// add voting
                	int votes = (int)p.getNumVotes();
                    p.setNumVotes(votes+1);
                	adapter.notifyDataSetChanged();
                    Log.i(TAG, "player " + p.getName() + " has " + p.getNumVotes()+" votes");
                }
			}
		});
	}
	
	private OnSeekBarChangeListener mySeekBarChangeListener(){
		return new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				//get time to change circadian widget
				double time = (double) progress/100;
				currentTime = progress;
				circadianWidget.changeTime(time);
				Log.i(TAG, "current time is "+time);
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

	public void addPlayer(int id, String img, String name, int numVotes) {
	    Log.i(TAG, ""+numVotes);
		adapter.add(new Player(id, img, name, numVotes));
		adapter.notifyDataSetChanged();
	}
	
	public int isNight(){
		return (currentTime > 700 && currentTime< 2000)? 0:1;
	}

}*/

