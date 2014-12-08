package edu.utexas.egbom.wherewolf;

public class CreateUserResponse extends BasicResponse{
	private int playerID = -1;
	public CreateUserResponse(String status, String errorMessage){
			super(status,errorMessage);
	}
	public CreateUserResponse(String status, String errorMessage, int playerID) {
	      super(status, errorMessage);
	      
	      this.playerID = playerID;
	  }
	public int getPlayerID()
	  {
	      return playerID;
	  }

}
