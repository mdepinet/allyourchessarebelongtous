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
		else weapons.add(weapon);
		return true;
	}
	public void setWeapon(ArrayList<Weapon> weapon) {
		weapons = weapon;
	}
	public void setWeapon(Weapon weapon, int index) {
		weapons.set(index, weapon);
	}
	public Weapon getCurrentWeapon() {
		return weapons.get(currWeapon);
	}
	public void setCurrWeapon(int nextWeapon) {
		currWeapon = nextWeapon;
	}
	public int nextWeapon() {
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
