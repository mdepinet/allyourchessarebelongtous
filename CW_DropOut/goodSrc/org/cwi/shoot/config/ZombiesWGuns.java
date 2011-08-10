package org.cwi.shoot.config;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.cwi.shoot.ai.ZombieBrain;
import org.cwi.shoot.ai.mike.SmartBrain;
import org.cwi.shoot.ai.objective.Objective;
import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Player.PlayerType;
import org.cwi.shoot.model.Weapon;
import org.cwi.shoot.threads.RespawnThread;
import org.cwi.shoot.util.NameGenerator;
import org.cwi.shoot.util.OtherNameGenerator;

public class ZombiesWGuns extends GameMode {
	public static final int NUM_ENEMIES = 10;
	public static final int ADD_NUM_ZOMBIES = 20;
	public static final int WAVE_TIME_LIMIT = 20;
	public static final int ZOMBIES_TEAM_NUM = 0;
	private int num_zombies_to_add = ADD_NUM_ZOMBIES;
	private int wave_time = WAVE_TIME_LIMIT;
	
	private long startTime;
	private int wave;
	private long waveStartTime;
	protected List<Player> deadZombies;
	protected List<Point> spawnLocs;
	protected boolean humanPlaying;
	protected Player originalPlayer;
	protected Map<String, Object> stats;
	private boolean dead;
	
	public ZombiesWGuns() {
		wave = 1;
		deadZombies = new ArrayList<Player>();
		spawnLocs = new ArrayList<Point>();
		stats = new HashMap<String, Object>();
	}
	
	public void onStartup(GameMap map, GameOptions setup){
		dead = false;
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
		dead = false;
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
			if(GameOptions.NAME_RESOURCE.equals("resource/namePartsNativeAmericanEnglishTranslation.txt"))
				gen = new OtherNameGenerator(GameOptions.NAME_RESOURCE);
			else gen = new NameGenerator(GameOptions.NAME_RESOURCE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int i = 1; i < num; i++) {
			Player foe = new Player(gen.compose((int)(Math.random()*3)+2), 0);
			foe.setTeam(ZOMBIES_TEAM_NUM);
			foe.setType(PlayerType.COMPUTER);
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
	public String getModeName() { return "Zombies... with Guns"; }

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
	}

	@Override
	public int getWinningTeam(List<org.cwi.shoot.model.Player> players) {
		return dead ? ZOMBIES_TEAM_NUM : -1;
	}
	public void showGameEndDialog(GameMap map, int winner){
		Weapon w = new Weapon((char)(76), new Point());
		if(humanPlaying) JOptionPane.showMessageDialog(null, "You're dead. You lasted " + (System.currentTimeMillis() - getStartTime())/1000. + " seconds.", "Game over!", 0, new ImageIcon(Weapon.getWeaponImg(w.getImgLoc())));
		else JOptionPane.showMessageDialog(null, "He's dead. He lasted " + (System.currentTimeMillis() - getStartTime())/1000. + " seconds.", "Game over!", 0, new ImageIcon(Weapon.getWeaponImg(w.getImgLoc())));
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onPlayerDeath(org.cwi.shoot.model.Player p) {
		if(p.getType() == PlayerType.COMPUTER && p.getTeam() == ZOMBIES_TEAM_NUM) {
			deadZombies.add(p);
		}
		else {
			dead = true;
			if(p.getType()==PlayerType.HUMAN) {
				stats = new HashMap<String, Object>();
				int score = 5 * ((int)(System.currentTimeMillis() - getStartTime())/1000 - 30) +(int)(1/5 *p.getStats().getKillsMinusSuicides() - p.getStats().getNumDeaths());
				stats.put("TotalScore:", score);
//				stats.put("Bullets-shot:", p.getStats().getShotsFired());
			}
			p.reset();
		}
	}

	@Override
	public void onPlayerRespawn(org.cwi.shoot.model.Player p) {
		
	}

	@Override
	public void drawModeMapPre(Graphics2D g, Point2D.Double playerLoc) {
		
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
