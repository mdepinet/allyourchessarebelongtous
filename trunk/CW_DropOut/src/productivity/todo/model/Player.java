package productivity.todo.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Player {
	private Point2D.Double location;
	private Point2D.Double direction;
	private PlayerType type;
	private int radius;
	private String name;
	private Color color;
	private ArrayList<Weapon> weapon;
	private double health;
	private double orientation;
	private int team;
	private int currWeapon;
	public Player(){
		setTeam(0);
		name = "player1";
		health = 100;
		color = Color.black;
		type = PlayerType.COMPUTER;
		location=new Point2D.Double(12,12);
		direction = new Point2D.Double(0,0);
		radius = 8;
		weapon=new ArrayList<Weapon>();
		currWeapon=0;
		orientation=0;
	}
	public Player(String pname){
		setTeam(0);
		name = pname;
		health = 100;
		color = Color.black;
		type = PlayerType.COMPUTER;
		radius = 8;
		location=new Point2D.Double(12,12);
		direction = new Point2D.Double(0,0);
		weapon=new ArrayList<Weapon>();
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
		if(type == PlayerType.PERSON)
			location = new Point2D.Double(location.getX()+(direction.getX()*2), location.getY()+(direction.getY()*2));
		else
		{
			location = addPoints(location,getDirectionToLoc(location,map.getPlayer().getLocation()));
		}
	}
	public Point2D.Double addPoints(Point2D.Double one, Point2D.Double two)
	{
		return new Point2D.Double(one.x+two.x,one.y+two.y);
	}
	public Point2D.Double getDirectionToLoc(Point2D.Double from, Point2D.Double to)
	{
		Point2D.Double ret = new Point2D.Double(from.x-to.x,from.y-to.y);
		double angle = Math.atan2(ret.y,ret.x);
		ret.x = -(Math.cos(angle));
		ret.y = -(Math.sin(angle));
		return ret;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public Color getColor() {
		return color;
	}
	public void removeWeapon(Weapon weapon) {
		this.weapon.remove(weapon);
	}
	public int getNumWeapons() {
		return weapon.size();
	}
	public void addWeapon(Weapon weapon) {
		this.weapon.add(weapon);
	}
	public void setWeapon(ArrayList<Weapon> weapon) {
		this.weapon = weapon;
	}
	public void setWeapon(Weapon weapon, int index) {
		this.weapon.set(index, weapon);
	}
	public Weapon getWeapon(int index) {
		return weapon.get(index);
	}
	public Weapon getWeapon() {
		return weapon.get(currWeapon);
	}
	public boolean containsWeapon(Weapon weapon) {
		return this.weapon.contains(weapon);
	}
	public int weaponIndex(Weapon weapon) {
		return this.weapon.indexOf(weapon);
	}
	public Weapon getCurrentWeapon() {
		return weapon.get(currWeapon);
	}
	public void setCurrWeapon(int nextWeapon) {
		currWeapon = nextWeapon;
	}
	public int nextWeapon() {
		if(weapon.size()>currWeapon+1)
			return ++currWeapon;
		currWeapon=0;
		return currWeapon;
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
		if (health <= 0) die();
	}
	public String toString(){
		return "Health: "+health + " Color: "+color+ " Weapon: "+weapon+ " Point: "+location;
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
		weapon.clear();
		location = new Point2D.Double(-1000.,-1000.);
	}
	public void respawn(Point2D.Double loc){
		addWeapon(new Weapon("Default"));
		location = loc;
	}
}
