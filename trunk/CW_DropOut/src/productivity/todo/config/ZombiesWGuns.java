package productivity.todo.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import productivity.todo.model.GameMap;
import productivity.todo.model.NameGenerator;
import productivity.todo.model.Player;

public class ZombiesWGuns extends GameMode {
	public static final int NUM_ENEMIES = 10;
	public static final int WAVE_TIME_LIMIT = 10;
	public static final int ZOMBIES_TEAM_NUM = 5;
	private long startTime;
	private int wave;
	private long waveStartTime;
	private Player player;
	private int numZombies;
	private List<Player> zombies;
	public ZombiesWGuns() {}
	
	public ZombiesWGuns(GameMap map) {
		super(map);
		player = gameMap.getPlayer();
		startTime = System.currentTimeMillis();
		waveStartTime = System.currentTimeMillis();
		wave = 1;
		numZombies = 1;
		zombies = new ArrayList<Player>();
	}
	public void createZombieList() {
		zombies = new ArrayList<Player>();
	}

	@Override
	public void loadGameObjects() {
		
	}
	
	@Override
	public void update() {
		player = gameMap.getPlayer();
		if((System.currentTimeMillis() - getWaveStartTime())/1000. >= WAVE_TIME_LIMIT) {
			addZombies(5);
			respawnZombies();
			setWave(getWave()+1);
			setWaveStartTime(System.currentTimeMillis());
		}
	}
	public void respawnZombies() {
		//if(zombies==null) zombies = new ArrayList<Player>();
		if(zombies.size()>0) {
			for(int i = 0; i < zombies.size(); i++)
				if(zombies.get(i).getHealth()<=0) {
					gameMap.spawn(zombies.get(i));
					zombies.remove(i--);
				}
		}
	}
	public void addZombies(int num) {
		NameGenerator gen = null;
		try {
			gen = new NameGenerator(GameMap.NAMETYPES);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 1; i < num; i++) {
			//Player foe = new Player("Zombie "  + numZombies++);
			Player foe = new Player(gen.compose((int)(Math.random()*3)+2));
			foe.setTeam(5);
			gameMap.getPlayers().add(foe);
			gameMap.spawn(foe);
			//zombies.add(foe);
		}
	}
	public List<Player> getDeadZombies() {
		return zombies;
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
		if(zombies==null) zombies = new ArrayList<Player>();
		zombies.add(p);
	}
	@Override
	public int getWinningTeam() {
		if(gameMap.getPlayer()==null)
			if(gameMap.getPlayer()==null)
				return ZOMBIES_TEAM_NUM;
		return -1;
	}

	@Override
	public String getModeName() { return "Zombies... with Guns"; }

	@Override
	public String getScoreForPlayer(Player player) {
		return player.getName() + ": " + player.getStats().getNumKills();
	}

	@Override
	public String getScoreForTeam(int team) {
		/*int kills = 0;
		for(Player p : gameMap.getPlayers()) {
			if(p.getTeam()==team)
				kills += p.getStats().getNumKills();
		}
		return ""+kills;*/
		return "Wave " + (getWave()==0 ? 1 : getWave());
	}

}
