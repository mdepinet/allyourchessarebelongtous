package productivity.todo.model;

import java.awt.Color;
import java.awt.geom.Point2D;

public class Player {
	private Point2D.Double location;
	private Point2D.Double direction;
	private String name;
	private Color color;
	private Weapon weapon;
	private double health;
	public Player(){
		name = "default";
		health = 100;
		color = Color.black;
		location=new Point2D.Double(20,20);
		direction = new Point2D.Double(0,0);
		weapon=null;
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
	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}
	public Weapon getWeapon() {
		return weapon;
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
}
