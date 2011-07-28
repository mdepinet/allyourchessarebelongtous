package org.cwi.shoot.config;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Weapon;

public class SurvivalMode extends GameMode {
	private static final float DAMAGE_MOD = .4f;
	private List<Player> dead;
	private Set<Weapon> weps;
	
	public SurvivalMode(){
		super();
		dead = new ArrayList<Player>();
		weps = new HashSet<Weapon>();
	}
	@Override
	public String getModeName() {
		return "Survival";
	}
	public void drawModeMap(Graphics2D g){
		
	}
	public void onStartup(GameMap map, GameOptions setup){
		super.onStartup(map,setup);
		Player.setRegenSpeed(0);
	}
	@Override
	public String getScoreForPlayer(Player player) {
		return player.getName() + ": " + player.getStats().getNumKills();
	}

	@Override
	public String getScoreForTeam(int team, List<Player> players) {
		int alive = 0;
		for(int i = 0; i < players.size();i++)
			if(players.get(i).getTeam()==team && players.get(i).getHealth()>0)
				alive++;
		return ""+alive;
				
	}

	@Override
	public void loadGameObjects(GameMap map) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(List<Player> players) {
		for(Player p : players){
			Weapon wep = p.getCurrWeapon();
			if(weps.add(wep))
				wep.setPower((int)(wep.getPower()*DAMAGE_MOD));
		}
	}

	@Override
	public int getWinningTeam(List<Player> players) {
		Map<Integer, Integer> membersAlive = new TreeMap<Integer, Integer>();
		for(Player p : players){
			if(p.getHealth()>0){
				int team = p.getTeam();
				if (membersAlive.containsKey(team)) membersAlive.put(team, membersAlive.get(team)+1);
				else membersAlive.put(team, 1);
			}
		}
		int numAlive = 0;
		int winTeam = -1;
		for(int i : membersAlive.keySet())
			if(membersAlive.get(i)!=null){
				numAlive++;
				winTeam=i;
			}
		if(numAlive != 1)
			return -1;
		return winTeam;
	}
	public void onReset(GameMap map, GameOptions setup){
		//gameOver = false;
		for(Player p : dead){
			map.getPlayers().add(p);
			map.spawn(p);
		}
		weps.clear();
		dead.clear();
	}
	@Override
	public boolean canGetWeapon(Player p, Weapon w) {
		return true;
	}

	@Override
	public char[] getAdditionalMapChars() {
		return new char[]{'+'};
	}

	@Override
	public int getMaxNumTeams() {
		return 10;
	}

	@Override
	public void addObjectives(GameMap map, Player p) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onPlayerDeath(Player p) {
		dead.add(p);
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
	public boolean handlesRespawn(){
		return true;
	}
}
