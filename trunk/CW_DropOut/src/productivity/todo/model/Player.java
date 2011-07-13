package productivity.todo.model;

import java.awt.Color;
import java.awt.geom.Point2D;

public class Player {
	private Point2D.Double location;
	private Point2D.Double direction;
	private int radius;
	private String name;
	private Color color;
	private Weapon[] weapon;
	private double health;
	private double orientation;
	private int team;
	private int currWeapon;
	private int numWeapons;
	public Player(){
		setTeam(0);
		name = "player1";
		health = 100;
		color = Color.black;
		location=new Point2D.Double(12,12);
		direction = new Point2D.Double(0,0);
		radius = 8;
		weapon=new Weapon[10];
		currWeapon=0;
		numWeapons=0;
		orientation=0;
	}
	public Player(String pname){
		setTeam(0);
		name = pname;
		health = 100;
		color = Color.black;
		radius = 8;
		location=new Point2D.Double(12,12);
		direction = new Point2D.Double(0,0);
		weapon=new Weapon[8];
		currWeapon=0;
		numWeapons=0;
		orientation=0;
	}
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
	public void update()
	{
		location = new Point2D.Double(location.getX()+(direction.getX()*2), location.getY()+(direction.getY()*2));
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public Color getColor() {
		return color;
	}
	public int getNumWeapons() {
		return numWeapons;
	}
	public void addWeapon(Weapon weapon) {
		this.weapon[numWeapons++] = weapon;
	}
	public void setWeapon(Weapon[] weapon) {
		this.weapon = weapon;
	}
	public void setWeapon(Weapon weapon, int index) {
		this.weapon[index] = weapon;
	}
	public Weapon getWeapon(int index) {
		return weapon[index];
	}
	public Weapon getWeapon() {
		return weapon[currWeapon];
	}
	public int getCurrWeapon() {
		return currWeapon;
	}
	public void setCurrWeapon(int nextWeapon) {
		currWeapon = nextWeapon;
	}
	public int nextWeapon() {
		if(weapon[currWeapon+1]!=null)
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
		for (int i = 0; i<weapon.length; i++){
			weapon[i] = null;
		}
		numWeapons = 0;
		location = new Point2D.Double(-1000.,-1000.);
	}
	public void respawn(Point2D.Double loc){
		addWeapon(new Weapon("Default"));
		location = loc;
	}
}
