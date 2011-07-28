package org.cwi.shoot.config;

import java.awt.Graphics2D;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.cwi.shoot.ai.objective.Objective;
import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Player.PlayerType;
import org.cwi.shoot.model.Weapon;
import org.cwi.shoot.model.Weapon.WeaponType;
import org.cwi.shoot.util.NameGenerator;

public class ZombiesWGuns extends GameMode {
	public static final int NUM_ENEMIES = 10;
	public static final int ADD_NUM_ZOMBIES = 10;
	public static final int WAVE_TIME_LIMIT = 20;
	public static final int ZOMBIES_TEAM_NUM = 0;
	private long startTime;
	private int wave;
	private long waveStartTime;
	private List<Player> deadZombies;
	private boolean addPlayers;
	
	public ZombiesWGuns() {
		startTime = System.currentTimeMillis();
		waveStartTime = System.currentTimeMillis();
		wave = 1;
		deadZombies = new ArrayList<Player>();
		addPlayers = false;
	}
	
	public void onStartup(GameMap map, GameOptions setup){
		map.spawn(map.getPlayer());
		List<Player> z = addZombies(NUM_ENEMIES);
		for(int i = 0; i < z.size(); i++) {
			Player zombie = z.get(i);
			if(map.getSpawnLocs().get(ZOMBIES_TEAM_NUM)!=null && !map.getSpawnLocs().get(zombie.getTeam()).isEmpty()) {
				map.getPlayers().add(zombie);
				map.spawn(zombie);
			}
		}
			
	}
	public void onReset(GameMap map, GameOptions setup){
		map.getPlayers().clear();
		Player player = new Player(setup.getPlayerName());
		player.setTeam(setup.getPlayerTeam());
		player.setType(Player.PlayerType.HUMAN);
		map.getPlayers().add(player);
		map.spawn(player);
		List<Player> z = addZombies(NUM_ENEMIES);
		for(int i = 0; i < z.size(); i++) {
			Player zombie = z.get(i);
			if(map.getSpawnLocs().get(ZOMBIES_TEAM_NUM)!=null && !map.getSpawnLocs().get(zombie.getTeam()).isEmpty()) {
				map.getPlayers().add(zombie);
				map.spawn(zombie);
			}
		}
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
			gen = new NameGenerator(GameOptions.NAME_RESOURCE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int i = 1; i < num; i++) {
			Player foe = new Player(gen.compose((int)(Math.random()*3)+2));
			foe.setTeam(ZOMBIES_TEAM_NUM);
			foe.setType(PlayerType.COMPUTER);
			zombiesToAdd.add(foe);
			/*gameMap.getPlayers().add(foe);
			gameMap.spawn(foe);*/
		}
		return zombiesToAdd;
	}
	public List<Player> getPlayersToAdd() {
		if(addPlayers) {
			addPlayers = false;
			List<Player> addZombies = addZombies(ADD_NUM_ZOMBIES);
			if(deadZombies.size()>0) {
				for(int i = 0; i < deadZombies.size(); i++)
					if(deadZombies.get(i).getHealth()<=0) {
						addZombies.add(deadZombies.get(i));
						deadZombies.remove(i--);
					}
			}
			return addZombies;
		}
		return null;
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
		return player.getName() + ": " + player.getStats().getNumKills();
	}

	@Override
	public String getScoreForTeam(int team,
			List<org.cwi.shoot.model.Player> players) {
		return "Wave " + (getWave()==0 ? 1 : getWave());
	}

	@Override
	public void update(List<org.cwi.shoot.model.Player> players) {
		if((System.currentTimeMillis() - getWaveStartTime())/1000. >= WAVE_TIME_LIMIT) {
			//addZombies(ADD_NUM_ZOMBIES);
			//respawnZombies();
			setWave(getWave()+1);
			setWaveStartTime(System.currentTimeMillis());
			addPlayers = true;
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
	public boolean canGetWeapon(org.cwi.shoot.model.Player p, Weapon w) {
		return p.getType() == PlayerType.COMPUTER && w.getType() != WeaponType.PISTOL ? false : true;
	}

	@Override
	public List<Character> getAdditionalMapChars() {
		return new ArrayList<Character>();
	}

	@Override
	public int getMaxNumTeams() {
		return 1;
	}

	@Override
	public List<Objective> getObjectives(org.cwi.shoot.map.GameMap map,
			org.cwi.shoot.model.Player p) {
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawModeMapPre(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawModeMapPost(Graphics2D g,
			List<org.cwi.shoot.model.Player> players) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean handlesRespawn(){
		return true;
	}

}
