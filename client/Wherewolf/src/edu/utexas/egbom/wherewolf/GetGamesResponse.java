package edu.utexas.egbom.wherewolf;

public class GetGamesResponse extends BasicResponse{
	
	String Games;
	
	  public GetGamesResponse(String status, String errorMessage) {
	      super(status, errorMessage);
	  }
	  
	  public GetGamesResponse(String status, String errorMessage, String Games) {
	      super(status, errorMessage);
	      this.Games = Games;
	  }
	  
	  public String getGames()
	  {
	      return Games;
	  }
}
