package org.cwi.shoot.config;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Weapon;
import org.cwi.shoot.threads.RespawnThread;

public abstract class GameMode {
	public static final List<Class<? extends GameMode>> availableTypes = new LinkedList<Class<? extends GameMode>>();
	static{
		availableTypes.add(TeamDeathmatchMode.class);
		availableTypes.add(CaptureTheFlagMode.class);
		availableTypes.add(ZombiesWGuns.class);
	}

	protected char[][] modeMap;
	protected GameMap gameMap;
	protected static List<RespawnThread> threads = Collections.synchronizedList(new ArrayList<RespawnThread>());;
	
	public GameMode() {
	}
	
	public abstract String getModeName();
	public abstract String getScoreForPlayer(Player player);
	public abstract String getScoreForTeam(int team, List<Player> players);
	public abstract void update(List<Player> players);
	public abstract int getWinningTeam(List<Player> players);
	public abstract boolean canGetWeapon(Player p, Weapon w);
	public abstract char[] getAdditionalMapChars();
	public abstract int getMaxNumTeams();
	public abstract void addObjectives(GameMap map, Player p);
	public abstract void onPlayerDeath(Player p);
	public abstract void onPlayerRespawn(Player p);
	public abstract void drawModeMapPre(Graphics2D g);
	public abstract void drawModeMapPost(Graphics2D g, List<Player> players);
	
	public void onStartup(GameMap map, GameOptions setup){
		gameMap = map;
		for(int i = 1; i<=Math.min(setup.getNumTeams(), getMaxNumTeams()); i++) {
			for(int j = 0; j < setup.getPlayersPerTeam(); j++) {
				if(i == setup.getPlayerTeam() && j == 0) continue;
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
		for(int i = 0; i < threads.size(); i++) { 
			RespawnThread t = threads.get(i); 
			t.respawn(); 
			t.kill(); 
		}
		threads.clear();
		for(int i = 1; i<=Math.min(setup.getNumTeams(), getMaxNumTeams()); i++) {
			for(int j = 0; j < setup.getPlayersPerTeam(); j++) {
				if(i == setup.getPlayerTeam() && j == 0) continue;
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
	
	public void showGameEndDialog(GameMap map, int winner){
		Weapon w = new Weapon((char)(winner+75), new Point());
		JOptionPane.showMessageDialog(null, GameMap.teamNames[winner-1] + " Wins!", "Game over!", 0, new ImageIcon(Weapon.getWeaponImg(w.getImgLoc())));
	}
	
	public int getNumPlayersOnTeam(int team, List<Player> players){
		int count = 0;
		for (Player p : players){
			if (p.getTeam() == team) count++;
		}
		return count;
	}
	
	public void loadGameObjects(GameMap map){
		char[][] charMap = map.getMap();
		Set<Character> myChars = new HashSet<Character>();
		char[] myCharsArray = getAdditionalMapChars();
		for (char c : myCharsArray){
			myChars.add(c);
		}
		modeMap = new char[charMap.length][charMap[0].length];
		for (int r = 0; r < charMap.length; r++){
			for (int c = 0; c < charMap[r].length; c++){
				if (myChars.contains(charMap[r][c])){
					modeMap[r][c] = charMap[r][c];
					charMap[r][c] = '_';
				}
			}
		}
	}
}
