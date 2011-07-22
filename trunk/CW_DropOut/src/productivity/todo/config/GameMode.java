package productivity.todo.config;

import productivity.todo.model.GameMap;

public abstract class GameMode {
	protected GameMap gameMap;
	public GameMode(GameMap map) {
		gameMap = map;
	}
	public static final Class<?>[] modes = { TeamDeathmatchMode.class };
	public abstract void loadGameObjects();
	public abstract void update();
	public abstract int getWinningTeam();
}
