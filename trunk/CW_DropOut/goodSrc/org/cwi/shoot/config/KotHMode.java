package org.cwi.shoot.config;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cwi.shoot.ai.objective.Objective;
import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Weapon;
import org.cwi.shoot.threads.RespawnThread;

public class KotHMode extends GameMode {
	public static final long TIME_TO_WIN = 30;
	public static final Character[] ZONE_CHARS = { 'X' };
	private Map<Point, Character> zone;
	private int teamControl;
	private long teamTime;
	
	public KotHMode() {
		zone = new HashMap<Point, Character>();
		teamControl = -1;
		teamTime = System.currentTimeMillis();
	}
	
	@Override
	public void loadGameObjects(GameMap map) {
		if(zone.keySet().isEmpty()) {
			int i = (int)(Math.random() * (int)(1/Math.random())*3)+1;
			char[][] charMap = map.getMap();
			Collection<Character> myChars = getAdditionalMapChars();
			modeMap = new char[charMap.length][charMap[0].length];
			int spotRow = (int)(charMap.length * Math.random());
			int spotCol = (int)(charMap[spotRow].length * Math.random());
			for (int r = spotRow; r < (spotRow + i > charMap.length ? charMap.length : spotRow + i); r++){
				for (int c = spotCol; c < (spotCol + i > charMap[r].length ? charMap[r].length : spotCol + i); c++){
					if (!myChars.contains(charMap[r][c])){
						modeMap[r][c] = charMap[r][c];
						zone.put(new Point(r,c), modeMap[r][c]);
						charMap[r][c] = '_';
					}
//					else {
//						relocateZone(map);
//						return;
//					}
				}
			}
		}
		else {
			for(Point i : zone.keySet()) {
				modeMap[i.x][i.y] = (char)(zone.get(i)+75);
			}
		}
	}
	
	
	public void onStartup(GameMap map, GameOptions setup){
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
		zone.clear();
		loadGameObjects(map);
	}

	@Override
	public String getModeName() {
		return "King of the Hill";
	}

	@Override
	public String getScoreForPlayer(Player player) {
		return player.getName() + ": " + player.getStats().getKillsMinusSuicides();
	}

	@Override
	public String getScoreForTeam(int team, List<Player> players) {
		// TODO Auto-generated method stub
		if(team == teamControl) return "" + (System.currentTimeMillis()-teamTime)/1000;
		return null;
	}

	@Override
	public void update(List<Player> players) {
		if(teamControl==-1) {
			for(Player p : players) {
				if(zone.containsKey(GameMap.getGridPoint(p.getLocation()))) {
					teamControl = p.getTeam();
					teamTime = System.currentTimeMillis();
				}
			}
		}
		else {
			boolean teamInZone = false;
			for(Player p : players) {
				if(p.getTeam()==teamControl && zone.containsKey(GameMap.getGridPoint(p.getLocation()))) {
					teamInZone = true;
				}
			}
			if(!teamInZone) {
				boolean inZone = false;
				for(Player p : players) {
					if(zone.containsKey(GameMap.getGridPoint(p.getLocation()))) {
						teamControl = p.getTeam();
						teamTime = System.currentTimeMillis();
					}
				}
				if(!inZone) {
					teamControl = -1;
					teamTime = 0;
				}
			}
		}
	}

	@Override
	public int getWinningTeam(List<Player> players) {
		if(teamControl!=-1 && (System.currentTimeMillis()-teamTime)/1000 >= TIME_TO_WIN) return teamControl;
		return -1;
	}

	@Override
	public boolean canGetWeapon(Player p, Weapon w) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List<Character> getAdditionalMapChars() {
		List<Character> characters = new ArrayList<Character>();
		for(Character c : ZONE_CHARS) {
			characters.add(c);
		}
		return characters;
	}

	@Override
	public int getMaxNumTeams() {
		// TODO Auto-generated method stub
		return 8;
	}

	@Override
	public List<Objective> getObjectives(GameMap map, Player p) {
		// TODO Auto-generated method stub
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
		g.setColor(new Color(0f,1f,0f,0.5f));
		for(Point p : zone.keySet())
			g.fillRect((int)p.getX()*GameMap.GRID_PIXELS, (int)p.getY()*GameMap.GRID_PIXELS, GameMap.GRID_PIXELS, GameMap.GRID_PIXELS);
	}

	@Override
	public void drawModeMapPost(Graphics2D g, List<Player> players) {
		// TODO Auto-generated method stub
	}

}
