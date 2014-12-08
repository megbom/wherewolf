package edu.utexas.egbom.wherewolf;

import android.os.Process;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Toast;


public class WherewolfService extends Service implements LocationListener{

	private static final int REPORT_PERIOD = 5000;
	private static final int MIN_CHANGE = 0;
	
	private WakeLock wakeLock;
	
	private LocationManager locationManager;
	
	private boolean isNight = false;
	private Game currentGame = null;
	private Player currentPlayer = null;
	
	private Looper mServiceLooper;
	private Handler mServicehandler;
	
	double latitude;
	double longitude;
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if (location != null){
			
			final String locMsg = "location changed " + location.getLatitude() + location.getLongitude();
			
			showLocation(locMsg);

            WherewolfPreferences pref = new WherewolfPreferences(WherewolfService.this);

			ChangedLocationRequest request = new ChangedLocationRequest(pref.getUsername(), pref.getPassword(), 
					location.getLatitude(), location.getLongitude(), pref.getCurrentGameID());
			
			ChangedLocationResponse response = request.execute( new WherewolfNetworking());
			
			if (response.getStatus().equals("success"))
			{
				pref.setTime(response.getCurrentTime());
			}
		}
	}
	 

	
	private void showLocation(String locMsg) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), locMsg, Toast.LENGTH_SHORT).show();
	}
	

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void onCreate(){
		super.onCreate();
		
		HandlerThread thread = new HandlerThread("wherewolfThread", Process.THREAD_PRIORITY_BACKGROUND);
		
		thread.start();
		
		mServiceLooper = thread.getLooper();
		mServicehandler = new Handler( mServiceLooper);
		
		locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");
		
		setNight();
		
	}
	
	public int onStartCommand(Intent intent, int flags, int startId){
		return START_STICKY;
	}
	
	
	@Override
	public void onDestroy(){
		locationManager.removeUpdates(listener);
	}
	
	private LocationListener listener = new LocationListener(){
		@Override
		public void onLocationChanged(Location location){
			
			if (location != null){
				latitude = location.getLatitude();
				longitude = location.getLongitude();
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
	};
	
	public void setNight()
	{
		mServicehandler.post(new Runnable(){
			@Override
			public void run()
			{
				
				wakeLock.acquire();
				
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, listener);
				isNight = true;
			}
			
		});
	}
	public void setDay(){
		
		mServicehandler.post(new Runnable(){
			@Override
			public void run(){
				
				if (isNight){
					
					if (wakeLock.isHeld()){
						wakeLock.release();
					}
					
					locationManager.removeUpdates(WherewolfService.this);
					
					isNight = false;
					Log.i("TAG", "Setting to day, turning off tracking");
				}
			}
		});
	}
	
	class ChangedLocationTask extends AsyncTask<ChangedLocationRequest, Void, ChangedLocationResponse> {

        @Override
        protected ChangedLocationResponse doInBackground(ChangedLocationRequest... request) {
        	
        	WherewolfPreferences pref = new WherewolfPreferences(WherewolfService.this);
            
            ChangedLocationRequest ChangedLocationRequest = new ChangedLocationRequest(pref.getUsername(),
            		pref.getPassword(), latitude, longitude, pref.getCurrentGameID());
            
            return request[0].execute(new WherewolfNetworking());
        }

        @Override
    	protected void onPostExecute(ChangedLocationResponse result) {
            
            if (result.getStatus().equals("success")) {
                
                WherewolfPreferences pref = new WherewolfPreferences(WherewolfService.this);
                pref.setCreds(pref.getUsername(), pref.getPassword());

            } else {
                // do something with bad password
               Toast.makeText(WherewolfService.this, "Error", Toast.LENGTH_LONG).show();
            }

        }
    }
		
}