package productivity.todo.config;

import java.io.IOException;

import productivity.todo.model.GameMap;
import productivity.todo.model.NameGenerator;
import productivity.todo.model.Player;

public class ZombiesWGuns extends GameMode {
	public static final int NUM_ENEMIES = 20;
	private long startTime;
	private int wave;
	private long waveStartTime;
	private Player player;
	private int numZombies;
	public ZombiesWGuns() {}
	
	public ZombiesWGuns(GameMap map) {
		super(map);
		player = gameMap.getPlayer();
		startTime = System.currentTimeMillis();
		waveStartTime = System.currentTimeMillis();
		wave = 1;
		numZombies = 1;
	}

	@Override
	public void loadGameObjects() {
		
	}
	
	@Override
	public void update() {
		player = gameMap.getPlayer();
		if((System.currentTimeMillis() - getStartTime())/1000. >= 45) {
			addZombies(NUM_ENEMIES);
			respawnZombies();
			setWave(getWave()+1);
			setWaveStartTime(System.currentTimeMillis());
		}
	}
	public void respawnZombies() {
		if(gameMap.getPlayers().size()>0) {
			for(int i = 0; i < gameMap.getPlayers().size(); i++)
				if(gameMap.getPlayers().get(i).getHealth()<=0)
					gameMap.spawn(gameMap.getPlayers().get(i));
		}
	}
	public void addZombies(int num) {
		NameGenerator gen = null;
		try {
			gen = new NameGenerator("resource/namePartsArab.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 1; i < num; i++) {
			//Player foe = new Player("Zombie "  + numZombies++);
			Player foe = new Player(gen.compose((int)(Math.random()*3)+2));
			foe.setTeam(5);
			gameMap.getPlayers().add(foe);
		}
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
	@Override
	public int getWinningTeam() {
		if(gameMap.getPlayer()==null)
			if(gameMap.getPlayer()==null)
				return gameMap.getPlayers().get(1).getTeam();
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
		int kills = 0;
		for(Player p : gameMap.getPlayers()) {
			if(p.getTeam()==team)
				kills += p.getStats().getNumKills();
		}
		return ""+kills;
	}

}
