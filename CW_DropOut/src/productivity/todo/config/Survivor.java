package productivity.todo.config;

import java.util.ArrayList;
import java.util.List;

import productivity.todo.model.GameMap;
import productivity.todo.model.Player;
import productivity.todo.model.PlayerType;

public class Survivor extends GameMode {
	public static final int NUM_ENEMIES = 20;
	private long startTime;
	
	public Survivor() {}
	
	public Survivor(GameMap map) {
		super(map);
		startTime = System.currentTimeMillis();
	}

	@Override
	public void loadGameObjects() {
		
	}
	
	@Override
	public void update() {
		
		
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	@Override
	public int getWinningTeam() {
		if(gameMap.getPlayer()==null)
			return gameMap.getPlayers().get(1).getTeam();
		return -1;
	}

	@Override
	public String getModeName() { return "Survivor"; }

	@Override
	public String getScoreForPlayer(Player player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getScoreForTeam(int team) {
		// TODO Auto-generated method stub
		return null;
	}

}
