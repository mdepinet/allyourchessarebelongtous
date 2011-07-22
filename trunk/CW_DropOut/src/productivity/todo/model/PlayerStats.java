package productivity.todo.model;

public class PlayerStats {
	private int numKills, numDeaths, numSuicides, shotsFired;
	public PlayerStats() {
		resetStats();
	}
	public void resetStats() {
		numKills = numDeaths = shotsFired = 0;
	}
	public int getNumKills() {
		return numKills;
	}
	public void incNumKills() {
		numKills++;
	}
	public void setNumKills(int numKills) {
		this.numKills = numKills;
	}
	public int getNumDeaths() {
		return numDeaths;
	}
	public void incNumDeaths() {
		numDeaths++;
	}
	public void setNumDeaths(int numDeaths) {
		this.numDeaths = numDeaths;
	}
	public int getNumSuicides() {
		return numSuicides;
	}
	public void incNumSuicides() {
		numSuicides++;
	}
	public void setNumSuicides(int numSuicides) {
		this.numSuicides = numSuicides;
	}
	public int getShotsFired() {
		return shotsFired;
	}
	public void incShotsFired() {
		shotsFired++;
	}
	public void setShotsFired(int shotsFired) {
		this.shotsFired = shotsFired;
	}
}
