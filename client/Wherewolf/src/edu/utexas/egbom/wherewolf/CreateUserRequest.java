package edu.utexas.egbom.wherewolf;

	import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import edu.utexas.egbom.wherewolf.BasicRequest;

	public class CreateUserRequest extends BasicRequest {

	  private String firstname;
	private String lastname;

	public CreateUserRequest (String username, String password, String firstname, String lastname)
	  {
	      super(username, password);
	      this.firstname=firstname;
	      this.lastname=lastname;
	  }
	  
	  /**
	  * Put the URL to your API endpoint here
	  */
	  @Override
	  public String getURL() {
	      return "/v1/register";
	  }

	  @Override
	  public List<NameValuePair> getParameters() {
		  List<NameValuePair> urlParameters=new ArrayList<NameValuePair>();
	      urlParameters.add(new BasicNameValuePair("username",username));
	      urlParameters.add(new BasicNameValuePair("password",password));
	      urlParameters.add(new BasicNameValuePair( "firstname", firstname));
	      urlParameters.add(new BasicNameValuePair("lastname", lastname));
		  return urlParameters;
	  }

	  @Override
	  public RequestType getRequestType() {
	      return RequestType.POST;
	  }

	  @Override
	  public CreateUserResponse execute(WherewolfNetworking net) {
	  
	      try {
	          JSONObject response = net.sendRequest(this);
	          
	          if (response.getString("status").equals("success"))
	          {
	              // int playerID = response.getInt("playerid");
	              return new CreateUserResponse("success", "signed in successfully");
	          } else {
	              
	              String errorMessage = response.getString("error");
	              return new CreateUserResponse("failure", errorMessage);
	          }
	      } catch (JSONException e) {
	          return new CreateUserResponse("failure", "sign in not working");
	      } catch (WherewolfNetworkException ex)
	      {
	          return new CreateUserResponse("failure", "could not communicate with the server");
	      }
	      
	      
	      
	  }

	}

