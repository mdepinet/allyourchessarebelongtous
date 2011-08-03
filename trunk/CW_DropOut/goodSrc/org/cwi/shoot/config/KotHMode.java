package org.cwi.shoot.config;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cwi.shoot.ai.objective.KillObjective;
import org.cwi.shoot.ai.objective.LocationObjective;
import org.cwi.shoot.ai.objective.Objective;
import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Weapon;
import org.cwi.shoot.threads.RespawnThread;

public class KotHMode extends GameMode {
	public static final long TIME_TO_WIN = 91;
	public static final Character[] ZONE_CHARS = { 'X' };
	private static final double OBJECTIVE_WEIGHT = 15;
	private static final double DEFEND_OBJECTIVE_WEIGHT = 20;
	public static final long HILL_IDLE_TIME = 5;

	private Map<Integer, Integer> teamPoints = new HashMap<Integer, Integer>();
	private Map<Point, Character> zone;
	private int teamControl;
	private long startTeamTime;
	private int teamTime;
	private List<Player> inTheZone;
	private long timeIdle;
	
	public KotHMode() {
		zone = new HashMap<Point, Character>();
		teamControl = -1;
		teamTime = -1;
		startTeamTime = -1;
		inTheZone = new ArrayList<Player>();
		timeIdle = 0;
	}
	
	public void moveHill() {
		zone = new HashMap<Point, Character>();
		int i = (int)(Math.random() * 4)+2;
		Collection<Character> myChars = getAdditionalMapChars();
		int spotRow = (int)(modeMap.length * Math.random());
		int spotCol = (int)(modeMap[spotRow].length * Math.random());
		for (int r = spotRow; r < (spotRow + i > modeMap.length ? modeMap.length : spotRow + i); r++){
			for (int c = spotCol; c < (spotCol + i > modeMap[r].length ? modeMap[r].length : spotCol + i); c++){
				if (!myChars.contains(modeMap[r][c])){
					//modeMap[r][c] = modeMap[r][c];
					zone.put(new Point(r,c), modeMap[r][c]);
					//charMap[r][c] = '_';
				}
			}
		}
	}
	
	@Override
	public void loadGameObjects(GameMap map) {
		if(zone.keySet().isEmpty()) {
			int i = (int)(Math.random() * 4)+2;
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
		if(setup.getPlayerTeam()==-1) map.getPlayers().remove(map.getPlayer());
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
		teamPoints = new HashMap<Integer, Integer>();
		teamTime = -1;
		startTeamTime = -1;
		teamControl = -1;
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
		Integer i = teamPoints.get(team);
		i = (i == null ? 0 : i);
		return ""+(teamControl != -1 && teamControl == team ? i+teamTime : i);
	}

	@Override
	public void update(List<Player> players) {
		if(teamControl==-1) {
			for(Player p : players) {
				if(zone.containsKey(GameMap.getGridPoint(p.getLocation()))) {
					teamControl = p.getTeam();
					startTeamTime = startTeamTime==-1 ? System.currentTimeMillis() : startTeamTime;
					teamTime = (int) ((System.currentTimeMillis() - startTeamTime)/1000);
					inTheZone.add(p);
					timeIdle = -1;
				}
				else if(inTheZone.contains(p) && !zone.containsKey(GameMap.getGridPoint(p.getLocation())))
					inTheZone.remove(p);
			}
			timeIdle = timeIdle == -1 ? System.currentTimeMillis() : timeIdle;
			if(((System.currentTimeMillis() - timeIdle)/1000) > HILL_IDLE_TIME) {
				timeIdle = -1;
				moveHill();
			}
		}
		else {
			boolean teamInZone = false;
			boolean contested = false;
			for(Player p : players) {
				if(p.getTeam()==teamControl && zone.containsKey(GameMap.getGridPoint(p.getLocation()))) {
					teamInZone = true;
					inTheZone.add(p);
				}
				else if(p.getTeam()!=teamControl && zone.containsKey(GameMap.getGridPoint(p.getLocation()))) contested = true;
			}
			if(teamInZone && !contested) teamTime = (int) ((System.currentTimeMillis() - startTeamTime)/1000);
			else if(teamInZone && contested) {
				Integer i = teamPoints.get(teamControl);
				i = (i == null ? 0 : i);
				teamPoints.put(teamControl, (int) (teamControl != -1 ? i+teamTime : i));
				teamControl = -1;
				teamTime = -1;
				startTeamTime = -1;
			}
			else if(!teamInZone) {
				boolean inZone = false;
				for(Player p : players) {
					if(zone.containsKey(GameMap.getGridPoint(p.getLocation()))) {
						Integer i = teamPoints.get(teamControl);
						i = (i == null ? 0 : i);
						teamPoints.put(teamControl, (int) (teamControl != -1 ? i+teamTime : i));
						teamControl = p.getTeam();
						startTeamTime = System.currentTimeMillis();
						teamTime = (int) ((System.currentTimeMillis() - startTeamTime)/1000);
					}
				}
				if(!inZone) {
					Integer i = teamPoints.get(teamControl);
					i = (i == null ? 0 : i);
					teamPoints.put(teamControl, (int) (teamControl != -1 ? i+teamTime : i));
					teamControl = -1;
					teamTime = -1;
					startTeamTime = -1;
					inTheZone.clear();
				}
			}
		}
	}

	@Override
	public int getWinningTeam(List<Player> players) {
		if (teamTime >= TIME_TO_WIN) return teamControl;
		for (Map.Entry<Integer,Integer> entry : teamPoints.entrySet()){
			if (entry.getValue() + ((teamControl == entry.getKey()) ? teamTime : 0) >= TIME_TO_WIN) return entry.getKey();
		}
		return -1;
	}

	@Override
	public boolean canGetWeapon(Player p, Weapon w) {
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
		return 8;
	}

	@Override
	public List<Objective> getObjectives(GameMap map, Player p) {
		LinkedList<Objective> objectives = new LinkedList<Objective>();
		if (teamControl == -1){
			List<Point> locs = new ArrayList<Point>();
			for(Point pp : zone.keySet())
				locs.add(pp);
			Point point = locs.get((int)(Math.random()*locs.size()));
			if (point != null) objectives.add(new LocationObjective(OBJECTIVE_WEIGHT,GameMap.getGridPoint(p.getLocation()).distance(point),GameMap.fromGridPoint(point)));
		}
		else{
			List<Point> locs = new ArrayList<Point>();
			for(Point pp : zone.keySet())
				locs.add(pp);
			Point point = locs.get((int)(Math.random()*locs.size()));
			if (p.getTeam() != teamControl){
				//Run away somehow
				for(int i = 0; i < inTheZone.size(); i++)
					if(inTheZone.get(i).getTeam()!=p.getTeam())
						objectives.add(new KillObjective(OBJECTIVE_WEIGHT,GameMap.getGridPoint(p.getLocation()).distance(point),inTheZone.get(i)));
				Player target = map.getClosestNonTeamPlayer(p.getTeam(), p.getLocation());
				if(target!=null)
					objectives.add(new KillObjective(OBJECTIVE_WEIGHT,GameMap.getGridPoint(p.getLocation()).distance(point),target));
			}
			else if(inTheZone.indexOf(p)==0) {
				objectives.add(new LocationObjective(DEFEND_OBJECTIVE_WEIGHT,GameMap.getGridPoint(p.getLocation()).distance(point),GameMap.fromGridPoint(point)));
			}
			else if(!zone.containsKey(GameMap.getGridPoint(p.getLocation())) && !p.equals(inTheZone.get(0))){
				Player target = map.getClosestNonTeamPlayer(p.getTeam(), inTheZone.get(0).getLocation());
				if(target!=null)
					objectives.add(new KillObjective(DEFEND_OBJECTIVE_WEIGHT,GameMap.getGridPoint(p.getLocation()).distance(point),target));
			}
		}
		return objectives;
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