package productivity.todo.model;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.Random;

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
