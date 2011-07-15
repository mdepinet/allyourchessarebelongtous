package productivity.todo.model;


import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import productivity.todo.view.GameCanvas;

public class GameMap{
	private List<Player> players;
	private char[][] map;
	public static final int HEIGHT = 750;
	public static final int WIDTH = 750;
	public static final int GRID_PIXELS = GameCanvas.GRID_PIXELS;
	private ArrayList<Bullet> bullets;
	private Map<Integer, ArrayList<Point2D.Double>> spawnLocs;
	private int numTeams = 2;
	public GameMap()
	{
		spawnLocs = new HashMap<Integer, ArrayList<Point2D.Double>>();
		spawnLocs.put(1, new ArrayList<Point2D.Double>());
		spawnLocs.put(2, new ArrayList<Point2D.Double>());
		spawnLocs.put(3, new ArrayList<Point2D.Double>());
		bullets = new ArrayList<Bullet>();
		players = Collections.synchronizedList(new LinkedList<Player>());
		loadDefaultMap();
		Player player = new Player("player1");
		player.setTeam(1);
		player.setType(PlayerType.PERSON);
		players.add(player);
		player = new Player ("player2");
		player.setTeam(1);
		players.add(player);
		for(int i = 0; i < 2;i++)
		{
			Player p2 = new Player("player" + (i+3));
			p2.setTeam(2);
			players.add(p2);
		}
		for(Player p: players)
		{
			spawn(p);
			if(!spawnLocs.get(new Integer(p.getTeam())).isEmpty()) p.setLocation(spawnLocs.get(p.getTeam()).get((int)(Math.random()*spawnLocs.size())));
		}
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
		JFileChooser chooser = new JFileChooser("resource");
		chooser.setFileFilter(new FileFilter(){
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith(".map");
			}
			@Override
			public String getDescription() {
				return "Map files";
			}
		});
		File mapChosen = null;
		int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) mapChosen = chooser.getSelectedFile();
        else mapChosen = new File("resource/default.map");
		map = new char[30][30];
		Scanner scan = null;
		try
		{
			scan = new Scanner(mapChosen);
		}
		catch(IOException e)
		{}
		for(int i = 0; i < map.length;i++)
		{
			for(int j = 0; j < map[i].length && scan.hasNext() ; j++) {
				String next = scan.next();
				map[j][i] = next.charAt(0);
				if (next.matches("\\d+")) { if(next.equals("1") || next.equals("2") || next.equals("3")) { spawnLocs.get(new Integer(next)).add(new Point2D.Double((j*GRID_PIXELS)+12.5,(i*GRID_PIXELS)+12.5)); map[j][i] = '_'; } }
			}
			if(scan.hasNextLine())
				scan.nextLine();
		}
	}
	public void shoot(Player p) {
		double tempAngle = p.getOrientation();
		for(int i = 1; i<=p.getCurrentWeapon().getRoundsPerShot(); i++){
			int spreadModifier = Math.random()>.5? -1:1;
			Bullet bullet = new Bullet(p.getCurrentWeapon());
			bullet.setLocation(p.getGunLocation());
			bullet.setTeam(p.getTeam());
			tempAngle += spreadModifier*Math.toRadians(Math.random()*p.getCurrentWeapon().getSpread()/2);
			bullet.setVelocity(new Point2D.Double(Math.cos(tempAngle+Math.PI/2)*p.getCurrentWeapon().getBulletSpeed(),Math.sin(tempAngle+Math.PI/2)*p.getCurrentWeapon().getBulletSpeed()));
			bullets.add(bullet);
			tempAngle=p.getOrientation();
		}
		p.getCurrentWeapon().setClipSize(p.getCurrentWeapon().getClipSize()-1);
		if(p.getCurrentWeapon().getClipSize()<=0 && p.getCurrentWeapon().getClipCount()==0) {
			p.removeWeapon(p.getCurrentWeapon());
			p.nextWeapon();
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
	public int getOppositeTeam(int num)
	{
		return (num+1<=numTeams) ? num+1 : num-1;
	}
	public Player getClosestTeamPlayer(int team,Point2D.Double loc)
	{
		double dist = Double.MAX_VALUE;
		Player ret = null;
		for(Player p:players)
		{
			if(p.getTeam()==team)
			{
				if(loc.distance(p.getLocation())<dist) { dist = loc.distance(p.getLocation()); ret = p; }
			}
		}
		return ret;
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
			for(int j = 0; j < map.length; j++)
			{
				for(int k = 0; k < map[j].length;k++)
					if(map[j][k] == 'X') bulletColDetect(b,new Rectangle(j*GRID_PIXELS,k*GRID_PIXELS,GRID_PIXELS,GRID_PIXELS));
			}
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
				if (!explode(b) && hit.getHealth()<=0) kill(hit);
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
				if(p.addWeapon(w)) map[getPlayerGridX(p)][getPlayerGridY(p)] = '_';
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
	public boolean bulletColDetect(Bullet bullet, Rectangle r)
	{
		double velX = bullet.getVelocity().x;
		double velY = bullet.getVelocity().y;
		Point2D.Double vec = new Point2D.Double(r.getLocation().x - bullet.getLocation().x,r.getLocation().y-bullet.getLocation().y);
		
		double a = velX*velX + velY*velY;
		double b = 2*(velX*vec.x + velY*vec.y);
		double c = (vec.x*vec.x+vec.y*vec.y) - (r.getWidth()/2)*(r.getWidth()/2);

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
		if(loc.x>(map[0].length*GRID_PIXELS)-radius || loc.x<radius || loc.y <radius || loc.y>(map.length*GRID_PIXELS)-radius) return false;
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
		if(splash<=1) return false;
		for (int i = 0; i<players.size(); i++){
			Player p = players.get(i);
			double distance = p.getLocation().distance(b.getLocation());
			if(distance<splash) {
				p.takeDamage(b.getWeapon().getPower()*((splash-distance)/splash));
				if (p.getHealth() <= 0){ kill(p); i--;}
			}
		}
		return true;
	}
	
	private void kill(Player p){
		p.die();
		int i;
		for (i = 0; i<players.size(); i++){
			if (players.get(i) == p){
				players.remove(i);
				break;
			}
		}
		new RespawnThread(this, p, i == 0, 5000).start();
	}
}
