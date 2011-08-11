package org.cwi.shoot.threads;

import java.awt.geom.Point2D;

import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Weapon;


public class WeaponRemoverThread extends Thread {
	private GameMap gameMap;
	private Point2D.Double location;
	private Weapon weapon;
	
	public WeaponRemoverThread(Weapon weapon, Point2D.Double location, GameMap gameMap){
		this.gameMap = gameMap;
		this.location = location;
		this.weapon = weapon;
	}
	
	public void run(){
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			gameMap.removeWeapon(location, weapon.getCharacter());
	}
}
