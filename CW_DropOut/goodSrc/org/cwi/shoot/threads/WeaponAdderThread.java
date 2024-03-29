package org.cwi.shoot.threads;

import java.awt.Point;

import org.cwi.shoot.map.GameMap;


public class WeaponAdderThread extends Thread {
	private GameMap gameMap;
	private Point location;
	private char weaponChar;
	
	public WeaponAdderThread(char weaponChar, Point location, GameMap gameMap){
		this.gameMap = gameMap;
		this.location = location;
		this.weaponChar = weaponChar;
	}
	
	public void run(){
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			gameMap.spawnWeapon(weaponChar, location);
	}
}
