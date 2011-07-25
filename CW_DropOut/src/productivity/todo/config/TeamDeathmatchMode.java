package productivity.todo.config;

import productivity.todo.model.GameMap;
import productivity.todo.model.Player;

public class TeamDeathmatchMode extends GameMode {

	public static final int KILLS_TO_WIN = 10;
	
	public TeamDeathmatchMode() {}
	
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
			if(teamKills[gameMap.getPlayers().get(i).getTeam()-1]>=KILLS_TO_WIN)
				return gameMap.getPlayers().get(i).getTeam();
		}
		return -1;
	}

	@Override
	public String getModeName() { return "Team Deathmatch"; }

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
