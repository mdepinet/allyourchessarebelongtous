package productivity.todo.model;

import java.awt.Rectangle;
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
		player.setWeapon(new Weapon("Default"),0);
		player.setWeapon(new Weapon("Assault Rifle"), 1);
		player.setWeapon(new Weapon("Barret .50Cal"),2);
		player.setTeam(1);
		loadDefaultMap();
		Player p2 = new Player("player2");
		spawn(p2);
		p2.setWeapon(new Weapon("Default"),0);
		p2.setTeam(2);
		players.add(p2);
		
		spawn(player);
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
		weapon.setClipSize(weapon.getClipSize()-1);
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
			if(!isValid(b.getLocation(),1)) bullets.remove(i--);
			Player hit = getHitPlayer(b);
			b.update();
			double effRange = b.getWeapon().getEffRange();
			if(b.getDistanceTraveled() > effRange*2) { bullets.remove(b); continue; }
			if (hit != null){
				double damage = b.getWeapon().getPower();
				if (b.getDistanceTraveled() > effRange) damage -= ((b.getDistanceTraveled() - effRange)/effRange)*damage;
				hit.takeDamage(damage);
				if (hit.getHealth()<=0){
					spawn(hit);
					new RespawnThread(hit,5000).start();
				}
				bullets.remove(b);
			}
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
	public Player getHitPlayer(Bullet bullet)
	{
		for(Player p:players)
		{
			if(bulletColDetect(bullet,p))
				return p;
		}
		if(bulletColDetect(bullet,player))
			return player;
		return null;
	}
	public boolean bulletColDetect(Bullet bullet, Player p)
	{
		double velX = bullet.getVelocity().x;
		double velY = bullet.getVelocity().y;
		Point2D.Double vec = new Point2D.Double(p.getLocation().x - bullet.getLocation().x,p.getLocation().y-bullet.getLocation().y);
		
		double a = velX*velX + velY*velY;
		double b = 2*(velX*vec.x + velY*vec.y);
		double c = (vec.x*vec.x+vec.y*vec.y) - p.getRadius()*p.getRadius();

		double discriminant = b*b-4*a*c;
		if( discriminant >= 0 )
		{
		  // ray didn't totally miss sphere,
		  // so there is a solution to
		  // the equation.


		  discriminant = Math.sqrt( discriminant );
		  double t1 = (-b + discriminant)/(2*a);
		  double t2 = (-b - discriminant)/(2*a);

		  if( t1 >= 0 && t1 <= 1 )
		  {
		    return true;
		  }
		}
		return false;
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
	
	public void spawn(Player p){
		p.respawn(spawnLocs.get(player.getTeam()).get((int)(Math.random()*spawnLocs.size())));
	}
}
