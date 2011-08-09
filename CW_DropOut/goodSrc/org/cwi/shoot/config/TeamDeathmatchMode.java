package org.cwi.shoot.config;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.cwi.shoot.ai.objective.Objective;
import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Weapon;

public class TeamDeathmatchMode extends GameMode {
	public static final int KILLS_TO_WIN = 10;
	private int ktw = KILLS_TO_WIN;
	@Override
	public String getModeName() {
		return "Team Deathmatch";
	}
	@Override
	public String getScoreForPlayer(Player player) {
		return player.getName() + ": " + player.getStats().getNumKills();
	}

	@Override
	public String getScoreForTeam(int team, List<Player> players) {
		int kills = 0;
		for(int i = 0; i < players.size();i++)
			if(players.get(i).getTeam()==team)
				kills += players.get(i).getStats().getNumKills() - players.get(i).getStats().getNumSuicides();
		return ""+kills;
	}

	@Override
	public void loadGameObjects(GameMap map) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(List<Player> players) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getWinningTeam(List<Player> players) {
		Map<Integer, Integer> teamKills = new TreeMap<Integer, Integer>();
		for(int i = 0; i<players.size(); i++){
			Player p = players.get(i);
			int team = p.getTeam();
			if (teamKills.containsKey(team)) teamKills.put(team, teamKills.get(team)+p.getStats().getKillsMinusSuicides());
			else teamKills.put(team, p.getStats().getKillsMinusSuicides());
			
		}
		for(int i : teamKills.keySet())
			if(teamKills.get(i)>=ktw)
				return i;
		return -1;
	}

	@Override
	public boolean canGetWeapon(Player p, Weapon w) {
		return true;
	}

	@Override
	public List<Character> getAdditionalMapChars() {
		return new ArrayList<Character>();
	}

	@Override
	public int getMaxNumTeams() {
		return 10;
	}

	@Override
	public List<Objective> getObjectives(GameMap map, Player p) {
		return null;
	}

	@Override
	public void onPlayerDeath(Player p) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPlayerRespawn(Player p) {
		// TODO Auto-generated method stub

	}
	@Override
	public void drawModeMapPre(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void drawModeMapPost(Graphics2D g, List<Player> players) {
		// TODO Auto-generated method stub
		
	}
	public Map<String, Object> getOptions() {
		Map<String, Object> options = new HashMap<String, Object>();
		Integer[] limits = { KILLS_TO_WIN, 1, 1000 };
		options.put("Kills to Win:", limits);
		return options;
	}
	public void defineSettings(String key, Object value) {
		if(key.equals("Kills to Win:")) {
			ktw = Integer.parseInt((String) value);
		}
	}
}
