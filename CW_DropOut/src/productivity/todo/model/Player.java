package productivity.todo.model;

import java.awt.Color;
import java.awt.geom.Point2D;

public class Player {
	private Point2D.Double point;
	private String name;
	private Color color;
	private Weapon weapon;
	private double health;
	public Player(){
		name = "default";
		health = 100;
		color = Color.black;
		point=new Point2D.Double(0,0);
		weapon=null;
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
	public void setPoint(Point2D.Double point) {
		this.point = point;
	}
	public Point2D.Double getPoint() {
		return point;
	}
	public void setHealth(double health) {
		this.health = health;
	}
	public double getHealth() {
		return health;
	}
	public String toString(){
		return "Health: "+health + " Color: "+color+ " Weapon: "+weapon+ " Point: "+point;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
}
