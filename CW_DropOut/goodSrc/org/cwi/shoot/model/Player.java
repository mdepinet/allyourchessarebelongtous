package org.cwi.shoot.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.cwi.shoot.ai.Controller;
import org.cwi.shoot.ai.DefaultBrain;
import org.cwi.shoot.config.GameMode;
import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.map.MapUpdatable;


public class Player implements Comparable<Player>, MapUpdatable {
	public static final double radius = 8;
	public static final char[] SPAWNLOC_CHARS ={'1','2','3','4'};
	public static int numTurrets = 100;
	public enum PlayerType {
		HUMAN, COMPUTER, REMOTE, TRANSITION, TURRET
	}
	private static double regenSpeed = 1/15.;
	
	private String name;
	private double health;
	private int team;
	
	private Point2D.Double location;
	private double orientation;
	private Point2D.Double direction; //Used for human movement
	
	private List<Weapon> weapons;
	private int currWeapon;
	
	private PlayerType type;
	private Controller brain;
	private PlayerStats stats;
	
	private boolean friendlyFire;
	private boolean canMove;
	private String turretPlayerName;
	private boolean deployedTurret;
	private int ID;
	
	public Player(String pname, int id){
		ID = id;
		health = 100;
		team = 0;
		location = new Point2D.Double(12,12);
		orientation = 0;
		direction = new Point2D.Double(0,0);
		weapons = Collections.synchronizedList(new ArrayList<Weapon>());
		currWeapon = 0;
		if(pname.contains("Turret")) {
			name = pname.substring(0,"Turret".length());
			turretPlayerName = pname.substring("Turret".length());
			type = PlayerType.TURRET;
			canMove = false;
		}
		else {
			name = pname;
			type = PlayerType.COMPUTER;
			canMove = true;
		}
		brain = new DefaultBrain();
		stats = new PlayerStats();
		friendlyFire = false;
		deployedTurret = false;
	}
	public int getNumTurrets() {
		return numTurrets;
	}
	public void setNumTurrets(int t) {
		numTurrets = t;
	}
	public int getID() {
		return ID;
	}
	public boolean hasDeployedTurret() {
		return deployedTurret;
	}
	public void setDeployedTurret(boolean t) {
		deployedTurret = t;
	}
	public String getTurretPlayerName() {
		return turretPlayerName;
	}
	public boolean isCanMove() {
		return canMove;
	}
	public void setCanMove(boolean canMove) {
		this.canMove = canMove;
	}
	public boolean isFriendlyFire() {
		return friendlyFire;
	}
	public void setFriendlyFire(boolean friendlyFire) {
		this.friendlyFire = friendlyFire;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getHealth() {
		return health;
	}
	public void setHealth(double health) {
		this.health = health;
	}
	public int getTeam() {
		return team;
	}
	public void setTeam(int team) {
		this.team = team;
	}
	public Point2D.Double getLocation() {
		return location;
	}
	public void setLocation(Point2D.Double point) {
		this.location = point;
	}
	public double getOrientation() {
		return orientation;
	}
	public void setOrientation(double orientation) {
		this.orientation = orientation;
	}
	public Point2D.Double getDirection() {
		return direction;
	}
	public void setDirection(Point2D.Double direction) {
		this.direction = direction;
	}
	public List<Weapon> getWeapons() {
		return weapons;
	}
	public void setWeapons(ArrayList<Weapon> weapon) {
		weapons = weapon;
	}
	public Weapon getCurrWeapon() {
		if(weapons.size()<=currWeapon) currWeapon=0;
		return weapons.size()>currWeapon ? weapons.get(currWeapon) : null;
	}
	public void setCurrWeapon(Weapon w) {
		if (weapons.contains(w)) currWeapon = weapons.indexOf(w);
	}
	public Weapon dropWeapon(Weapon w) {
		if(weapons.contains(w)) {
			nextWeapon();
			return weapons.remove(weapons.indexOf(w));
		}
		return null;
	}
	public PlayerType getType() {
		return type;
	}
	public void setType(PlayerType type) {
		this.type = type;
	}
	public PlayerStats getStats() {
		return stats;
	}
	public void setStats(PlayerStats stats) {
		this.stats = stats;
	}
	public Controller getBrain() {
		return brain;
	}
	public void setBrain(Controller brain) {
		this.brain = brain;
	}
	
	public boolean hasFlag() {
		for(Weapon w : weapons)
			if(Pattern.compile(Pattern.quote("flag"), Pattern.CASE_INSENSITIVE).matcher(w.getName()).find()) return true;
		return false;
	}
	public boolean hasTeamFlag(int team) {
		for(Weapon w : weapons)
			if(Pattern.compile(Pattern.quote(""+team+" flag"), Pattern.CASE_INSENSITIVE).matcher(w.getName()).find()) return true;
		return false;
	}
	
	public void update(){
		if(health<=0 || weapons.size()<=0) return;
		health += regenSpeed;
		if(health > 100) health = 100;
		for (Weapon w : weapons){
			w.update();
		}
		if(type == PlayerType.HUMAN){
			location = new Point2D.Double(location.getX()+(direction.getX()*2), location.getY()+(direction.getY()*2));
		}
	}
	public void update(GameMode mode, GameMap map) {
		update();
		if (type == PlayerType.COMPUTER || type == PlayerType.TURRET) brain.makeMove(mode, map, this);
	}
	
	public void removeWeapon(Weapon weapon) {
		weapons.remove(weapon);
		if (currWeapon == weapons.size()) currWeapon--;
	}
	public int getNumWeapons() {
		return weapons.size();
	}
	public boolean addWeapon(Weapon weapon, GameMode mode) {
		if (!mode.canGetWeapon(this, weapon)) return false;
		if(weapons.contains(weapon))
		{
			Weapon w = weapons.get(weapons.indexOf(weapon));
			if(w.getClipCount()==w.getMaxClipCount()) return false;
			w.setClipCount(w.getMaxClipCount());
		}
		else { 
			weapons.add(weapon);
		}
		return true;
	}
	public boolean canGetWeapon(Weapon weapon, GameMode mode) {
		if (!mode.canGetWeapon(this, weapon)) return false;	
		if(weapons.contains(weapon)) {	
			Weapon wep = weapons.get(weapons.indexOf(weapon));
			return wep.getClipCount() < wep.getMaxClipCount();
		}
		return true;
	}
	public void nextWeapon() {
		if(getCurrWeapon()!=null && getCurrWeapon().getTypes().contains(Weapon.WeaponType.OBJECTIVE)) return;
		if(weapons.size() > currWeapon+1) { currWeapon++; return; }
		currWeapon=0;
	}
	public void switchToWeapon(int num) {
		if(getCurrWeapon()!=null && getCurrWeapon().getTypes().contains(Weapon.WeaponType.OBJECTIVE)) return;
		if(weapons.size()>num) currWeapon = num;
	}
	
	public void clearStats() {
		stats = new PlayerStats();
	}
	
	public String toString(){
		return name+": " + getTeam() + " ("+health+")"+" @ ("+location.x+","+location.y+") with "+getCurrWeapon();
	}
	
	public Point2D.Double getGunLocation() {
		return new Point2D.Double(getLocation().x + Math.cos(getOrientation()+Math.PI/1.5)*12,getLocation().y + Math.sin(getOrientation()+Math.PI/1.5)*12);
	}
	
	public int compareTo(Player p) {
		return stats.compareTo(p.getStats());
	}
	
	public void takeDamage (double damage) {
		health -= damage;
		if(health<0) health = 0;
	}
	public void die(GameMode mode){
		mode.onPlayerDeath(this);
		stats.incNumDeaths();
		health = 0;
		weapons.clear();
		direction = new Point2D.Double(0,0);
		deployedTurret = false;
		numTurrets = 2;
	}
	public void reset() {
		numTurrets = 2;
		currWeapon = 0;
		stats = new PlayerStats();
		health = 100;
		direction = new Point2D.Double(0,0);
	}
	public void respawn(Point2D.Double loc, GameMode mode){
		currWeapon = 0;
		weapons.clear();
		addWeapon(new Weapon("Default"), mode);
		health = 100;
		orientation = 0;
		location = loc;
		mode.onPlayerRespawn(this);
	}
	public static double getRegenSpeed() {
		return regenSpeed;
	}
	public static void setRegenSpeed(double regen) {
		regenSpeed = regen;
	}
}
