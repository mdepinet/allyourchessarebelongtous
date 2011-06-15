import java.io.Serializable;


public class Score implements Serializable, Comparable<Score> {
	private static final long serialVersionUID = 1L;
	private int score;
	private String player;
	public boolean cheat = false;
//	private boolean didCheat;

	public Score(int score)
	{
		setScore(score);
		setPlayer(null);
//		didCheat = false;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
//	
//	public boolean isDidCheat() {
//		return didCheat;
//	}
//
//	public void setDidCheat(boolean didCheat) {
//		this.didCheat = didCheat;
//	}
	
	public int compareTo(Score s)
	{
		return ((Integer)s.getScore()).compareTo(score);
	}
	public String toString() {
		return player+"=/"+score;
	}
}
