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
	private ArrayList<Weapon> weapons;
	private double health;
	private double orientation;
	private int team;
	private int currWeapon;
	public Player(){
		setTeam(0);
		name = "player1";
		health = 100;
		type = PlayerType.COMPUTER;
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
			Point2D.Double newLoc;
			Player p = map.getClosestTeamPlayer(map.getOppositeTeam(team), location);
			Point2D.Double destLoc;
			Point2D.Double wLoc;
			Point2D.Double pLoc;
			if(p==null)
				destLoc = map.getClosestWeapon(location);
			else
			{
				pLoc = p.getLocation();
				wLoc = map.getClosestWeapon(location);
				destLoc = (wLoc==null || location.distance(pLoc) < location.distance(wLoc)) ? pLoc : wLoc;
			}
			if(destLoc!=null)
			{
				newLoc = addPoints(location,getDirectionToLoc(location,destLoc));
				if(p!=null)
				{
					orientation = getAngleBetweenPoints(location,p.getLocation())+Math.PI/2;
					if(weapons.size()>1 && currWeapon==0)
						nextWeapon();
					if(p.getHealth()>0)
					{
						if(location.distance(p.getLocation())<=p.getCurrentWeapon().getEffRange())
						{
							if(Math.abs(Math.sin(orientation+Math.PI) - Math.sin(p.getOrientation()))<=Math.PI/5)
								newLoc = addPoints(location,multiplyPointByScalar(getDirectionToLoc(location,p.getLocation()),-1));
						}
						if(location.distance(p.getLocation())<=getCurrentWeapon().getEffRange())
						{
							if(getCurrentWeapon().canShoot())
								map.shoot(this);
						}
					}
				}
				location = newLoc;
			}
			else {
				//get weapons
			}
		}
	}
	public Point2D.Double getSmartDirectionToLoc(Point2D.Double loc, GameMap m)
	{
		
		return null;
	}
	public Point2D.Double multiplyPointByScalar(Point2D.Double point, double scalar)
	{
		return new Point2D.Double(point.x*scalar,point.y*scalar);
	}
	public Point2D.Double addPoints(Point2D.Double one, Point2D.Double two)
	{
		return new Point2D.Double(one.x+two.x,one.y+two.y);
	}
	public double getAngleBetweenPoints(Point2D.Double from, Point2D.Double to)
	{
		Point2D.Double ret = new Point2D.Double(from.x-to.x,from.y-to.y);
		return Math.atan2(ret.y,ret.x);
	}
	public Point2D.Double getDirectionToLoc(Point2D.Double from, Point2D.Double to)
	{
		double angle = getAngleBetweenPoints(from,to);
		return new Point2D.Double(-(Math.cos(angle)),-(Math.sin(angle)));
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
	public Weapon getWeapon() {
		return weapons.get(currWeapon);
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
