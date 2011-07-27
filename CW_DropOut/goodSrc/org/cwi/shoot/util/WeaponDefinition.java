package org.cwi.shoot.util;

import org.cwi.shoot.model.Weapon;

public class WeaponDefinition {
	
	private String name;
	private Weapon.WeaponType type;
	private int power;
	private int effRange;
	private double spread;
	private int roundsPerShot;
	private double shotsPerSec;
	private int maxClipSize;
	private int splash;
	private String imgLoc;
	private int bulletSpeed;
	private String bulletImgLoc;
	private char representativeChar;
	private int maxClipCount;
	
	public WeaponDefinition(String line){
		String[] info = line.split(",");
		int i = 0;
		name = info[i++];
		type = Weapon.WeaponType.valueOf(info[i++].trim());
		power = Integer.parseInt(info[i++].trim());
		effRange = Integer.parseInt(info[i++].trim());
		spread = Double.parseDouble(info[i++].trim());
		roundsPerShot = Integer.parseInt(info[i++].trim());
		shotsPerSec = Double.parseDouble(info[i++].trim());
		maxClipSize = Integer.parseInt(info[i++].trim());
		maxClipCount = Integer.parseInt(info[i++].trim());
		splash = Integer.parseInt(info[i++].trim());
		bulletSpeed = Integer.parseInt(info[i++].trim());
		imgLoc = info[i++].trim();
		bulletImgLoc = info[i++].trim();
		representativeChar = info[i++].trim().charAt(0);
	}

	public String getName() {
		return name;
	}
	public Weapon.WeaponType getType() {
		return type;
	}
	public int getPower() {
		return power;
	}
	public int getEffRange() {
		return effRange;
	}
	public double getSpread() {
		return spread;
	}
	public int getRoundsPerShot() {
		return roundsPerShot;
	}
	public double getShotsPerSec() {
		return shotsPerSec;
	}
	public int getMaxClipSize() {
		return maxClipSize;
	}
	public int getMaxClipCount() {
		return maxClipCount;
	}
	public int getSplash() {
		return splash;
	}
	public String getImgLoc() {
		return imgLoc;
	}
	public int getBulletSpeed() {
		return bulletSpeed;
	}
	public String getBulletImgLoc() {
		return bulletImgLoc;
	}
	public char getRepresentativeChar() {
		return representativeChar;
	}
}
