package productivity.todo.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import productivity.todo.ai.Controller;
import productivity.todo.ai.DefaultBrain;

public class Player {
	private Point2D.Double location;
	private Point2D.Double direction;
	private PlayerType type;
	private int radius;
	private String name;
	private PlayerStats stats;
	private ArrayList<Weapon> weapons;
	private double health;
	private double orientation;
	private int team;
	private int currWeapon;
	private Controller brain;
	public Player(){
		setTeam(0);
		name = "player1";
		health = 100;
		type = PlayerType.COMPUTER;
		stats = new PlayerStats();
		brain = new DefaultBrain();
		location=new Point2D.Double(12,12);
		direction = new Point2D.Double(0,0);
		radius = 8;
		weapons=new ArrayList<Weapon>();
		currWeapon=0;
		orientation=0;
	}
	public Player(String pname){
		setTeam(0);
		name = pname;
		health = 100;
		type = PlayerType.COMPUTER;
		stats = new PlayerStats();
		brain = new DefaultBrain();
		radius = 8;
		location=new Point2D.Double(12,12);
		direction = new Point2D.Double(0,0);
		weapons=new ArrayList<Weapon>();
		currWeapon=0;
		orientation=0;
	}
	public PlayerType getType() {
		return type;
	}
	public void setType(PlayerType type) {
		this.type = type;
	}
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
	public void update(GameMap map)
	{
		if(health<=0 || weapons.size()<=0)
			return;
		if(getCurrentWeapon()==null) return;
		getCurrentWeapon().update();
		if(type == PlayerType.PERSON)
			location = new Point2D.Double(location.getX()+(direction.getX()*2), location.getY()+(direction.getY()*2));
		else
		{
			brain.makeMove(map, this);
		}
	}
	
	public void removeWeapon(Weapon weapon) {
		weapons.remove(weapon);
	}
	public int getNumWeapons() {
		return weapons.size();
	}
	public boolean addWeapon(Weapon weapon) {
		if(weapons.contains(weapon))
		{
			Weapon w = weapons.get(weapons.indexOf(weapon));
			if(w.getClipCount()==w.getMaxClipCount()) return false;
			w.setClipCount(w.getMaxClipCount());
		}
		else { 
			weapons.add(weapon);
			if(weapon.getName().indexOf("Flag")!=-1) currWeapon = weapons.size()-1;
		}
		return true;
	}
	public boolean canGetWeapon(Weapon w)
	{
		if(weapons.contains(w)) {	
			Weapon wep = weapons.get(weapons.indexOf(w));
			return wep.getClipCount() < wep.getMaxClipCount();
		}
		return true;
	}
	public void setWeapon(ArrayList<Weapon> weapon) {
		weapons = weapon;
	}
	public void setWeapon(Weapon weapon, int index) {
		weapons.set(index, weapon);
	}
	public Weapon getCurrentWeapon() {
		if(!weapons.isEmpty() && currWeapon<weapons.size()) return weapons.get(currWeapon);
		else return new Weapon("dud");
	}
	public void setCurrWeapon(int nextWeapon) {
		currWeapon = nextWeapon;
	}
	public int nextWeapon() {
		if(getCurrentWeapon()==null) return -1;
		if(getCurrentWeapon().getName().indexOf("Flag")!=-1)
			return currWeapon;
		if(weapons.size()>currWeapon+1)
			return ++currWeapon;
		currWeapon=0;
		return currWeapon;
	}
	public void switchToWeapon(int num)
	{
		if(weapons.size()>num)
			currWeapon = num;
	}
	public void setLocation(Point2D.Double point) {
		this.location = point;
	}
	public Point2D.Double getLocation() {
		return location;
	}
	public void setHealth(double health) {
		this.health = health;
	}
	public double getHealth() {
		return health;
	}
	public PlayerStats getStats() {
		return stats;
	}
	public void setStats(PlayerStats stats) {
		this.stats = stats;
	}
	public void takeDamage (double damage) {
		health -= damage;
		if(health<0)
			health = 0;
	}
	public String toString(){
		return "Health: "+health + " Weapon: "+weapons+ " Point: "+location;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public Point2D.Double getDirection() {
		return direction;
	}
	public void setDirection(Point2D.Double direction) {
		this.direction = direction;
	}
	public void setOrientation(double orientation) {
		this.orientation = orientation;
	}
	public double getOrientation() {
		return orientation;
	}
	public Point2D.Double getGunLocation() {
		return new Point2D.Double(getLocation().x + Math.cos(getOrientation()+Math.PI/1.5)*12,getLocation().y + Math.sin(getOrientation()+Math.PI/1.5)*12);
	}
	public void setTeam(int team) {
		this.team = team;
	}
	public int getTeam() {
		return team;
	}
	
	public void die(){
		stats.incNumDeaths();
		health = 0;
		weapons.clear();
	}
	public void respawn(Point2D.Double loc){
		currWeapon = 0;
		health = 100;
		direction = new Point2D.Double();
		addWeapon(new Weapon("Default"));
		location = loc;
	}
}
