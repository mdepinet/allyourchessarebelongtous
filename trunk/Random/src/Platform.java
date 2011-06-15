import java.awt.*;
import java.awt.geom.Point2D;


public class Platform {
	public static final int PLATFORM_WIDTH = 40;
	public static final int PLATFORM_HEIGHT = 10;
	private Point2D location;
	private boolean onScreen;
	private Color	color;

	public Platform(Point2D loc, int num)
	{
		location = loc;
		onScreen = true;
		chooseColor(num);
	}
	public void chooseColor(int num)
	{
		switch(num)
		{
		case 11:
		case 12:
		case 13:
		case 6:
		case 0:
			color = Color.green;
			break;
		case 7:
		case 14:
		case 15:
		case 16:
		case 1:
			color = Color.red;
			break;
		case 8:
		case 17:
		case 2:
			color = Color.orange;
			break;
		case 9:
		case 3:
			color = Color.magenta;
			break;
		case 4:
		case 10:
			color = Color.cyan;
			break;
		case 5:
		case 18:
		case 19:
			color = Color.white;
			break;
		default:
			color = Color.black;
			break;
		}
		
	}
	
	public void draw(Graphics g)
	{
		g.setColor(color);
		g.fillRect((int)location.getX() - PLATFORM_WIDTH/2, Global.GAME_HEIGHT-(int)location.getY(), PLATFORM_WIDTH, PLATFORM_HEIGHT);
		
	}
	public void update(double move)
	{
		setLocation(new Point2D.Double(getLocation().getX(),getLocation().getY()-move));
		if(location.getY()<-5)
			onScreen = false;
	}
	public boolean collidesWith(Player p)
	{
			Rectangle platRect = new Rectangle((int)location.getX()-PLATFORM_WIDTH/2, Global.GAME_HEIGHT-(int)location.getY(),PLATFORM_WIDTH,PLATFORM_HEIGHT);
			Rectangle playerRect = new Rectangle((int)p.getLocation().getX() - Player.PLAYER_WIDTH/2, Global.GAME_HEIGHT-((int)p.getLocation().getY() + Player.PLAYER_HEIGHT/2), Player.PLAYER_WIDTH, Player.PLAYER_HEIGHT);
			return (platRect.intersects(playerRect) && p.getLocation().getY()+10 > location.getY());
	}
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public boolean isOnScreen() {
		return onScreen;
	}

	public void setOnScreen(boolean onScreen) {
		this.onScreen = onScreen;
	}

	public Point2D getLocation() {
		return location;
	}

	public void setLocation(Point2D location) {
		this.location = location;
	}
}
