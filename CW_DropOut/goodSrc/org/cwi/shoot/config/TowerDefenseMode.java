package org.cwi.shoot.config;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.cwi.shoot.ai.ZombieBrain;
import org.cwi.shoot.ai.mike.SmartBrain;
import org.cwi.shoot.ai.objective.KillObjective;
import org.cwi.shoot.ai.objective.LocationObjective;
import org.cwi.shoot.ai.objective.Objective;
import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Player.PlayerType;
import org.cwi.shoot.model.Weapon;
import org.cwi.shoot.threads.RespawnThread;
import org.cwi.shoot.util.NameGenerator;
import org.cwi.shoot.util.OtherNameGenerator;

public class TowerDefenseMode extends GameMode {
	public static final int NUM_ENEMIES = 10;
	public static final int ADD_NUM_ZOMBIES = 20;
	public static final int WAVE_TIME_LIMIT = 30;
	public static final int ZOMBIES_TEAM_NUM = 0;
	private int num_zombies_to_add = ADD_NUM_ZOMBIES;
	private int wave_time = WAVE_TIME_LIMIT;
	public static final long TIME_TO_LOSE = 10;
	public static final Character[] ZONE_CHARS = { 'X' };
	private static final double OBJECTIVE_WEIGHT = 15;
	
	private long startTime;
	private int wave;
	private long waveStartTime;
	protected List<Player> deadZombies;
	protected List<Point> spawnLocs;
	protected boolean humanPlaying;
	protected Player originalPlayer;
	protected Map<String, Object> stats;
	private Map<Point, Character> zone;
	private List<Player> inTheZone;
	private int teamControl;
	private long startTeamTime;
	private long teamTime;
	
	public TowerDefenseMode() {
		wave = 1;
		deadZombies = new ArrayList<Player>();
		spawnLocs = new ArrayList<Point>();
		stats = new HashMap<String, Object>();
		zone = new HashMap<Point, Character>();
		inTheZone = new ArrayList<Player>();
		teamControl = -1;
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
		for(int r = 0; r < modeMap.length; r++) 
			for(int c = 0; c < modeMap[r].length; c++)
				if(modeMap[r][c]!='X' && GameMap.getGridPoint(map.getPlayer().getLocation()).x!=c && GameMap.getGridPoint(map.getPlayer().getLocation()).y !=r)
					spawnLocs.add(new Point(r,c));
		if(setup.getPlayerTeam()!=-1) {
			map.spawn(map.getPlayer());
			map.getPlayer().addWeapon(new Weapon('T', new Point()), this);
			map.getPlayer().setCurrWeapon(new Weapon('T', new Point()));
			humanPlaying = true;
			originalPlayer = map.getPlayer();
		}
		else {
			map.getPlayers().clear();
			Player p = new Player(setup.getNameGen().compose((int)(Math.random()*3)+2), 1);
			p.setTeam(1);
			p.setBrain(new SmartBrain());
			map.getPlayers().add(p);
			map.spawn(p);
			map.getPlayers().get(0).addWeapon(new Weapon('T', new Point()), this);
			map.getPlayers().get(0).setCurrWeapon(new Weapon('T', new Point()));
			humanPlaying = false;
			originalPlayer = map.getPlayers().get(0);
		}
		startTime = System.currentTimeMillis();
		waveStartTime = System.currentTimeMillis();
	}
	public void onReset(GameMap map, GameOptions setup){
		map.getPlayers().clear();
		for(int i = 0; i < map.getThreads().size(); i++) { 
			RespawnThread t = map.getThreads().get(i); 
			t.kill(); 
		}
		map.getThreads().clear();
		
		if(humanPlaying) {
			setup.getProfile().addStats(stats);
			setup.getProfile().writeToFile();
		}
		map.getPlayers().clear();
		map.getPlayers().add(0,originalPlayer);
		map.getPlayers().get(0).getWeapons().clear();
		map.spawn(originalPlayer);
		map.getPlayers().get(0).removeWeapon(new Weapon("Default"));
		map.getPlayers().get(0).addWeapon(new Weapon('T', new Point()), this);
		map.getPlayers().get(0).setCurrWeapon(new Weapon('T', new Point()));
		
		
		setWave(1);
		setWaveStartTime(System.currentTimeMillis());
		setStartTime(System.currentTimeMillis());
	}
	
	public void createZombieList() {
		deadZombies = new ArrayList<Player>();
	}
	public void respawnZombies() {
		if(deadZombies.size()>0) {
			for(int i = 0; i < deadZombies.size(); i++)
				if(deadZombies.get(i).getHealth()<=0) {
					deadZombies.remove(i--);
				}
		}
	}
	public List<Player> addZombies(int num) {
		ArrayList<Player> zombiesToAdd = new ArrayList<Player>();
		NameGenerator gen = null;
		try {
			if(GameOptions.NAME_RESOURCE.contains("/bonus/") || GameOptions.NAME_RESOURCE.contains("\\bonus\\"))
				gen = new OtherNameGenerator(GameOptions.NAME_RESOURCE);
			else gen = new NameGenerator(GameOptions.NAME_RESOURCE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int i = 1; i < num; i++) {
			Player foe = new Player(gen.compose((int)(Math.random()*3)+2), 0);
			foe.setTeam(ZOMBIES_TEAM_NUM);
			foe.setType(PlayerType.COMPUTER);
//			int q = (int)(Math.random()*4);
			foe.setLocation(GameMap.fromGridPoint(spawnLocs.get((int)(spawnLocs.size()*Math.random()))));
			foe.addWeapon(new Weapon("Default"), this);
			foe.getCurrWeapon().setPower((int)Math.ceil(foe.getCurrWeapon().getPower()*.4));
			foe.setBrain(new ZombieBrain());
			foe.setFriendlyFire(friendlyFire());
			zombiesToAdd.add(foe);
			
		}
		return zombiesToAdd;
	}
	public List<Player> getDeadZombies() {
		return deadZombies;
	}
	public void setWaveStartTime(long start) {
		waveStartTime = start;
	}
	public long getWaveStartTime() {
		return waveStartTime;
	}
	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public int getWave() {
		return wave;
	}
	public void setWave(int num) {
		wave = num;
	}
	public void addDeadZombie(Player p) {
		if(deadZombies==null) deadZombies = new ArrayList<Player>();
		deadZombies.add(p);
	}

	@Override
	public String getModeName() { return "Tower Defense"; }

	@Override
	public String getScoreForPlayer(org.cwi.shoot.model.Player player) {
		return player.getTeam() == ZOMBIES_TEAM_NUM ? "" : player.getName() + ": " + player.getStats().getNumKills();
	}

	@Override
	public String getScoreForTeam(int team,
			List<org.cwi.shoot.model.Player> players) {
		return "Wave " + (getWave()==0 ? 1 : getWave());
	}

	@Override
	public void update(List<org.cwi.shoot.model.Player> players) {
		if((System.currentTimeMillis() - getWaveStartTime())/1000. >= wave_time) {
			List<Player> addZombies = addZombies(num_zombies_to_add);
			if(deadZombies.size()>0) {
				for(int i = 0; i < deadZombies.size(); i++)
					if(deadZombies.get(i).getHealth()<=0) {
						deadZombies.get(i).setLocation(GameMap.fromGridPoint(spawnLocs.get((int)(spawnLocs.size()*Math.random()))));
						addZombies.add(deadZombies.get(i));
						deadZombies.remove(i--);
					}
			}
			players.addAll(addZombies);
			setWave(getWave()+1);
			setWaveStartTime(System.currentTimeMillis());
		}
		if(teamControl==-1) {
			for(int i = 0; i < players.size(); i++) {
				Player p = players.get(i);
				if(zone.containsKey(GameMap.getGridPoint(p.getLocation())) && p.getTeam()==ZOMBIES_TEAM_NUM) {
					teamControl = p.getTeam();
					startTeamTime = startTeamTime==-1 ? System.currentTimeMillis() : startTeamTime;
					teamTime = (int) ((System.currentTimeMillis() - startTeamTime)/1000);
					inTheZone.add(p);
				}
				else if(inTheZone.contains(p) && !zone.containsKey(GameMap.getGridPoint(p.getLocation())))
					inTheZone.remove(p);
			}
		}
		else {
			boolean teamInZone = false;
			boolean contested = false;
			for(int i = 0; i < players.size(); i++) {
				Player p = players.get(i);
				if(p.getTeam()==teamControl && zone.containsKey(GameMap.getGridPoint(p.getLocation()))) {
					teamInZone = true;
					inTheZone.add(p);
				}
				else if(p.getTeam()!=teamControl && zone.containsKey(GameMap.getGridPoint(p.getLocation()))) contested = true;
			}
			if(teamInZone && !contested) teamTime = (int) ((System.currentTimeMillis() - startTeamTime)/1000);
			else if(teamInZone && contested) {
//				Integer i = teamPoints.get(teamControl);
//				i = (i == null ? 0 : i);
//				teamPoints.put(teamControl, (int) (teamControl != -1 ? i+teamTime : i));
				teamControl = -1;
				teamTime = -1;
				startTeamTime = -1;
			}
			else if(!teamInZone) {
				boolean inZone = false;
				for(int i = 0; i < players.size(); i++) {
					Player p = players.get(i);
					if(zone.containsKey(GameMap.getGridPoint(p.getLocation()))) {
//						Integer j = teamPoints.get(teamControl);
//						j = (j == null ? 0 : j);
//						teamPoints.put(teamControl, (int) (teamControl != -1 ? j+teamTime : j));
						teamControl = p.getTeam();
						startTeamTime = System.currentTimeMillis();
						teamTime = (int) ((System.currentTimeMillis() - startTeamTime)/1000);
					}
				}
				if(!inZone) {
//					Integer j = teamPoints.get(teamControl);
//					j = (j == null ? 0 : j);
//					teamPoints.put(teamControl, (int) (teamControl != -1 ? j+teamTime : j));
					teamControl = -1;
					teamTime = -1;
					startTeamTime = -1;
					inTheZone.clear();
				}
			}
		}
	}

	@Override
	public int getWinningTeam(List<org.cwi.shoot.model.Player> players) {
		if (teamTime >= TIME_TO_LOSE) return teamControl;
		if((teamTime - startTeamTime)/1000>=TIME_TO_LOSE) return ZOMBIES_TEAM_NUM;
//		for (Map.Entry<Integer,Integer> entry : teamPoints.entrySet()){
//			if (entry.getValue() + ((teamControl == entry.getKey()) ? teamTime : 0) >= ttw) return entry.getKey();
//		}
		return -1;
	}
	public void showGameEndDialog(GameMap map, int winner){
		Weapon w = new Weapon((char)(76), new Point());
		if(humanPlaying) JOptionPane.showMessageDialog(null, "They're in the zone. You lasted " + (System.currentTimeMillis() - getStartTime())/1000. + " seconds.", "Game over!", 0, new ImageIcon(Weapon.getWeaponImg(w.getImgLoc())));
		else JOptionPane.showMessageDialog(null, "They're in the zone. He lasted " + (System.currentTimeMillis() - getStartTime())/1000. + " seconds.", "Game over!", 0, new ImageIcon(Weapon.getWeaponImg(w.getImgLoc())));
	}
	@Override
	public boolean canGetWeapon(Player p, Weapon w) {
		if(p.getBrain() instanceof ZombieBrain)
			return false;
		return true;
	}

	@Override
	public List<Character> getAdditionalMapChars() {
		List<Character> specChars = new ArrayList<Character>();
		specChars.add('X');
		return specChars;
	}

	@Override
	public int getMaxNumTeams() {
		return 1;
	}

	@Override
	public List<Objective> getObjectives(org.cwi.shoot.map.GameMap map,
			org.cwi.shoot.model.Player p) {
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
				objectives.add(new LocationObjective(OBJECTIVE_WEIGHT,GameMap.getGridPoint(p.getLocation()).distance(point),GameMap.fromGridPoint(point)));
			}
			else if(!zone.containsKey(GameMap.getGridPoint(p.getLocation())) && !p.equals(inTheZone.get(0))){
				Player target = map.getClosestNonTeamPlayer(p.getTeam(), inTheZone.get(0).getLocation());
				if(target!=null)
					objectives.add(new KillObjective(OBJECTIVE_WEIGHT,GameMap.getGridPoint(p.getLocation()).distance(point),target));
			}
		}
		return objectives;
	}

	@Override
	public void onPlayerDeath(org.cwi.shoot.model.Player p) {
		if(p.getType() == PlayerType.COMPUTER && p.getTeam() == ZOMBIES_TEAM_NUM) {
			deadZombies.add(p);
		}
		else {
			
		}
	}

	@Override
	public void onPlayerRespawn(org.cwi.shoot.model.Player p) {
		
	}

	@Override
	public void drawModeMapPre(Graphics2D g, Point2D.Double playerLoc) {
		g.setColor(new Color(0f,1f,0f,0.5f));
		for(Point p : zone.keySet()) {
			if(playerLoc!=null) g.fillRect((int)p.getX()*GameMap.GRID_PIXELS - (int)playerLoc.x, (int)p.getY()*GameMap.GRID_PIXELS - (int)playerLoc.y, GameMap.GRID_PIXELS, GameMap.GRID_PIXELS);
			else g.fillRect((int)p.getX()*GameMap.GRID_PIXELS, (int)p.getY()*GameMap.GRID_PIXELS, GameMap.GRID_PIXELS, GameMap.GRID_PIXELS);
		}
	}

	@Override
	public void drawModeMapPost(Graphics2D g,
			List<org.cwi.shoot.model.Player> players, Point2D.Double playerLoc) {
		
	}
	
	public boolean handlesRespawn(){
		return true;
	}
	
	public boolean friendlyFire() {
		return true;
	}
	public Map<String, Object> getOptions() {
		Map<String, Object> options = new HashMap<String, Object>();
		Integer[] waves = { WAVE_TIME_LIMIT, 10, 60 };
		options.put("Time between waves:", waves);
		Integer[] zombiesPerWave = { ADD_NUM_ZOMBIES, 1, 20 };
		options.put("Number of Zombies per wave:", zombiesPerWave);
		return options;
	}
	public void defineSettings(String key, Object value) {
		if(key.equals("Time between waves:")) wave_time = Integer.parseInt((String)value);
		else if(key.equals("Number of Zombies per wave:")) num_zombies_to_add = Integer.parseInt((String)value);
	}
}
