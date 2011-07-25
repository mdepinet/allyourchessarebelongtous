package productivity.todo.config;

import java.util.ArrayList;
import java.util.List;

import productivity.todo.model.GameMap;
import productivity.todo.model.Player;
import productivity.todo.model.PlayerType;

public class Survivor extends GameMode {
	public static final int NUM_ENEMIES = 10;
	
	public Survivor() {}
	
	public Survivor(GameMap map) {
		super(map);
	}

	@Override
	public void loadGameObjects() {
		
	}
	
	@Override
	public void update() {
		
		
	}

	@Override
	public int getWinningTeam() {
//		int[] teamKills = new int[4];
//		for(int i = 0; i < gameMap.getPlayers().size();i++)
//		{
//			teamKills[gameMap.getPlayers().get(i).getTeam()-1] += gameMap.getPlayers().get(i).getStats().getNumKills() - gameMap.getPlayers().get(i).getStats().getNumSuicides();
		if(gameMap.getPlayer()==null)
			return gameMap.getPlayers().get(1).getTeam();
//		}
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
