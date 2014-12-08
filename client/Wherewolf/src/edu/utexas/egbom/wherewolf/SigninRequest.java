package edu.utexas.egbom.wherewolf;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class SigninRequest extends BasicRequest {

  public SigninRequest (String username, String password)
  {
      super(username, password);
  }
  
  /**
  * Put the URL to your API endpoint here
  */
  @Override
  public String getURL() {
      return "/v1/check_password";
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
  public SigninResponse execute(WherewolfNetworking net) {
  
	  Log.i("TAG","Testing Signin Response");
      try {
    	  
          JSONObject response = net.sendRequest(this);
          Log.i("TAG","First Try");
          
          if (response.getString("status").equals("success"))
          {
              //int playerID = response.getInt("playerid");
        	  Log.i("TAG","sign in success");
              return new SigninResponse("success", "signed in successfully");
          } else {
              
              String errorMessage = response.getString("error");
              Log.i("TAG","error Message 1");
              return new SigninResponse("failure", errorMessage);
          }
      } catch (JSONException e) {
    	  Log.i("TAG","error Message 2");
          return new SigninResponse("failure", "sign in not working");
      } catch (WherewolfNetworkException ex)
      {
    	  Log.i("TAG","catch");
          return new SigninResponse("failure", "could not communicate with the server");
      }
        
  }

}