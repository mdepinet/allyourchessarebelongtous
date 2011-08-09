package org.cwi.net.server;

import java.util.Map;

public class GameDescription {
	private long _ID;
	private Class<?> gameMode;
	private String gameMap;
	private int numTeams;
	private int playersPerTeam;
	private int numHumans;
	private Map<Integer, String> scoreMap;
	private String weaponSet;
	private boolean teamTakeover;
	private boolean playerTakeover;
	
	public GameDescription(long _ID, Class<?> gameMode, String gameMap, int numTeams, int playersPerTeam, int numHumans, Map<Integer, String> scoreMap, String weaponSet, boolean teamTakeover, boolean playerTakeover) {
		this._ID = _ID;
		this.gameMode = gameMode;
		this.gameMap = gameMap;
		this.numTeams = numTeams;
		this.playersPerTeam = playersPerTeam;
		this.numHumans = numHumans;
		this.scoreMap = scoreMap;
		this.weaponSet = weaponSet;
		this.teamTakeover = teamTakeover;
		this.playerTakeover = playerTakeover;
	}

	public long getID(){
		return _ID;
	}
	public void setID(long id){
		_ID = id;
	}
	public Class<?> getGameMode() {
		return gameMode;
	}
	public void setGameMode(Class<?> gameMode) {
		this.gameMode = gameMode;
	}
	public String getGameMap() {
		return gameMap;
	}
	public void setGameMap(String gameMap) {
		this.gameMap = gameMap;
	}
	public int getNumTeams() {
		return numTeams;
	}
	public void setNumTeams(int numTeams) {
		this.numTeams = numTeams;
	}
	public int getPlayersPerTeam() {
		return playersPerTeam;
	}
	public void setPlayersPerTeam(int playersPerTeam) {
		this.playersPerTeam = playersPerTeam;
	}
	public int getNumHumans() {
		return numHumans;
	}
	public void setNumHumans(int numHumans) {
		this.numHumans = numHumans;
	}
	public Map<Integer, String> getScoreMap() {
		return scoreMap;
	}
	public void setScoreMap(Map<Integer, String> scoreMap) {
		this.scoreMap = scoreMap;
	}
	public String getWeaponSet() {
		return weaponSet;
	}
	public void setWeaponSet(String weaponSet) {
		this.weaponSet = weaponSet;
	}
	public boolean isTeamTakeover() {
		return teamTakeover;
	}
	public void setTeamTakeover(boolean teamTakeover) {
		this.teamTakeover = teamTakeover;
	}
	public boolean isPlayerTakeover() {
		return playerTakeover;
	}
	public void setPlayerTakeover(boolean playerTakeover) {
		this.playerTakeover = playerTakeover;
	}
	
	
}
