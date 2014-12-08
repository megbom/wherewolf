package edu.utexas.egbom.wherewolf;

public class Game {
	private String gameName;
	private String gameStatus;
	private int GameId;
	
	public Game( String gameStatus, int GameId, String gameName) {
		this.gameName = gameName;
		this.gameStatus = gameStatus;
		this.GameId = GameId;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public String getGameStatus() {
		return gameStatus;
	}

	public void setGameStatus(String gameStatus) {
		this.gameStatus = gameStatus;
	}

	public int getGameId() {
		return GameId;
	}

	public void setGameId(int gameId) {
		GameId = gameId;
	}
}


	

