package org.cwi.shoot.config;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.cwi.shoot.ai.objective.Objective;
import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Weapon;
import org.cwi.shoot.threads.RespawnThread;

public class ComputerPlayersOnlyMode extends GameMode {
	public static final int KILLS_TO_WIN = 10;
	
	
	public void onStartup(GameMap map, GameOptions setup){
		map.getPlayers().clear();
		for(int i = 1; i<=Math.min(setup.getNumTeams(), getMaxNumTeams()); i++) {
			for(int j = 0; j < setup.getPlayersPerTeam(); j++) {
				//if(i == setup.getPlayerTeam() && j == 0) continue;
				Player p2 = new Player(setup.getNameGen().compose((int)(Math.random()*3)+2));
				p2.setTeam(i);
				map.getPlayers().add(p2);
			}
		}
		for(int i = 0; i < map.getPlayers().size();i++) {
			Player p = map.getPlayers().get(i);
			if(map.getSpawnLocs().get(p.getTeam())!=null && !map.getSpawnLocs().get(p.getTeam()).isEmpty()) map.spawn(p);
		}
	}
	public void onReset(GameMap map, GameOptions setup){
		for(Player p : map.getPlayers()) {
			p.reset();
			map.spawn(p);
		}
		if (!handlesRespawn()){
			for(int i = 0; i < map.getThreads().size(); i++) { 
				RespawnThread t = map.getThreads().get(i); 
				t.respawn(); 
				t.resetPlayer();
				t.kill(); 
			}
			map.getThreads().clear();
		}
	}
	
	
	@Override
	public String getModeName() {
		return "Computer Players Only...";
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
			if(teamKills.get(i)>=KILLS_TO_WIN)
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
}
