package productivity.todo.ai;

import productivity.todo.model.GameMap;
import productivity.todo.model.Player;

public interface Controller {
	void makeMove(GameMap gm, Player p);
}
