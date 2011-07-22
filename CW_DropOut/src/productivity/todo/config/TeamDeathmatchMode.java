package productivity.todo.config;

import productivity.todo.model.GameMap;

public class TeamDeathmatchMode extends GameMode {

	public TeamDeathmatchMode(GameMap map) {
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
		int[] teamKills = new int[4];
		for(int i = 0; i < gameMap.getPlayers().size();i++)
		{
			teamKills[gameMap.getPlayers().get(i).getTeam()-1] += gameMap.getPlayers().get(i).getStats().getNumKills() - gameMap.getPlayers().get(i).getStats().getNumSuicides();
			if(teamKills[gameMap.getPlayers().get(i).getTeam()-1]>=10)
				return gameMap.getPlayers().get(i).getTeam();
		}
		return -1;
	}

}
