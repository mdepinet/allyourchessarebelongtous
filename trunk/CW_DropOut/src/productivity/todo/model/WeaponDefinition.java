package productivity.todo.model;

public class WeaponDefinition {
	private String name;
	private String type;
	private int power;
	private int effRange;
	private double spread;
	private int roundsPerShot;
	private double shotsPerSec;
	private int maxClipSize;
	private int splash;
	private String imgLoc;
	private String imgTopLoc;
	private int bulletSpeed;
	private String bulletImgLoc;
	private char representativeChar;
	
	public WeaponDefinition(String line){
		String[] info = line.split(",");
		int i = 0;
		name = info[i++];
		type = info[i++];
		power = Integer.parseInt(info[i++]);
		effRange = Integer.parseInt(info[i++]);
		spread = Double.parseDouble(info[i++]);
		roundsPerShot = Integer.parseInt(info[i++]);
		shotsPerSec = Double.parseDouble(info[i++]);
		maxClipSize = Integer.parseInt(info[i++]);
		splash = Integer.parseInt(info[i++]);
		bulletSpeed = Integer.parseInt(info[i++]);
		imgLoc = info[i++];
		imgTopLoc = info[i++];
		bulletImgLoc = info[i++];
		representativeChar = info[i++].charAt(0);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
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

	public String getImgLoc() {
		return imgLoc;
	}

	public void setImgLoc(String imgLoc) {
		this.imgLoc = imgLoc;
	}

	public String getImgTopLoc() {
		return imgTopLoc;
	}

	public void setImgTopLoc(String imgTopLoc) {
		this.imgTopLoc = imgTopLoc;
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

	public char getRepresentativeChar() {
		return representativeChar;
	}

	public void setRepresentativeChar(char representativeChar) {
		this.representativeChar = representativeChar;
	}
}
