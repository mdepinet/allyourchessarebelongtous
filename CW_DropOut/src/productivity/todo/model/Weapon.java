package productivity.todo.model;

public class Weapon {
	private static final int reloadMillis = 3000; //3 seconds
	private String type;
	private int power;
	private int effRange;
	private double spread;
	private int roundsPerShot;
	private double shotsPerSec;
	private int clipSize;
	private int maxClipSize;
	private int splash;
	private String imgLoc;
	private String imgTopLoc;
	private int bulletSpeed;
	private String bulletImgLoc;
	private int shotCounter;
	private long reloadStartTime;
	public Weapon(String name){
		WeaponLoader.load(this,name);
		shotCounter = 0;
		reloadStartTime = 0;
	}
	public Weapon(char c) {
		if(!WeaponLoader.load(this,c)) throw new IllegalArgumentException("This weapon doesn't exist");
		shotCounter = 0;
		reloadStartTime = 0;
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
	public double getShotsPerSec() {
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
	public boolean canShoot() {
		if(shotCounter>=30/shotsPerSec){
			shotCounter=0;
		}
		else return false;
		if (clipSize <= 0){
			reloadStartTime = System.currentTimeMillis();
			clipSize = maxClipSize;
		}
		if (reloadStartTime != 0){
			if (System.currentTimeMillis() - (reloadStartTime + reloadMillis) > 0){
				reloadStartTime = 0;
				return true;
			}
			else return false;
		}
		
		return true;
	}
	public void reload() {
		reloadStartTime = System.currentTimeMillis();
		clipSize = maxClipSize;
		if (System.currentTimeMillis() - (reloadStartTime + reloadMillis) > 0){
			reloadStartTime = 0;
		}
	}
	public void update() {
		shotCounter++;
	}
	public int getShotCounter() {
		return shotCounter;
	}
	public void setShotCounter(int shotCounter) {
		this.shotCounter = shotCounter;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setPower(int power) {
		this.power = power;
	}
	public void setEffRange(int effRange) {
		this.effRange = effRange;
	}
	public void setSpread(double spread) {
		this.spread = spread;
	}
	public void setRoundsPerShot(int roundsPerShot) {
		this.roundsPerShot = roundsPerShot;
	}
	public void setShotsPerSec(double shotsPerSec) {
		this.shotsPerSec = shotsPerSec;
	}
	public void setClipSize(int clipSize) {
		this.clipSize = clipSize;
	}
	public void setMaxClipSize(int maxClipSize) {
		this.maxClipSize = maxClipSize;
	}
	public void setSplash(int splash) {
		this.splash = splash;
	}
	public void setImgLoc(String imgLoc) {
		this.imgLoc = imgLoc;
	}
	public void setImgTopLoc(String imgTopLoc) {
		this.imgTopLoc = imgTopLoc;
	}
	public void setBulletSpeed(int bulletSpeed) {
		this.bulletSpeed = bulletSpeed;
	}
	public void setBulletImgLoc(String bulletImgLoc) {
		this.bulletImgLoc = bulletImgLoc;
	}
	
	void setProperties(WeaponDefinition wepDef){
		if (wepDef == null) return;
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
		setImgLoc(wepDef.getImgLoc());
		setImgTopLoc(wepDef.getImgTopLoc());
		setBulletImgLoc(wepDef.getBulletImgLoc());
	}
}
