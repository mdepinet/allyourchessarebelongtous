package productivity.todo.model;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.Random;

public class WeaponAdderThread extends Thread {
	private char[][] map;
	private Point2D[] mapLocs;
	private char[] weapons;
	private Random rand;
	private boolean done;
	
	public WeaponAdderThread(char[][] map, long seed){
		this.map = map;
		findMapLocs();
		rand = new Random();
		rand.setSeed(seed);
	}
	
	public void run(){
		while (!done){
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			int rand = this.rand.nextInt(mapLocs.length);
			map[(int) mapLocs[rand].getX()][(int) mapLocs[rand].getY()] = weapons[rand];
		}
	}
	
	private void findMapLocs(){
		LinkedList<Point2D> mapLocs = new LinkedList<Point2D>();
		LinkedList<Character> weapons = new LinkedList<Character>();
		for (int i = 0; i<map.length; i++){
			for (int j = 0; j<map[i].length; j++){
				if (Character.isLetter(map[i][j]) && map[i][j] != 'X'){
					mapLocs.add(new Point2D.Float(i,j));
					weapons.add(map[i][j]);
				}
			}
		}
		this.mapLocs = new Point2D[mapLocs.size()];
		int index = 0;
		while (!mapLocs.isEmpty() && index<this.mapLocs.length) this.mapLocs[index++] = mapLocs.remove(0);
		
		this.weapons = new char[weapons.size()];
		index = 0;
		while (!weapons.isEmpty() && index<this.weapons.length) this.weapons[index++] = weapons.remove(0).charValue();
	}
}
