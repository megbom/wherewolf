package edu.utexas.egbom.wherewolf;

import java.util.Calendar;

public class ChangedLocationResponse extends BasicResponse{		  

	public ChangedLocationResponse(String status, String errorMessage) {
		super(status, errorMessage);
	}

	public long getCurrentTime() {
		// TODO Auto-generated method stub
		Calendar c = Calendar.getInstance(); 
		int hour = c.get(Calendar.HOUR_OF_DAY);
		return hour;
	}
}
