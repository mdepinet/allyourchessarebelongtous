package org.cwi.shoot.model;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.cwi.shoot.util.WeaponDefinition;
import org.cwi.shoot.util.WeaponLoader;

import org.cwi.shoot.map.Updatable;
import org.cwi.shoot.threads.ReloadThread;

public class Weapon implements Updatable{
	private static final int reloadMillis = 3000; //3 seconds
	private static final int updatesPerSec = 30;
	public enum WeaponType{
		OBJECTIVE, PISTOL, EXPOLSIVE, RIFLE, SNIPER, SHOTGUN, THROWN, SPECIAL, MELEE
	}
	
	private String name;
	private WeaponType type;
	private int power;
	private int effRange;
	private double spread;
	private int roundsPerShot;
	private double shotsPerSec;
	private int clipSize;
	private int maxClipSize;
	private int splash;
	private int bulletSpeed;
	private String bulletImgLoc;
	private char character;
	private BufferedImage image;
	
	private int shotCounter;
	private Point spawnLoc;
	private boolean swung;
	private int clipCount;
	private int maxClipCount;
	
	public Weapon(String name){
		WeaponLoader.load(this,name);
		shotCounter = 0;
	}
	public Weapon(char c, Point spawnLoc) {
		if(!WeaponLoader.load(this,c)) throw new IllegalArgumentException("This weapon doesn't exist");
		character = c;
		this.spawnLoc = spawnLoc;
		shotCounter = 0;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public WeaponType getType() {
		return type;
	}
	public void setType(WeaponType type) {
		this.type = type;
	}
	public int getPower() {
		return power;
	}
	public void setPower(int power) {
		this.power = power;
	}
	public int getEffRange() {
		return effRange;
	}
	public void setEffRange(int effRange) {
		this.effRange = effRange;
	}
	public double getSpread() {
		return spread;
	}
	public void setSpread(double spread) {
		this.spread = spread;
	}
	public int getRoundsPerShot() {
		return roundsPerShot;
	}
	public void setRoundsPerShot(int roundsPerShot) {
		this.roundsPerShot = roundsPerShot;
	}
	public double getShotsPerSec() {
		return shotsPerSec;
	}
	public void setShotsPerSec(double shotsPerSec) {
		this.shotsPerSec = shotsPerSec;
	}
	public int getClipSize() {
		return clipSize;
	}
	public void setClipSize(int clipSize) {
		this.clipSize = clipSize;
	}
	public int getMaxClipSize() {
		return maxClipSize;
	}
	public void setMaxClipSize(int maxClipSize) {
		this.maxClipSize = maxClipSize;
	}
	public int getSplash() {
		return splash;
	}
	public void setSplash(int splash) {
		this.splash = splash;
	}
	public int getBulletSpeed() {
		return bulletSpeed;
	}
	public void setBulletSpeed(int bulletSpeed) {
		this.bulletSpeed = bulletSpeed;
	}
	public String getBulletImgLoc() {
		return bulletImgLoc;
	}
	public void setBulletImgLoc(String bulletImgLoc) {
		this.bulletImgLoc = bulletImgLoc;
	}
	public char getCharacter() {
		return character;
	}
	public void setCharacter(char character) {
		this.character = character;
	}
	public BufferedImage getImage() {
		return image;
	}
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	public int getShotCounter() {
		return shotCounter;
	}
	public void setShotCounter(int shotCounter) {
		this.shotCounter = shotCounter;
	}
	public Point getSpawnLoc() {
		return spawnLoc;
	}
	public void setSpawnLoc(Point spawnLoc) {
		this.spawnLoc = spawnLoc;
	}
	public boolean isSwung() {
		return swung;
	}
	public void setSwung(boolean swung) {
		this.swung = swung;
	}
	public int getClipCount() {
		return clipCount;
	}
	public void setClipCount(int clipCount) {
		this.clipCount = clipCount;
	}
	public int getMaxClipCount() {
		return maxClipCount;
	}
	public void setMaxClipCount(int maxClipCount) {
		this.maxClipCount = maxClipCount;
	}

	public boolean equals(Object o) {
		return (o instanceof Weapon) && name.equals(((Weapon)o).getName());
	}
	
	public boolean canShoot() {
		if(shotCounter >= updatesPerSec/shotsPerSec){
			shotCounter=0;
		}
		else return false;
		if(type.equals("Melee")) return true;
		if(clipSize <= 0) return false;
		if (clipSize == 1){
			if(clipCount!=0) new ReloadThread(this,reloadMillis).start();
			clipSize--;
			return true;
		}
		return true;
	}
	public void reload(){
		if(clipCount!=0 && clipSize!=maxClipSize) new ReloadThread(this,reloadMillis).start();
	}
	public void update() {
		shotCounter++;
	}
	
	public void setProperties(WeaponDefinition wepDef){
		if (wepDef == null) return;
		setName(wepDef.getName());
		setMaxClipCount(wepDef.getMaxClipCount());
		setType(wepDef.getType());
		setPower(wepDef.getPower());
		setEffRange(wepDef.getEffRange());
		setSpread(wepDef.getSpread());
		setRoundsPerShot(wepDef.getRoundsPerShot());
		setShotsPerSec(wepDef.getShotsPerSec());
		setClipSize(wepDef.getMaxClipSize());
		setMaxClipSize(wepDef.getMaxClipSize());
		setSplash(wepDef.getSplash());
		setBulletSpeed(wepDef.getBulletSpeed());
		image = null;
		try {
			image = ImageIO.read(new File(wepDef.getImgLoc()));
		} catch (IOException e) {}
		setBulletImgLoc(wepDef.getBulletImgLoc());
		setClipCount(wepDef.getMaxClipCount());
	}
}
