public class ScoreRecord {
	int score;
	String playerName;
	
	public ScoreRecord(String inputName, int inputScore) {
		this.playerName = inputName;
		this.score = inputScore;
	}
	
	public int getScore() {
		return score;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public void setPlayerName(String inputName) {
		this.playerName = inputName;
	}
}
