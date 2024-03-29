package org.cwi.shoot.model;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.cwi.shoot.map.Updatable;

public class Bullet implements Updatable{
	private static Map<String, BufferedImage> images = new HashMap<String, BufferedImage>();
	
	private String bulletImgLoc;
	private Point2D.Double location;
	private Point2D.Double velocity;
	private Weapon weapon;
	private Player player;
	private double distanceTraveled;
	
	public Bullet(Weapon weapon, Player p) {
		this.weapon = weapon;
		bulletImgLoc = weapon.getBulletImgLoc();
		loadImage();
		this.player = p;
		setLocation(player.getGunLocation());
		distanceTraveled = 0;
	}
	public void update() {
		Point2D.Double newLoc = new Point2D.Double(location.x+velocity.x, location.y+velocity.y);
		distanceTraveled += location.distance(newLoc);
		setLocation(newLoc);
	}
	public int getTeam() {
		return player.getTeam();
	}
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
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
	public Weapon getWeapon() {
		return weapon;
	}
	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}
	public double getDistanceTraveled() {
		return distanceTraveled;
	}
	
	private void loadImage(){
		if (images.containsKey(bulletImgLoc)) return;
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(bulletImgLoc));
			images.put(bulletImgLoc,image);
		} catch (IOException e) {}
	}
	
	public static BufferedImage getBulletImg(String key){
		return images.get(key);
	}
	
}
