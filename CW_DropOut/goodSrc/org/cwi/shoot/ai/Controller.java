package org.cwi.shoot.ai;

import org.cwi.shoot.config.GameMode;
import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;


public interface Controller {
	void makeMove(GameMode mode, GameMap gm, Player player);
}
