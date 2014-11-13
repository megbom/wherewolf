package edu.utexas.egbom.wherewolf;

	public class Player {

		  private int playerID;
		  private String name;
		  private String profilePic;
		  private int numVotes;
		public int getPlayerID() {
			return playerID;
		}
		public void setPlayerID(int playerID) {
			this.playerID = playerID;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getProfilePic() {
			return profilePic;
		}
		public Player(int playerID, String name, String profilePic, int numVotes) {
			super();
			this.playerID = playerID;
			this.name = name;
			this.profilePic = profilePic;
			this.numVotes = numVotes;
		}
		public void setProfilePic(String profilePic) {
			this.profilePic = profilePic;
		}
		public int getNumVotes() {
			return numVotes;
		}
		public void setNumVotes(int numVotes) {
			this.numVotes = numVotes;
		}

		  // getters and constructors go here

		}
