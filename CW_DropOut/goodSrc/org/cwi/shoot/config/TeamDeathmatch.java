package org.cwi.shoot.config;

import java.awt.Graphics2D;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Weapon;

public class TeamDeathmatch extends GameMode {
	public static final int KILLS_TO_WIN = 10;
	@Override
	public String getModeName() {
		return "Team Deathmatch";
	}
	public void drawModeMap(Graphics2D g){
		
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
		return GameMap.teamNames[team]+": "+kills;
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
		for(Player p : players){
			int team = p.getTeam();
			teamKills.put(team, teamKills.get(team)+p.getStats().getKillsMinusSuicides());
		}
		for(int i : teamKills.keySet())
			if(teamKills.get(i)>=KILLS_TO_WIN)
				return i;
		return -1;
	}

	@Override
	public boolean canGetWeapon(Player p, Weapon w) {
		return true;
	}

	@Override
	public char[] getMapChars() {
		String chars = String.copyValueOf(Weapon.WEAPON_CHARS);
		chars+=String.copyValueOf(Player.SPAWNLOC_CHARS)+"X";
		return chars.toCharArray();
	}

	@Override
	public int getNumTeams() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addObjectives(GameMap map, Player p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPlayerDeath(Player p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPlayerRespawn(Player p) {
		// TODO Auto-generated method stub

	}

}
