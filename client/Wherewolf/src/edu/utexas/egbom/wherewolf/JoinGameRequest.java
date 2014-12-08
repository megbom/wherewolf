package edu.utexas.egbom.wherewolf;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JoinGameRequest extends BasicRequest{
	
	private int gameId;
	
	public JoinGameRequest (String username, String password, int clickedGameId)
	  {
	      super(username, password);
	      this.gameId = clickedGameId;
	      
	  }

	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		return "/v1/game/"+gameId+"/lobby";
		//return null;
	}

	@Override
	public List<NameValuePair> getParameters() {

		return null;
	}

	@Override
	public RequestType getRequestType() {
		// TODO Auto-generated method stub
		return RequestType.POST;
	}
	
	 @Override
	  public JoinGameResponse execute(WherewolfNetworking net) {
		 
	     
		 Log.i("REQUEST","GOT TO EXECUTE");
		 
	      try {
	          JSONObject jObject = net.sendRequest(this);
	          
	          String status = jObject.getString("status");
	          
	          if (status.equals("success"))
	          {
	        	  Log.i("JOIN GAME REQUEST","was actually successful");
	              return new JoinGameResponse("success", "created game");
	          } else {
	        	  
	              String errorMessage = jObject.getString("error");
	              return new JoinGameResponse("failure", errorMessage);
	          }
	          
	      } catch (WherewolfNetworkException ex)
	      {
	    	  Log.i("REQUEST","could not communicate with server");
	          return new JoinGameResponse("failure", "could not communicate with server.");
	      } catch (JSONException e) {
	    	  
	    	  Log.i("REQUEST","could not parse JSON");
	          return new JoinGameResponse("failure", "could not parse JSON.");
	      }
	      
	  }
}
