package productivity.todo.model;

public class Weapon {
	private String type;
	private int power;
	private int effRange;
	private double spread;
	private int roundsPerShot;
	private int shotsPerSec;
	private int clipSize;
	private int maxClipSize;
	private int splash;
	private String imgLoc;
	private String imgTopLoc;
	private int bulletSpeed;
	private String bulletImgLoc;
	public Weapon(String type){
		//load stuff from file
	}
	public String getType(){
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
	public int getShotsPerSec() {
		return shotsPerSec;
	}
	public int getClipSize() {
		return clipSize;
	}
	public int getMaxClipSize() {
		return maxClipSize;
	}
	public int getSplash() {
		return splash;
	}
	public String getImgLoc() {
		return imgLoc;
	}
	public String getImgTopLoc() {
		return imgTopLoc;
	}
	public int getBulletSpeed() {
		return bulletSpeed;
	}
	public String getBulletImgLoc() {
		return bulletImgLoc;
	}

}
