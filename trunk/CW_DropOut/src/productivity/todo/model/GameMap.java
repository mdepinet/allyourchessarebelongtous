package productivity.todo.model;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class GameMap{
	private Set<Player> players;
	private Player player;
	private boolean[][] map;
	public static final int HEIGHT = 500;
	public static final int WIDTH = 500;
	private ArrayList<Bullet> bullets;
	private Map<Integer, ArrayList<Point2D.Double>> spawnLocs;
	public GameMap()
	{
		spawnLocs = new HashMap<Integer, ArrayList<Point2D.Double>>();
		spawnLocs.put(1, new ArrayList<Point2D.Double>());
		spawnLocs.put(2, new ArrayList<Point2D.Double>());
		spawnLocs.put(3, new ArrayList<Point2D.Double>());
		bullets = new ArrayList<Bullet>();
		players = new HashSet<Player>();
		player = new Player("player1");
		player.setWeapon(new Weapon(".44 Magnum"));
		player.setTeam(1);
		loadDefaultMap();
		Player p2 = new Player("player2");
		p2.getLocation().x=300;
		p2.getLocation().y=300;
		p2.setWeapon(new Weapon("Semtex"));
		p2.setTeam(2);
		players.add(p2);
		
		player.setLocation(spawnLocs.get(player.getTeam()).get((int)(Math.random()*spawnLocs.size())));
		for(Player p: players)
			if(!spawnLocs.get(new Integer(p.getTeam())).isEmpty()) p.setLocation(spawnLocs.get(p.getTeam()).get((int)(Math.random()*spawnLocs.size())));
	}
	public ArrayList<Bullet> getBullets() {
		return bullets;
	}
	public void setBullets(ArrayList<Bullet> bullets) {
		this.bullets = bullets;
	}
	public void loadDefaultMap()
	{
		map = new boolean[20][20];
		Scanner scan = null;
		try
		{
			scan = new Scanner(new File("resource/default.map"));
		}
		catch(IOException e)
		{}
		for(int i = 0; i < 20;i++)
		{
			for(int j = 0; j < 20; j++) {
				String next = scan.next();
				map[j][i] = next.equals("X");
				if(next.equals("1") || next.equals("2") || next.equals("3")) spawnLocs.get(new Integer(next)).add(new Point2D.Double((j*25)+12.5,(i*25)+12.5));
			}
			if(scan.hasNextLine())
				scan.nextLine();
		}
	}
	public void shoot(int x, int y, double angle, Point2D.Double shootLoc, Weapon weapon) {
		Bullet bullet = new Bullet(weapon);
		bullet.setLocation(shootLoc);
		bullet.setVelocity(new Point2D.Double(Math.cos(angle+Math.PI/2)*weapon.getBulletSpeed(),Math.sin(angle+Math.PI/2)*weapon.getBulletSpeed()));
		
		bullets.add(bullet);
	}
	public boolean[][] getMap() {
		return map;
	}
	public void setMap(boolean[][] map) {
		this.map = map;
	}
	public Set<Player> getPlayers() {
		return players;
	}

	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public void gameUpdate()
	{
		for(int i=0;i<bullets.size();i++) {
			Bullet b = bullets.get(i);
			b.update();
			if(!isValid(b.getLocation(),1)) bullets.remove(i--);
		}
		for(Player p: players)
			p.update();
		Point2D.Double loc = player.getLocation();
		player.update();
		if(!isValid(player.getLocation(), player.getRadius()))
		{
			if(isValid(new Point2D.Double(player.getLocation().x,loc.y), player.getRadius()))
				player.setLocation(new Point2D.Double(player.getLocation().x,loc.y));
			else if(isValid(new Point2D.Double(loc.x,player.getLocation().y), player.getRadius()))
				player.setLocation(new Point2D.Double(loc.x,player.getLocation().y));
			else
				player.setLocation(loc);
		}
	}
	public boolean isValid(Point2D.Double loc, int radius)
	{
		if(loc.x>500-radius || loc.x<radius || loc.y <radius || loc.y>500-radius) return false;
		for(int i = 0; i < map.length; i++)
		{
			for(int j = 0; j < map[0].length;j++)
				if(map[i][j] && new Rectangle(i*25,j*25,25,25).intersects(new Rectangle((int)loc.x-radius,(int)loc.y-radius,radius*2,radius*2)))
						return false;
		}
		return true;
	}
	public void setPlayers(Set<Player> players) {
		this.players = players;
	}
	public Player getPlayerByName(String name) {
		Iterator<Player> it = players.iterator();
		while( it.hasNext() ) {
			Player next = it.next();
			if(next.getName().equals(name)) return next;
		}
		return null;
	}
}
