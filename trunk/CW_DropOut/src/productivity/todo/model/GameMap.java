package productivity.todo.model;


import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
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
	private ArrayList<Explosion> explosions;
	private Map<Integer, ArrayList<Point2D.Double>> spawnLocs;
	private int numTeams = 2;
	public GameMap()
	{
		spawnLocs = new HashMap<Integer, ArrayList<Point2D.Double>>();
		spawnLocs.put(1, new ArrayList<Point2D.Double>());
		spawnLocs.put(2, new ArrayList<Point2D.Double>());
		spawnLocs.put(3, new ArrayList<Point2D.Double>());
		spawnLocs.put(4, new ArrayList<Point2D.Double>());
		bullets = new ArrayList<Bullet>();
		explosions = new ArrayList<Explosion>();
		players = Collections.synchronizedList(new LinkedList<Player>());
		loadDefaultMap();
		Player player = new Player("player1");
		player.setTeam(1);
		player.setType(PlayerType.PERSON);
		players.add(player);
		/*player = new Player ("player2");
		player.setTeam(1);
		players.add(player);*/
		double team = 1.5;
		for(int i = 0; i < 3 + (spawnLocs.get(3).size()>0 ? 2 : 0) + (spawnLocs.get(4).size()>0 ? 2 : 0);i++)
		{
			Player p2 = new Player("player" + (i+2));
			p2.setTeam((int)team);
			team+=0.5;
			players.add(p2);
		}
		for(int i = 0; i < players.size();i++)
		{
			Player p = players.get(i);
			if(spawnLocs.get(new Integer(p.getTeam()))==null) System.out.println("null");
			if(!spawnLocs.get(new Integer(p.getTeam())).isEmpty()) {
				spawn(p);
				p.setLocation(spawnLocs.get(p.getTeam()).get((int)(Math.random()*spawnLocs.size())));
			}
			else players.remove(i);
		}
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
				if (next.matches("\\d+")) { if(next.equals("1") || next.equals("2") || next.equals("3") || next.equals("4")) { spawnLocs.get(new Integer(next)).add(new Point2D.Double((j*GRID_PIXELS)+12.5,(i*GRID_PIXELS)+12.5)); map[j][i] = '_'; } }
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
	public Point2D.Double getClosestWeapon(Point2D.Double loc)
	{
		double dist = Double.MAX_VALUE;
		Point2D.Double ret = null;
		for(int i =0;i<map.length;i++)
		{
			for(int j = 0;j < map[i].length; j++)
				if(map[i][j] != '_' && map[i][j]!='X')
				{
					if(loc.distance(new Point2D.Double(12.5+(i*25),12.5+(j*25)))<dist) { dist = loc.distance(new Point2D.Double(12.5+(i*25),12.5+(j*25))); ret = new Point2D.Double(12.5+(i*25),12.5+(j*25)); }
				}
		}
		return ret;
	}
	public Player getClosestTeamPlayer(int team,Point2D.Double loc)
	{
		double dist = Double.MAX_VALUE;
		Player ret = null;
		for(int i = 0; i < players.size();i++)
		{
			Player p = players.get(i);
			if(p.getTeam()!=team)
			{
				if(loc.distance(p.getLocation())<dist) { dist = loc.distance(p.getLocation()); ret = p; }
			}
		}
		return ret;
	}
	public Player getPlayer() {
		if(players.get(0).getType()==PlayerType.PERSON)
			return players.get(0);
		return null;
	}
	public void setPlayer(Player player) {
		this.players.set(0, player);
	}
	public ArrayList<Explosion> getExplosions() {
		return explosions;
	}
	public void setExplosions(ArrayList<Explosion> explosions) {
		this.explosions = explosions;
	}
	public void gameUpdate()
	{
		OUTTER: for(int i=0;i<bullets.size();i++) {
			Bullet b = bullets.get(i);
			if(!isValid(b.getLocation(),1)) 
				if(!b.getWeapon().getType().equalsIgnoreCase("thrown")){
					explode(b); 
					bullets.remove(i--); 
					continue OUTTER;
				}
			
			for(int j = 0; j < map.length; j++)
			{
				for(int k = 0; k < map[j].length;k++)
					if(map[j][k] == 'X'){
						Point2D.Double intersection = bulletColDetect(b,new Rectangle(j*GRID_PIXELS,k*GRID_PIXELS,GRID_PIXELS,GRID_PIXELS));
						if(intersection!=null && !b.getWeapon().getType().equalsIgnoreCase("thrown")){
							b.setLocation(intersection);
							explode(b); 
							bullets.remove(i--); 
							continue OUTTER;
						}
					}
			}
			Player hit = getHitPlayer(b);
			b.update();
			double effRange = b.getWeapon().getEffRange();
			//if it's outside effective range and it's an explosive, blow it and remove it
			if(b.getDistanceTraveled()>effRange) 
				if(explode(b)){
					bullets.remove(b);
					i--;
					continue OUTTER;
				}
			if(b.getDistanceTraveled() > effRange*2) { bullets.remove(b); i--; continue OUTTER; }
			if (hit != null){
				double damage = b.getWeapon().getPower();
				if (b.getDistanceTraveled() > effRange) damage -= ((b.getDistanceTraveled() - effRange)/effRange)*damage;
				hit.takeDamage(damage);
				if (hit.getHealth()<=0) kill(hit);
				explode(b);
				bullets.remove(b);
				i--;
			}
		}
		for(int i =0; i<players.size(); i++)
		{
			Player p= players.get(i);
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
				if(p.addWeapon(w)) {
					new WeaponAdderThread(map[getPlayerGridX(p)][getPlayerGridY(p)], new Point(getPlayerGridX(p), getPlayerGridY(p)), this).start();
					map[getPlayerGridX(p)][getPlayerGridY(p)] = '_';
				}
			}
		}
		for(int i = 0; i < explosions.size();i++)
		{	
			explosions.get(i).update();
			if(!explosions.get(i).isActive())
				explosions.remove(i);
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
	public void spawnWeapon(char c, Point location)
	{
		map[location.x][location.y] = c;
	}
	public Player getHitPlayer(Bullet bullet)
	{
		for(int i = 0; i < players.size();i++)
		{
			Player p = players.get(i);
			if(bulletColDetect(bullet,p))
				return p;
		}
		return null;
	}
	public Point2D.Double bulletColDetect(Bullet bullet, Rectangle r)
	{
		Line2D.Double bulletSegment = new Line2D.Double(
				bullet.getLocation(),
				new Point2D.Double(bullet.getVelocity().x+bullet.getLocation().x,bullet.getVelocity().y+bullet.getLocation().y));
		
		if(bulletSegment.intersects(r))
		{
			double angle = Math.toDegrees(Math.atan2(bulletSegment.y2-bulletSegment.y1, bulletSegment.x2-bulletSegment.x1));
			Line2D.Double top = new Line2D.Double(r.x,r.y,r.x+r.width,r.y);
			Line2D.Double bottom = new Line2D.Double(r.x,r.y+r.height,r.x+r.width,r.y+r.height);
			Line2D.Double left = new Line2D.Double(r.x,r.y,r.x,r.y+r.height);
			Line2D.Double right = new Line2D.Double(r.x+r.width,r.y,r.x+r.width,r.y+r.height);
			Point2D.Double intersection = null;
			if(angle <=90 && angle >=0) {
				if((intersection = getIntersectionPoint(bulletSegment, top))==null)
					intersection = getIntersectionPoint(bulletSegment, left);
			}
			else if(angle >=90 && angle <=180) {
				if((intersection = getIntersectionPoint(bulletSegment, top))==null)
					intersection = getIntersectionPoint(bulletSegment, right);
			}
			else if(angle >=180 && angle <=270) {
				if((intersection = getIntersectionPoint(bulletSegment, bottom))==null)
					intersection = getIntersectionPoint(bulletSegment, right);
			}
			else {
				if((intersection = getIntersectionPoint(bulletSegment, bottom))==null)
					intersection = getIntersectionPoint(bulletSegment, left);
			}
			return intersection;
		}
		return null;
	}
	public Point2D.Double getIntersectionPoint(Line2D.Double line1, Line2D.Double line2) {
		if (! line1.intersectsLine(line2) ) return null;
		double 	px = line1.getX1(), py = line1.getY1(), rx = line1.getX2()-px, ry = line1.getY2()-py;
		double 	qx = line2.getX1(), qy = line2.getY1(), sx = line2.getX2()-qx, sy = line2.getY2()-qy;
		double det = sx*ry - sy*rx;
		if (det != 0) {
			double z = (sx*(qy-py)+sy*(px-qx))/det;
			if (z==0 || z==1) return null;
			return new Point2D.Double((px+z*rx), (py+z*ry));
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
		  double t2 = (-b - discriminant)/(2*a);
		  if( (t1 >= 0 && t1 <= 1) ||  (t2 >= 0 && t2 <= 1)) return true;
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
		p.respawn(spawnLocs.get(p.getTeam()).get((int)(Math.random()*spawnLocs.get(p.getTeam()).size())));
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
	public Point getGridPoint(Point2D.Double p){
		return new Point((int) Math.floor(p.x/GRID_PIXELS), (int) Math.floor(p.y/GRID_PIXELS));
	}
	public Point2D.Double fromGridPoint(Point p){
		return new Point2D.Double(p.x*GRID_PIXELS+(GRID_PIXELS/2),p.y*GRID_PIXELS+(GRID_PIXELS/2));
	}
	
	private boolean explode(Bullet b){
		int splash = b.getWeapon().getSplash();
		if(splash<=1) return false;
		explosions.add(new Explosion(b.getLocation(), b.getWeapon().getSplash()*2));
		for (int i = 0; i<players.size(); i++){
			Player p = players.get(i);
			double distance = p.getLocation().distance(b.getLocation()) - p.getRadius();
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
