package org.cwi.shoot.config;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Weapon;

public abstract class GameMode {
	public static final List<Class<? extends GameMode>> availableTypes = new LinkedList<Class<? extends GameMode>>();

	protected char[][] modeMap;
	
	public GameMode() {
		availableTypes.add(this.getClass());
	}
	
	public abstract String getModeName();
	public abstract String getScoreForPlayer(Player player);
	public abstract String getScoreForTeam(int team, List<Player> players);
	public abstract void loadGameObjects(GameMap map);
	public abstract void update(List<Player> players);
	public abstract int getWinningTeam(List<Player> players);
	public abstract boolean canGetWeapon(Player p, Weapon w);
	public abstract char[] getMapChars();
	public abstract int getNumTeams();
	public abstract void addObjectives(GameMap map, Player p);
	public abstract void onPlayerDeath(Player p);
	public abstract void onPlayerRespawn(Player p);
	public abstract void drawModeMap(Graphics2D g);
	
	public void onStartup(GameMap map, GameOptions setup){
		for(int i = 1; i<getNumTeams(); i++) {
			for(int j = 0; j < getNumPlayersOnTeam(i, map.getPlayers()); j++) {
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
		for(int i = 1; i<getNumTeams(); i++) {
			for(int j = 0; j < getNumPlayersOnTeam(i, map.getPlayers()); j++) {
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
		JOptionPane.showMessageDialog(null, GameMap.teamNames[winner-1] + " Wins!", "Game over!", 0, new ImageIcon(new Weapon((char)(winner+75), new Point()).getImage()));
	}
	
	public int getNumPlayersOnTeam(int team, List<Player> players){
		int count = 0;
		for (Player p : players){
			if (p.getTeam() == team) count++;
		}
		return count;
	}
}
