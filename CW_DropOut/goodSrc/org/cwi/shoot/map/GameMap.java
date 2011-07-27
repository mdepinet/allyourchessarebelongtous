package org.cwi.shoot.map;


import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cwi.shoot.config.GameOptions;
import org.cwi.shoot.model.Bullet;
import org.cwi.shoot.model.Explosion;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Player.PlayerType;
import org.cwi.shoot.model.PlayerStats;
import org.cwi.shoot.model.Weapon;
import org.cwi.shoot.model.Weapon.WeaponType;
import org.cwi.shoot.threads.RespawnThread;
import org.cwi.shoot.threads.WeaponAdderThread;

public class GameMap{
	public static final String[] teamNames = { "America", "England", "Mexico", "Canada" };
	public static final int GRID_PIXELS = 25;
	
	private GameOptions setup;
	private char[][] map;
	private List<Player> players;
	private List<RespawnThread> threads;
	private List<Bullet> bullets;
	private List<Explosion> explosions;
	private Map<Integer, List<Point2D.Double>> spawnLocs;
	private List<Point2D.Double> droppedWeps;
	
	public GameMap(GameOptions setup) {
		this.setup = setup;
		init();
	}
	public void init() {
		map = setup.loadMap();
		
		bullets = new ArrayList<Bullet>();
		explosions = new ArrayList<Explosion>();
		threads = Collections.synchronizedList(new ArrayList<RespawnThread>());
		players = Collections.synchronizedList(new LinkedList<Player>());
		droppedWeps = Collections.synchronizedList(new ArrayList<Point2D.Double>());
		
		setupSpawnLocs();
		
		Player player = new Player(setup.getPlayerName());
		player.setTeam(setup.getPlayerTeam());
		player.setType(Player.PlayerType.HUMAN);
		players.add(player);

		setup.getMode().onStartup(this, setup);
		setup.getMode().loadGameObjects(this);
	}
	public void resetGame()
	{
		bullets.clear();
		explosions.clear();
		droppedWeps.clear();
		/*for(int i = 0; i < threads.size(); i++) { 
			RespawnThread t = threads.get(i); 
			t.respawn(); 
			t.kill(); 
		}
		threads.clear();*/
		
		Player p = new Player(setup.getPlayerName());
		p.setTeam(setup.getPlayerTeam());
		p.setType(Player.PlayerType.HUMAN);
		
		players.clear();
		players.add(p);
		setup.getMode().onReset(this, setup);
		setup.getMode().loadGameObjects(this);
	}
	private void setupSpawnLocs(){
		spawnLocs = new HashMap<Integer, List<Point2D.Double>>();
		for (int i = 0; i<=Math.min(setup.getNumTeams(),setup.getMode().getMaxNumTeams());){
			spawnLocs.put(i++, new ArrayList<Point2D.Double>());
		}
		for (int r = 0; r<map.length; r++){
			for (int c = 0; c<map[r].length; c++){
				List<Point2D.Double> locs = null;
				if (Character.isDigit(map[r][c])){
					locs = spawnLocs.get(Character.getNumericValue(map[r][c]));
					map[r][c] = GameOptions.BLANK_CHARACTER;
				}
				if (locs != null) locs.add(fromGridPoint(new Point(r,c)));
			}
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
	public List<RespawnThread> getThreads() {
		return threads;
	}
	public List<Bullet> getBullets() {
		return bullets;
	}
	public List<Explosion> getExplosions() {
		return explosions;
	}
	public Map<Integer, List<Point2D.Double>> getSpawnLocs() {
		return spawnLocs;
	}
	public List<Point2D.Double> getDroppedWeps() {
		return droppedWeps;
	}
	public Player getPlayer() {
		if(players.get(0).getType()==PlayerType.HUMAN) return players.get(0);
		return null;
	}
	public void setPlayer(Player player) {
		this.players.set(0, player);
	}
	
	public List<PlayerStats> getPlayerStats() {
		List<PlayerStats> ret = new ArrayList<PlayerStats>();
		for(Player p: players)
			ret.add(p.getStats());
		return ret;
	}
	public int getPixelWidth(){
		return map.length*GRID_PIXELS;
	}
	public int getPixelHeight(){
		return map[0].length*GRID_PIXELS;
	}
	
	public void shoot(Player p) {
		double tempAngle = p.getOrientation();
		if(p.getCurrWeapon().getEffRange()==0) {
			p.getCurrWeapon().setSwung(true);
			return;
		}
		p.getStats().incShotsFired();
		for(int i = 1; i<=p.getCurrWeapon().getRoundsPerShot(); i++){
			int spreadModifier = Math.random()>.5? -1:1;
			Bullet bullet = new Bullet(p.getCurrWeapon(), p);
			tempAngle += spreadModifier*Math.toRadians(Math.random()*p.getCurrWeapon().getSpread()/2);
			bullet.setVelocity(new Point2D.Double(Math.cos(tempAngle+Math.PI/2)*p.getCurrWeapon().getBulletSpeed(),Math.sin(tempAngle+Math.PI/2)*p.getCurrWeapon().getBulletSpeed()));
			bullets.add(bullet);
			tempAngle=p.getOrientation();
		}
		p.getCurrWeapon().setClipSize(p.getCurrWeapon().getClipSize()-1);
		if(p.getCurrWeapon().getClipSize()<=0 && p.getCurrWeapon().getClipCount()==0) {
			p.removeWeapon(p.getCurrWeapon());
			p.nextWeapon();
		}
	}
	public Player melee(Player p) {
		for(int i = 0; i < players.size(); i++) {
			if(players.get(i).getTeam()==p.getTeam()) continue;
			if(p.getLocation().distance(players.get(i).getLocation())<Player.radius*3) {
				return players.get(i);
			}
		}
		return null;
	}
	
	public Point2D.Double getClosestWeaponLoc(Player p) {
		double dist = Double.MAX_VALUE;
		Point2D.Double ret = null;
		for(int i =0;i<map.length;i++) {
			for(int j = 0;j < map[i].length; j++)
				if(map[i][j] != GameOptions.BLANK_CHARACTER && map[i][j]!=GameOptions.WALL_CHARACTER) {
					if(!p.canGetWeapon(new Weapon(map[i][j], new Point(i,j)), setup.getMode())) continue;
					if(p.getLocation().distance(new Point2D.Double(12.5+(i*25),12.5+(j*25)))<dist) { dist = p.getLocation().distance(new Point2D.Double(12.5+(i*25),12.5+(j*25))); ret = new Point2D.Double(12.5+(i*25),12.5+(j*25)); }
				}
		}
		return ret;
	}
	public Weapon getClosestWeapon(Player p){
		double dist = Double.MAX_VALUE;
		Weapon ret = null;
		for(int i =0;i<map.length;i++) {
			for(int j = 0;j < map[i].length; j++)
				if(map[i][j] != GameOptions.BLANK_CHARACTER && map[i][j]!=GameOptions.WALL_CHARACTER) {
					if(!p.canGetWeapon(new Weapon(map[i][j], new Point(i,j)), setup.getMode())) continue;
					if(p.getLocation().distance(new Point2D.Double(12.5+(i*25),12.5+(j*25)))<dist) { dist = p.getLocation().distance(new Point2D.Double(12.5+(i*25),12.5+(j*25))); ret = new Weapon(map[i][j], new Point(i,j)); }
				}
		}
		return ret;
	}
	public Player getClosestNonTeamPlayer(int team,Point2D.Double loc) {
		double dist = Double.MAX_VALUE;
		Player ret = null;
		for(int i = 0; i < players.size();i++) {
			Player p = players.get(i);
			if(p.getTeam()!=team) {
				if(loc.distance(p.getLocation())<dist) { dist = loc.distance(p.getLocation()); ret = p; }
			}
		}
		return ret;
	}
	public Player getClosestTeamPlayer(int team,Point2D.Double loc) {
		double dist = Double.MAX_VALUE;
		Player ret = null;
		for(int i = 0; i < players.size();i++) {
			Player p = players.get(i);
			if(p.getTeam()==team) {
				if(loc.distance(p.getLocation())<dist) { dist = loc.distance(p.getLocation()); ret = p; }
			}
		}
		return ret;
	}
	public Player[] getEnemies(Player p){
		List<Player> enemies = new LinkedList<Player>();
		for (Player o : players){
			if (o.getTeam()!=p.getTeam()) enemies.add(o);
		}
		return enemies.toArray(new Player[enemies.size()]);
	}
	public Player[] getAllies(Player p){
		List<Player> allies = new LinkedList<Player>();
		for (Player o : players){
			if (o.getTeam()==p.getTeam()) allies.add(o);
		}
		return allies.toArray(new Player[allies.size()]);
	}

	public void gameUpdate() {
		setup.getMode().update(players);
		int winner;
		if((winner = setup.getMode().getWinningTeam(players)) != -1) {
			setup.getMode().showGameEndDialog(this, winner);
			resetGame();
		}
		OUTTER: for(int i=0;i<bullets.size();i++) {
			
			Bullet b = bullets.get(i);
			if(!isValid(b.getLocation(),1)) 
				if(b.getWeapon().getType() != Weapon.WeaponType.THROWN){
					explode(b); 
					bullets.remove(i--); 
					continue OUTTER;
				}
			
			for(int j = 0; j < map.length; j++) {
				for(int k = 0; k < map[j].length;k++)
					if(map[j][k] == GameOptions.WALL_CHARACTER){
						Point2D.Double intersection = bulletColDetect(b,new Rectangle(j*GRID_PIXELS,k*GRID_PIXELS,GRID_PIXELS,GRID_PIXELS));
						if(intersection!=null && b.getWeapon().getType() != Weapon.WeaponType.THROWN){
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
		
		
		for(int i =0; i<players.size(); i++) {
			Player p= players.get(i);
			Point2D.Double loc = p.getLocation();
			p.update(setup.getMode(), this);
			if(p.getCurrWeapon()!=null && p.getCurrWeapon().isSwung()) {
				Player hit = melee(p);
				if(hit!=null) {
					hit.setHealth(0);
					p.getStats().incNumKills();
					kill(hit);
					players.remove(hit);
				}
				p.getCurrWeapon().setSwung(false);
			}
			if(!isValid(p.getLocation(), (int) Player.radius)) {
				if(isValid(new Point2D.Double(p.getLocation().x,loc.y), (int) Player.radius))
					p.setLocation(new Point2D.Double(p.getLocation().x,loc.y));
				else if(isValid(new Point2D.Double(loc.x,p.getLocation().y), (int) Player.radius))
					p.setLocation(new Point2D.Double(loc.x,p.getLocation().y));
				else
					p.setLocation(loc);
			}
			
			Weapon w;
			if((w = getWeapon(p))!=null){
				if(p.addWeapon(w,setup.getMode())) {
					if(w.getName().indexOf("Flag")==-1 && !droppedWeps.remove(new Point2D.Double(getPlayerGridX(p), getPlayerGridY(p))))
						new WeaponAdderThread(map[getPlayerGridX(p)][getPlayerGridY(p)], new Point(getPlayerGridX(p), getPlayerGridY(p)), this).start();
					map[getPlayerGridX(p)][getPlayerGridY(p)] = GameOptions.BLANK_CHARACTER;
				}
			}
		}
		
		for(int i = 0; i < explosions.size();i++) {	
			explosions.get(i).update();
			if(!explosions.get(i).isActive())
				explosions.remove(i);
		}
	}
	
	public Weapon getWeapon(Player p) {
		try {
			return getObjectAtPlayer(p);
		} 
		catch(IllegalArgumentException e) {return null;}
	}
	
	public void spawnWeapon(char c, Point location) {
		map[location.x][location.y] = c;
	}
	
	public Player getHitPlayer(Bullet bullet) {
		for(int i = 0; i < players.size();i++) {
			Player p = players.get(i);
			if(bulletColDetect(bullet,p))
				return p;
		}
		return null;
	}
	public Point2D.Double bulletColDetect(Bullet bullet, Rectangle r) {
		Line2D.Double bulletSegment = new Line2D.Double(
				bullet.getLocation(),
				new Point2D.Double(bullet.getVelocity().x+bullet.getLocation().x,bullet.getVelocity().y+bullet.getLocation().y));
		
		if(bulletSegment.intersects(r)) {
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
	public boolean bulletColDetect(Bullet bullet, Player p) {
		if(bullet.getTeam()==p.getTeam()) return false;
		double velX = bullet.getVelocity().x;
		double velY = bullet.getVelocity().y;
		return lineIntersectsCircle(new Line2D.Double(bullet.getLocation(), new Point2D.Double(bullet.getLocation().x+velX, bullet.getLocation().y+velY)), p.getLocation(), (int)Player.radius);
	}
	public boolean isValid(Point2D.Double loc, int radius) {
		if(loc.x>(map[0].length*GRID_PIXELS)-radius || loc.x<radius || loc.y <radius || loc.y>(map.length*GRID_PIXELS)-radius) return false;
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map[0].length;j++)
				if(map[i][j] == GameOptions.WALL_CHARACTER && new Rectangle(i*GRID_PIXELS,j*GRID_PIXELS,GRID_PIXELS,GRID_PIXELS).intersects(new Rectangle((int)loc.x-radius,(int)loc.y-radius,radius*2,radius*2)))
						return false;
		}
		return true;
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
		p.respawn(spawnLocs.get(p.getTeam()).get((int)(Math.random()*spawnLocs.get(p.getTeam()).size())), setup.getMode());
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
	public static Point getGridPoint(Point2D.Double p){
		if (p == null) return null;
		return new Point((int) Math.floor(p.x/GRID_PIXELS), (int) Math.floor(p.y/GRID_PIXELS));
	}
	public static Point2D.Double fromGridPoint(Point p){
		if (p == null) return null;
		return new Point2D.Double(p.x*GRID_PIXELS+(GRID_PIXELS/2),p.y*GRID_PIXELS+(GRID_PIXELS/2));
	}
	
	private boolean explode(Bullet b){
		int splash = b.getWeapon().getSplash();
		if(splash<=1) return false;
		explosions.add(new Explosion(b.getLocation(), b.getWeapon().getSplash()*2));
		for (int i = 0; i<players.size(); i++){
			Player p = players.get(i);
			if(p.getTeam()==b.getPlayer().getTeam() && p != b.getPlayer()) continue;
			double distance = p.getLocation().distance(b.getLocation()) - Player.radius;
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
	
	//Mode handles respawn thread
	private void kill(Player p){
		if(p.getCurrWeapon()!=null && p.getCurrWeapon().getType() != Weapon.WeaponType.PISTOL &&p.getCurrWeapon().getType()!= WeaponType.OBJECTIVE) {
			map[getPlayerGridX(p)][getPlayerGridY(p)]=p.getCurrWeapon().getCharacter();
			droppedWeps.add(new Point2D.Double(getPlayerGridX(p),getPlayerGridY(p)));
		} 
		p.die(setup.getMode());
		int i;
		for (i = 0; i<players.size(); i++){
			if (players.get(i) == p){
				players.remove(i);
				break;
			}
		}
		
		
	}
}
