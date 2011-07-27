package org.cwi.shoot.map;


import org.cwi.shoot.config.GameMode;


public interface MapUpdatable extends Updatable {
	void update(GameMode mode, GameMap map);
}
