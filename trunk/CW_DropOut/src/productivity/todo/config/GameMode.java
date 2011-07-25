package productivity.todo.config;

import java.util.LinkedList;
import java.util.List;

import productivity.todo.model.GameMap;

public abstract class GameMode {
	protected GameMap gameMap;
	public void setGameMap(GameMap map) {
		gameMap = map;
	}
	public static final List<Class<? extends GameMode>> availableTypes = new LinkedList<Class<? extends GameMode>>();
	public GameMode() { }
	public GameMode(GameMap map) {
		gameMap = map;
	}
	public static final Class<?>[] modes = { TeamDeathmatchMode.class, CaptureTheFlagMode.class };
	public abstract String getModeName();
	public abstract void loadGameObjects();
	public abstract void update();
	public abstract int getWinningTeam();
}