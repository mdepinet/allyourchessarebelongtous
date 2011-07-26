package productivity.todo.model;


import java.awt.Point;
import java.awt.Rectangle;
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

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import productivity.todo.config.GameMode;
import productivity.todo.config.ZombiesWGuns;
import productivity.todo.config.TeamDeathmatchMode;
import productivity.todo.view.GameCanvas;

public class GameMap{
	private List<Player> players;
	private char[][] map;
	public static final String[] teamNames = { "America", "England", "Mexico", "Canada" };
	public static final int HEIGHT = 750;
	public static final int WIDTH = 750;
	public static final int GRID_PIXELS = GameCanvas.GRID_PIXELS;
	public static final int NUM_TEAMMATES = 2;
	private GameMode gameMode;
	private List<RespawnThread> threads;
	private ArrayList<Bullet> bullets;
	private ArrayList<Explosion> explosions;
	private File mapChosen;
	private Map<Integer, ArrayList<Point2D.Double>> spawnLocs;
	private int pTeam;
	public GameMap(File mapFile)
	{
		spawnLocs = new HashMap<Integer, ArrayList<Point2D.Double>>();
		spawnLocs.put(1, new ArrayList<Point2D.Double>());
		spawnLocs.put(2, new ArrayList<Point2D.Double>());
		spawnLocs.put(3, new ArrayList<Point2D.Double>());
		spawnLocs.put(4, new ArrayList<Point2D.Double>());
		spawnLocs.put(5, new ArrayList<Point2D.Double>());
		mapChosen = mapFile;
		bullets = new ArrayList<Bullet>();
		gameMode = new TeamDeathmatchMode(this);
		explosions = new ArrayList<Explosion>();
		threads = Collections.synchronizedList(new ArrayList<RespawnThread>());
		players = Collections.synchronizedList(new LinkedList<Player>());
	}
	public void init(int playerTeam)
	{
		loadMap();
		Player player = new Player("Player 1");
		player.setTeam(playerTeam);
		player.setType(PlayerType.PERSON);
		players.add(player);
		pTeam = playerTeam;
		NameGenerator gen = null;
		try {
			gen = new NameGenerator("resource/namePartsArab.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(gameMode instanceof ZombiesWGuns) {
			((ZombiesWGuns)gameMode).addZombies(ZombiesWGuns.NUM_ENEMIES);
		}
		else {
			for(int i = 1; i < spawnLocs.keySet().size() && !spawnLocs.get(i).isEmpty(); i++) {
				for(int j = 0; j < NUM_TEAMMATES; j++)
				{
					if(i == player.getTeam() && j == 0) continue;
					//Player p2 = new Player("Player " + (((i-1)*2)+j+(i>=player.getTeam()?1:2)));
					Player p2 = new Player(gen.compose((int)(Math.random()*3)+2));
					p2.setTeam(i);
					players.add(p2);
				}
			}
		}
		for(int i = 0; i < players.size();i++)
		{
			Player p = players.get(i);
			if(spawnLocs.get(new Integer(p.getTeam()))==null) System.out.println("null");
			if(!spawnLocs.get(new Integer(p.getTeam())).isEmpty()) {
				spawn(p);
				p.setLocation(spawnLocs.get(p.getTeam()).get((int)(Math.random()*spawnLocs.get(p.getTeam()).size())));
			}
			else players.remove(i);
		}
		if(gameMode instanceof ZombiesWGuns) ((ZombiesWGuns)gameMode).setStartTime(System.currentTimeMillis());
	}
	public void resetGame()
	{
		bullets.clear();
		for(int i = 0; i < threads.size(); i++) { 
			RespawnThread t = threads.get(i); 
			t.respawn(); 
			t.kill(); 
		}
		threads.clear();
		if(gameMode instanceof ZombiesWGuns) {
			((ZombiesWGuns)gameMode).setStartTime(System.currentTimeMillis());
		}
		gameMode.loadGameObjects();
		
		for(int i = 0; i < players.size();i++)
		{
			Player p = players.get(i);
			p.getWeapons().clear();
			p.clearStats();
			if(spawnLocs.get(new Integer(p.getTeam()))==null) System.out.println("null");
			if(!spawnLocs.get(new Integer(p.getTeam())).isEmpty()) {
				spawn(p);
				p.setLocation(spawnLocs.get(p.getTeam()).get((int)(Math.random()*spawnLocs.get(p.getTeam()).size())));
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
	public void loadMap()
	{
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
				if(!next.matches("[L-O]"))
					map[j][i] = next.charAt(0);
				else
					map[j][i] = '_';
				if (next.matches("\\d+")) { if(next.equals("1") || next.equals("2") || next.equals("3") || next.equals("4") || next.equals("5")) { spawnLocs.get(new Integer(next)).add(new Point2D.Double((j*GRID_PIXELS)+12.5,(i*GRID_PIXELS)+12.5)); map[j][i] = '_'; } }
			}
			if(scan.hasNextLine())
				scan.nextLine();
		}
		gameMode.loadGameObjects();
	}
	public Player melee(Player p) {
		for(int i = 0; i < players.size(); i++) {
			if(players.get(i).getTeam()==p.getTeam()) continue;
			if(p.getLocation().distance(players.get(i).getLocation())<p.getRadius()*3) {
				return players.get(i);
			}
		}
		return null;
	}
	public List<PlayerStats> getPlayerStats()
	{
		List<PlayerStats> ret = new ArrayList<PlayerStats>();
		for(Player p: players)
			ret.add(p.getStats());
		return ret;
	}
	public void shoot(Player p) {
		double tempAngle = p.getOrientation();
		if(p.getCurrentWeapon().getType().equals("Melee")) {
			p.getCurrentWeapon().setSwung(true);
			return;
		}
		p.getStats().incShotsFired();
		for(int i = 1; i<=p.getCurrentWeapon().getRoundsPerShot(); i++){
			int spreadModifier = Math.random()>.5? -1:1;
			Bullet bullet = new Bullet(p.getCurrentWeapon(), p);
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
	public GameMode getGameMode() {
		return gameMode;
	}
	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}
	public List<Player> getPlayers() {
		return players;
	}
	public List<RespawnThread> getThreads() {
		return threads;
	}
	public void setThreads(ArrayList<RespawnThread> threads) {
		this.threads = threads;
	}
	public Point2D.Double getClosestWeaponLoc(Player p)
	{
		double dist = Double.MAX_VALUE;
		Point2D.Double ret = null;
		for(int i =0;i<map.length;i++)
		{
			for(int j = 0;j < map[i].length; j++)
				if(map[i][j] != '_' && map[i][j]!='X')
				{
					if(!p.canGetWeapon(new Weapon(map[i][j], new Point(i,j)))) continue;
					if(p.getLocation().distance(new Point2D.Double(12.5+(i*25),12.5+(j*25)))<dist) { dist = p.getLocation().distance(new Point2D.Double(12.5+(i*25),12.5+(j*25))); ret = new Point2D.Double(12.5+(i*25),12.5+(j*25)); }
				}
		}
		return ret;
	}
	public Weapon getClosestWeapon(Player p){
		double dist = Double.MAX_VALUE;
		Weapon ret = null;
		for(int i =0;i<map.length;i++)
		{
			for(int j = 0;j < map[i].length; j++)
				if(map[i][j] != '_' && map[i][j]!='X')
				{
					if(!p.canGetWeapon(new Weapon(map[i][j], new Point(i,j)))) continue;
					if(p.getLocation().distance(new Point2D.Double(12.5+(i*25),12.5+(j*25)))<dist) { dist = p.getLocation().distance(new Point2D.Double(12.5+(i*25),12.5+(j*25))); ret = new Weapon(map[i][j], new Point(i,j)); }
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
	public File getMapChosen() {
		return mapChosen;
	}
	public void setMapChosen(File mapChosen) {
		this.mapChosen = mapChosen;
	}
	public void gameUpdate()
	{
		gameMode.update();
		int winner;
		if((winner = gameMode.getWinningTeam()) != -1) {
			if(winner==5) JOptionPane.showMessageDialog(null, "You're dead. You lasted " + ((System.currentTimeMillis() - ((ZombiesWGuns)gameMode).getStartTime())/1000. ) + " seconds", "Game over!", 0, new ImageIcon(new Weapon((char)(pTeam+75), new Point()).getImage()));
			else JOptionPane.showMessageDialog(null, teamNames[winner-1] + " Wins!", "Game over!", 0, new ImageIcon(new Weapon((char)(winner+75), new Point()).getImage()));
			resetGame();
		}
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
				if (hit.getHealth()<=0) {
					if(hit!=b.getPlayer()) b.getPlayer().getStats().incNumKills();
					else b.getPlayer().getStats().incNumSuicides();
					kill(hit);
				}
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
			if(p.getCurrentWeapon()!=null && p.getCurrentWeapon().isSwung()) {
				Player hit = melee(p);
				if(hit!=null) {
					hit.setHealth(0);
					p.getStats().incNumKills();
					kill(hit);
					players.remove(hit);
				}
				p.getCurrentWeapon().setSwung(false);
			}
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
					if(w.getName().indexOf("Flag")==-1)
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
			return getObjectAtPlayer(p);
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
	public boolean lineIntersectsCircle(Line2D.Double line, Point2D.Double circLoc, int radius)
	{
		double velX = line.x2-line.x1;
		double velY = line.y2-line.y1;
		Point2D.Double vec = new Point2D.Double(circLoc.x - line.x1,circLoc.y-line.y1);
		
		double a = velX*velX + velY*velY;
		double b = 2*(velX*vec.x + velY*vec.y);
		double c = (vec.x*vec.x+vec.y*vec.y) - radius*radius;

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
	public boolean bulletColDetect(Bullet bullet, Player p)
	{
		if(bullet.getTeam()==p.getTeam()) return false;
		double velX = bullet.getVelocity().x;
		double velY = bullet.getVelocity().y;
		return lineIntersectsCircle(new Line2D.Double(bullet.getLocation(), new Point2D.Double(bullet.getLocation().x+velX, bullet.getLocation().y+velY)), p.getLocation(), p.getRadius());
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
	public Weapon getObjectAtPlayer(Player p){
		int x = getPlayerGridX(p);
		int y = getPlayerGridY(p);
		if (x >= 0 && x<map.length && y >= 0){
			if (y<map[x].length) return new Weapon(map[x][y], new Point(x,y));
		}
		return null;
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
			if(p.getTeam()==b.getPlayer().getTeam() && p != b.getPlayer()) continue;
			double distance = p.getLocation().distance(b.getLocation()) - p.getRadius();
			if(distance<splash) {
				p.takeDamage(b.getWeapon().getPower()*((splash-distance)/splash));
				if (p.getHealth() <= 0){ 
					if(p!=b.getPlayer()) b.getPlayer().getStats().incNumKills();
					else b.getPlayer().getStats().incNumSuicides();
					kill(p);
					i--;
				}
			}
		}
		return true;
	}
	
	private void kill(Player p){
		if(p.hasFlag()) spawnWeapon(p.getFlag().getCharacter(), p.getFlag().getSpawnLoc()); 
		p.die();
		int i;
		for (i = 0; i<players.size(); i++){
			if (players.get(i) == p){
				players.remove(i);
				break;
			}
		}
		//if(!(gameMode instanceof ZombiesWGuns) || p.getType()==PlayerType.PERSON) {
			threads.add(new RespawnThread(this, p, i == 0 && p.getType()==PlayerType.PERSON, 5000));
			threads.get(threads.size()-1).start();
//		}
	}
}
