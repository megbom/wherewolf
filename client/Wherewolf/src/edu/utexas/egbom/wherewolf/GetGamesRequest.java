package edu.utexas.egbom.wherewolf;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class GetGamesRequest extends BasicRequest{
	

	  public GetGamesRequest (String username, String password)
	  {
	      super(username, password);
	  }
	  
	  /**
	  * Put the URL to your API endpoint here
	  */
	  @Override
	  public String getURL() {
	      return "/v1/get_games";
	  }

	  @Override
	  public List<NameValuePair> getParameters() {
	      return null;
	  }

	  @Override
	  public RequestType getRequestType() {
	      return RequestType.GET;
	  }

	  @Override
	  public GetGamesResponse execute(WherewolfNetworking net) {
	  
		  Log.i("REQUEST","GOT TO EXECUTE");
			 
	      try {
	          JSONObject jObject = net.sendRequest(this);
	          
	          String status = jObject.getString("status");
	          
	          if (status.equals("success"))
	          {
	        	  String games = jObject.getString("games");
	        	  Log.i("JOIN GAME REQUEST","was actually successful");
	              return new GetGamesResponse("success", "successfully retrieved games",games);
	          } else {
	        	  
	              String errorMessage = jObject.getString("error");
	              return new GetGamesResponse("failure", errorMessage);
	          }
	          
	      } catch (WherewolfNetworkException ex)
	      {
	    	  Log.i("REQUEST","could not communicate with server");
	          return new GetGamesResponse("failure", "could not communicate with server.");
	      } catch (JSONException e) {
	    	  
	    	  Log.i("REQUEST","could not parse JSON");
	          return new GetGamesResponse("failure", "could not parse JSON.");
	      }
	        
	  }

	}
