package productivity.todo.model;

import java.awt.Color;
import java.awt.geom.Point2D;

public class Player {
	private Point2D.Double point;
	private Color color;
	private Weapon weapon;
	public Player(){
		setColor(Color.BLACK);
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
}
