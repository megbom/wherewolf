package edu.utexas.egbom.wherewolf;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class ChangedLocationRequest extends BasicRequest{
	
	double longitude;
	double latitude;
	int	gameId;

	public ChangedLocationRequest (String username, String password, double latitude, 
			double longitude, int gameId)
	{
		super(username, password);
		
		this.latitude = latitude;
		this.longitude = longitude;
		this.gameId = gameId;
	}

	/**
	 * Put the URL to your API endpoint here
	 */
	@Override
	public String getURL() {
		return "/v1/"+this.gameId+"update";
	}

	@Override
	public List<NameValuePair> getParameters() {
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("username", username));
		urlParameters.add(new BasicNameValuePair("password", password));
		urlParameters.add(new BasicNameValuePair("latitude", Double.toString(latitude)));
		urlParameters.add(new BasicNameValuePair("longitude", Double.toString(longitude)));
		urlParameters.add(new BasicNameValuePair("gameId", Double.toString(gameId)));
		
		return urlParameters;
	}

	@Override
	public RequestType getRequestType() {
		return RequestType.PUT;
	}

	@Override
	public ChangedLocationResponse execute(WherewolfNetworking net) {
		
		try {

			JSONObject response = net.sendRequest(this);

			if (response.getString("status").equals("success"))
			{
				Log.i("","sign in success");
				return new ChangedLocationResponse("success", "signed in successfully");
			} else {

				String errorMessage = response.getString("error");
				Log.i("","error Message 1");
				return new ChangedLocationResponse("failure", errorMessage);
			}
		} catch (JSONException e) {
			Log.i("","error Message 2");
			return new ChangedLocationResponse("failure", "sign in not working");
		} catch (WherewolfNetworkException ex)
		{
			Log.i("","catch");
			return new ChangedLocationResponse("failure", "could not communicate with the server");
		}

	}

}