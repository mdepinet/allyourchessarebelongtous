package productivity.todo.model;

import java.awt.geom.Point2D;

public class Bullet {
	private String bulletImgLoc;
	private Point2D.Double location;
	private Point2D.Double velocity;
	private Weapon weapon;
	
	public Bullet(Weapon weapon) {
		this.weapon=weapon;
	}
	public void update() {
		setLocation(new Point2D.Double(getLocation().x+getVelocity().x, getLocation().y+getVelocity().y));
	}
	public void setLocation(Point2D.Double location) {
		this.location = location;
	}
	public Point2D.Double getLocation() {
		return location;
	}
	public void setBulletImgLoc(String bulletImgLoc) {
		this.bulletImgLoc = bulletImgLoc;
	}
	public String getBulletImgLoc() {
		return bulletImgLoc;
	}
	public Point2D.Double getVelocity() {
		return velocity;
	}
	public void setVelocity(Point2D.Double velocity) {
		this.velocity = velocity;
	}
	
}
