package org.cwi.shoot.model;

public class PlayerStats implements Comparable<PlayerStats> {
	private int numKills, numDeaths, numSuicides, shotsFired;
	public PlayerStats() {
		resetStats();
	}
	public void resetStats() {
		numKills = numDeaths = numSuicides = shotsFired = 0;
	}
	public int compareTo(PlayerStats stats)
	{
		if(numKills-stats.getNumKills()!=0)
			return stats.getNumKills()-numKills;
		else if(numDeaths-stats.getNumDeaths()!=0)
			return numDeaths-stats.getNumDeaths();
		else
			return numSuicides-stats.getNumSuicides();
	}
	public int getKillsMinusSuicides(){
		return numKills-numSuicides;
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
