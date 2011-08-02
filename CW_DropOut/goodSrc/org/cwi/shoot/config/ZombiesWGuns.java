package org.cwi.shoot.config;

import java.awt.Graphics2D;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.cwi.shoot.ai.ZombieBrain;
import org.cwi.shoot.ai.objective.Objective;
import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Player.PlayerType;
import org.cwi.shoot.model.Weapon;
import org.cwi.shoot.model.Weapon.WeaponType;
import org.cwi.shoot.threads.RespawnThread;
import org.cwi.shoot.util.NameGenerator;
import org.cwi.shoot.util.NativeAmericanNameGenerator;

public class ZombiesWGuns extends GameMode {
	public static final int NUM_ENEMIES = 10;
	public static final int ADD_NUM_ZOMBIES = 20;
	public static final int WAVE_TIME_LIMIT = 20;
	public static final int ZOMBIES_TEAM_NUM = 0;
	private long startTime;
	private int wave;
	private long waveStartTime;
	protected List<Player> deadZombies;
	protected List<Point> spawnLocs;
	
	public ZombiesWGuns() {
		startTime = System.currentTimeMillis();
		waveStartTime = System.currentTimeMillis();
		wave = 1;
		deadZombies = new ArrayList<Player>();
		spawnLocs = new ArrayList<Point>();
	}
	
	public void onStartup(GameMap map, GameOptions setup){
		for(int r = 0; r < modeMap.length; r++) 
			for(int c = 0; c < modeMap[r].length; c++)
				if(modeMap[r][c]!='X' && GameMap.getGridPoint(map.getPlayer().getLocation()).x!=c && GameMap.getGridPoint(map.getPlayer().getLocation()).y !=r)
					spawnLocs.add(new Point(r,c));
		map.spawn(map.getPlayer());
		map.getPlayer().removeWeapon(new Weapon("Default"));
		/*map.getPlayer().addWeapon(new Weapon("Minigun"), this);
		map.getPlayer().setCurrWeapon(new Weapon("Minigun"));
		map.getPlayer().getCurrWeapon().setClipCount(-1);
		map.getPlayer().getCurrWeapon().setClipSize(1000);*/
		map.getPlayer().addWeapon(new Weapon("Flamethrower"), this);
		map.getPlayer().setCurrWeapon(new Weapon("Flamethrower"));
		
		/*List<Player> z = addZombies(NUM_ENEMIES);
		map.getPlayers().addAll(z);*/
			
	}
	public void onReset(GameMap map, GameOptions setup){
		map.getPlayers().clear();
		for(int i = 0; i < map.getThreads().size(); i++) { 
			RespawnThread t = map.getThreads().get(i); 
			t.respawn(); 
			t.kill(); 
		}
		map.getThreads().clear();
		
		Player player = new Player(setup.getPlayerName());
		player.setTeam(setup.getPlayerTeam());
		player.setType(Player.PlayerType.HUMAN);
		map.getPlayers().add(0,player);
		map.spawn(player);

		map.getPlayer().removeWeapon(new Weapon("Default"));
		/*map.getPlayer().addWeapon(new Weapon("Minigun"), this);
		map.getPlayer().setCurrWeapon(new Weapon("Minigun"));
		map.getPlayer().getCurrWeapon().setClipCount(-1);
		map.getPlayer().getCurrWeapon().setClipSize(1000);*/
		map.getPlayer().addWeapon(new Weapon("Flamethrower"), this);
		map.getPlayer().setCurrWeapon(new Weapon("Flamethrower"));
		
		/*List<Player> z = addZombies(NUM_ENEMIES);
		map.getPlayers().addAll(z);*/
		
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
					/*gameMap.getPlayers().add(deadZombies.get(i));
					gameMap.spawn(deadZombies.get(i));*/
					deadZombies.remove(i--);
				}
		}
	}
	public List<Player> addZombies(int num) {
		ArrayList<Player> zombiesToAdd = new ArrayList<Player>();
		NameGenerator gen = null;
		try {
			if(GameOptions.NAME_RESOURCE.equals("resource/namePartsNativeAmericanEnglishTranslation.txt"))
				gen = new NativeAmericanNameGenerator(GameOptions.NAME_RESOURCE);
			else gen = new NameGenerator(GameOptions.NAME_RESOURCE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int i = 1; i < num; i++) {
			Player foe = new Player(gen.compose((int)(Math.random()*3)+2));
			foe.setTeam(ZOMBIES_TEAM_NUM);
			foe.setType(PlayerType.COMPUTER);
			foe.setLocation(GameMap.fromGridPoint(spawnLocs.get((int)(spawnLocs.size()*Math.random()))));
			foe.addWeapon(new Weapon("Default"), this);
			foe.getCurrWeapon().setPower((int)Math.ceil(foe.getCurrWeapon().getPower()*.4));
			foe.setBrain(new ZombieBrain());
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
		return player.getType()==PlayerType.COMPUTER ? "" : player.getName() + ": " + player.getStats().getNumKills();
	}

	@Override
	public String getScoreForTeam(int team,
			List<org.cwi.shoot.model.Player> players) {
		return "Wave " + (getWave()==0 ? 1 : getWave());
	}

	@Override
	public void update(List<org.cwi.shoot.model.Player> players) {
		if((System.currentTimeMillis() - getWaveStartTime())/1000. >= WAVE_TIME_LIMIT) {
			List<Player> addZombies = addZombies(ADD_NUM_ZOMBIES);
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
		for(int i = 0; i < players.size(); i++)
			if(players.get(i).getType()==PlayerType.HUMAN)
				return -1;
		return ZOMBIES_TEAM_NUM;
	}
	public void showGameEndDialog(GameMap map, int winner){
		Weapon w = new Weapon((char)(76), new Point());
		JOptionPane.showMessageDialog(null, "You're dead. You lasted " + (System.currentTimeMillis() - getStartTime())/1000. + " seconds.", "Game over!", 0, new ImageIcon(Weapon.getWeaponImg(w.getImgLoc())));
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
		if(p.getType() == PlayerType.COMPUTER) {
			deadZombies.add(p);
		}
	}

	@Override
	public void onPlayerRespawn(org.cwi.shoot.model.Player p) {
		
	}

	@Override
	public void drawModeMapPre(Graphics2D g) {
		
	}

	@Override
	public void drawModeMapPost(Graphics2D g,
			List<org.cwi.shoot.model.Player> players) {
		
	}
	
	public boolean handlesRespawn(){
		return true;
	}

}
