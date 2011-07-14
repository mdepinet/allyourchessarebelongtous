package productivity.todo.model;


import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import productivity.todo.view.GameCanvas;

public class GameMap{
	private List<Player> players;
	private char[][] map;
	public static final int HEIGHT = 500;
	public static final int WIDTH = 500;
	public static final int GRID_PIXELS = GameCanvas.GRID_PIXELS;
	private ArrayList<Bullet> bullets;
	private Map<Integer, ArrayList<Point2D.Double>> spawnLocs;
	public GameMap()
	{
		spawnLocs = new HashMap<Integer, ArrayList<Point2D.Double>>();
		spawnLocs.put(1, new ArrayList<Point2D.Double>());
		spawnLocs.put(2, new ArrayList<Point2D.Double>());
		spawnLocs.put(3, new ArrayList<Point2D.Double>());
		bullets = new ArrayList<Bullet>();
		players = new LinkedList<Player>();
		Player player = new Player("player1");
		player.addWeapon(new Weapon("BFG"));
		player.setTeam(1);
		player.setType(PlayerType.PERSON);
		players.add(player);
		loadDefaultMap();
		Player p2 = new Player("player2");
		p2.setTeam(2);
		spawn(p2);
		players.add(p2);
		
		spawn(player);
		for(Player p: players)
			if(!spawnLocs.get(new Integer(p.getTeam())).isEmpty()) p.setLocation(spawnLocs.get(p.getTeam()).get((int)(Math.random()*spawnLocs.size())));
		
		long seed = System.currentTimeMillis();
		System.out.println("Started game with seed "+seed);
		new WeaponAdderThread(map, seed).start();
	}
	public ArrayList<Bullet> getBullets() {
		return bullets;
	}
	public void setBullets(ArrayList<Bullet> bullets) {
		this.bullets = bullets;
	}
	public void loadDefaultMap()
	{
		map = new char[20][20];
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
				map[j][i] = next.charAt(0);
				if (next.matches("\\d+")) { if(next.equals("1") || next.equals("2") || next.equals("3")) { spawnLocs.get(new Integer(next)).add(new Point2D.Double((j*GRID_PIXELS)+12.5,(i*GRID_PIXELS)+12.5)); map[j][i] = '_'; } }
			}
			if(scan.hasNextLine())
				scan.nextLine();
		}
	}
	public void shoot(int x, int y, double angle, Point2D.Double shootLoc, Weapon weapon) {
		double tempAngle = angle;
		for(int i = 1; i<=weapon.getRoundsPerShot(); i++){
			int spreadModifier = Math.random()>.5? -1:1;
			Bullet bullet = new Bullet(weapon);
			bullet.setLocation(shootLoc);
			bullet.setTeam(getPlayer().getTeam());
			tempAngle += spreadModifier*Math.toRadians(Math.random()*weapon.getSpread()/2);
			bullet.setVelocity(new Point2D.Double(Math.cos(tempAngle+Math.PI/2)*weapon.getBulletSpeed(),Math.sin(tempAngle+Math.PI/2)*weapon.getBulletSpeed()));
			bullets.add(bullet);
			tempAngle=angle;
		}
		weapon.setClipSize(weapon.getClipSize()-1);
		if(weapon.getClipSize()<=0 && weapon.getClipCount()==0) {
			getPlayer().removeWeapon(weapon);
			getPlayer().nextWeapon();
		}
	}
	public char[][] getMap() {
		return map;
	}
	public void setMap(char[][] map) {
		this.map = map;
	}
	public List<Player> getPlayers() {
		return players;
	}

	public Player getPlayer() {
		return players.get(0);
	}
	public void setPlayer(Player player) {
		this.players.set(0, player);
	}
	public void gameUpdate()
	{
		for(int i=0;i<bullets.size();i++) {
			Bullet b = bullets.get(i);
			if(!isValid(b.getLocation(),1)) { explode(b); bullets.remove(i--); continue; }
			Player hit = getHitPlayer(b);
			b.update();
			double effRange = b.getWeapon().getEffRange();
			//if it's outside effective range and it's an explosive, blow it and remove it
			if(b.getDistanceTraveled()>effRange) 
				if(explode(b)){
					bullets.remove(b);
					i--;
					continue;
				}
			if(b.getDistanceTraveled() > effRange*2) { bullets.remove(b); i--; continue; }
			if (hit != null){
				double damage = b.getWeapon().getPower();
				if (b.getDistanceTraveled() > effRange) damage -= ((b.getDistanceTraveled() - effRange)/effRange)*damage;
				hit.takeDamage(damage);
				if (hit.getHealth()<=0){
					spawn(hit);
					new RespawnThread(hit,5000).start();
				}
				bullets.remove(b);
				i--;
			}
		}
		for(Player p: players)
		{
			Point2D.Double loc = p.getLocation();
			p.update(this);
			if(!isValid(p.getLocation(), p.getRadius()))
			{
				if(isValid(new Point2D.Double(p.getLocation().x,loc.y), p.getRadius()))
					p.setLocation(new Point2D.Double(p.getLocation().x,loc.y));
				else if(isValid(new Point2D.Double(loc.x,p.getLocation().y), p.getRadius()))
					p.setLocation(new Point2D.Double(loc.x,p.getLocation().y));
				else
					p.setLocation(loc);
			}
			
			Weapon w;
			if((w = getWeapon(p))!=null){
				if(p.containsWeapon(w)) p.getWeapon(p.weaponIndex(w)).setClipCount(p.getWeapon(p.weaponIndex(w)).getClipCount()+3);
				else p.addWeapon(w);
				map[getPlayerGridX(p)][getPlayerGridY(p)] = '_';
			}
		}
	}
	public Weapon getWeapon(Player p)
	{
		try {
			return new Weapon(getObjectAtPlayer(p));
		} 
		catch(IllegalArgumentException e) { }
		return null;
	}
	public Player getHitPlayer(Bullet bullet)
	{
		for(Player p:players)
		{
			if(bulletColDetect(bullet,p))
				return p;
		}
		return null;
	}
	public boolean bulletColDetect(Bullet bullet, Player p)
	{
		if(bullet.getTeam()==p.getTeam()) return false;
		double velX = bullet.getVelocity().x;
		double velY = bullet.getVelocity().y;
		Point2D.Double vec = new Point2D.Double(p.getLocation().x - bullet.getLocation().x,p.getLocation().y-bullet.getLocation().y);
		
		double a = velX*velX + velY*velY;
		double b = 2*(velX*vec.x + velY*vec.y);
		double c = (vec.x*vec.x+vec.y*vec.y) - p.getRadius()*p.getRadius();

		double discriminant = b*b-4*a*c;
		if( discriminant >= 0 )
		{
		  // ray didn't totally miss sphere, so there is a solution to the equation.
		  discriminant = Math.sqrt( discriminant );
		  double t1 = (-b + discriminant)/(2*a);
		  //double t2 = (-b - discriminant)/(2*a);
		  if( (t1 >= 0 && t1 <= 1) /*||  (t2 >= 0 && t2 <= 1)*/) return true;
		}
		return false;
	}
	public boolean isValid(Point2D.Double loc, int radius)
	{
		if(loc.x>500-radius || loc.x<radius || loc.y <radius || loc.y>500-radius) return false;
		for(int i = 0; i < map.length; i++)
		{
			for(int j = 0; j < map[0].length;j++)
				if(map[i][j] == 'X' && new Rectangle(i*GRID_PIXELS,j*GRID_PIXELS,GRID_PIXELS,GRID_PIXELS).intersects(new Rectangle((int)loc.x-radius,(int)loc.y-radius,radius*2,radius*2)))
						return false;
		}
		return true;
	}
	public void setPlayers(List<Player> players) {
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
		p.respawn(spawnLocs.get(p.getTeam()).get((int)(Math.random()*spawnLocs.size())));
	}
	public char getObjectAtPlayer(Player p){
		int x = getPlayerGridX(p);
		int y = getPlayerGridY(p);
		if (x >= 0 && x<map.length && y >= 0){
			if (y<map[x].length) return map[x][y];
		}
		return '_';
	}
	private int getPlayerGridX(Player p){
		return (int) (Math.floor(p.getLocation().x/GRID_PIXELS));
	}
	private int getPlayerGridY(Player p){
		return (int) (Math.floor(p.getLocation().y/GRID_PIXELS));
	}
	private boolean explode(Bullet b){
		int splash = b.getWeapon().getSplash();
		if(splash<1) return false;
		for(Player p : players){
			double distance = p.getLocation().distance(b.getLocation());
			if(distance<splash) {
				p.takeDamage(b.getWeapon().getPower()*((splash-distance)/splash));
				if(p.getHealth()<=0) p.die();
			}
		}
		return true;
		//TODO
	}
}
