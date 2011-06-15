import java.awt.*;
import java.awt.geom.Point2D;


public class Player {
	public static final double MAX_SPEED = 8.0;
	public static final int PLAYER_WIDTH = 20;
	public static final int PLAYER_HEIGHT = 20;
	private Point2D location;
	private Point2D velocity;
	boolean bounce;
	int rir;
	int boostCount;

	public Player()
	{
		location = new Point2D.Double(100, 0);
		velocity = new Point2D.Double(0,5);
		bounce = false;
		rir=0;
		boostCount = 3;
	}
	
	public void update()
	{
		if(velocity.getY()> -MAX_SPEED)
			velocity.setLocation(velocity.getX(),velocity.getY()-Global.GRAVITY);
		else
			velocity.setLocation(velocity.getX(),-MAX_SPEED);
		if(location.getY()<=170 || velocity.getY()<0)
			location.setLocation(location.getX(), location.getY()+velocity.getY());
		if(location.getY()<=10)
			bounce(null);
	}
	
	public void draw(Graphics g)
	{
		g.setColor(Color.blue);
		g.fillOval((int)location.getX() - PLAYER_WIDTH/2, Global.GAME_HEIGHT-((int)location.getY() + PLAYER_HEIGHT/2), PLAYER_WIDTH, PLAYER_HEIGHT);
		
	}
	
	public Point2D getVelocity() {
		return velocity;
	}
	public double getVelX(){return velocity.getX();}
	public double getVelY(){return velocity.getY();}

	public void setVelocity(Point2D velocity) {
		setVelX(velocity.getX());
		setVelY(velocity.getY());
	}
	public void setVelX(double x){
		velocity = new Point2D.Double(x,getVelY());
	}
	public void setVelY(double y){
		velocity = (new Point2D.Double(getVelX(),Math.min(Global.MAX_Y_VELOCITY,y)));
	}
	public int getBoostCount(){return boostCount;}
	public void setBoostCount(int boostCount){this.boostCount = boostCount;}

	public void bounce(Platform p)
	{
			if(p == null)
			{
				velocity.setLocation(0,Math.min(Global.MAX_Y_VELOCITY,9+(5*rir)));
				return;
			}
			if(p.getColor() == Color.green) {
				velocity.setLocation(0,Math.min(Global.MAX_Y_VELOCITY,9+(10*rir)));
				rir = 0;
			}
			else if(p.getColor() == Color.orange) {
				velocity.setLocation(0,Math.min(Global.MAX_Y_VELOCITY,15+(15*rir)));
				rir = 0;
			}
			else if(p.getColor() == Color.red) {
				setVelY(Math.min(Global.MAX_Y_VELOCITY,getVelY()-10-(5*rir)));
				rir++;
			}
			else if(p.getColor() == Color.magenta) {
				velocity.setLocation(0,Math.min(Global.MAX_Y_VELOCITY,25+(25*rir)));
				rir = 0;
			}
			else if(p.getColor() == Color.cyan) {
				velocity.setLocation(0,Math.min(Global.MAX_Y_VELOCITY,(int)(Math.random()*100)+(100*rir)));
				rir = 0;
			}
			else if(p.getColor() == Color.white) {
				velocity.setLocation(0,Math.min(Global.MAX_Y_VELOCITY,9+(5*rir)));
				int c = ((int)(Math.random()*3));
				setLocation(new Point2D.Double((c == 0 ? getLocation().getX() + 50 : (c==1 ? getLocation().getX()-50 : getLocation().getX())),getLocation().getY()));
				if (getLocation().getX() > Global.GAME_WIDTH) setLocation(new Point2D.Double((getLocation().getX()-Global.GAME_WIDTH),getLocation().getY()));
				if (getLocation().getX() < 0) setLocation(new Point2D.Double((getLocation().getX()+Global.GAME_WIDTH),getLocation().getY()));
				rir = 0;
			}
	}
	
	public void moveLeft()
	{
		setLocation(new Point2D.Double(getLocation().getX() - 5,getLocation().getY()));
		if(location.getX() < 0)
			setLocation(new Point2D.Double(Global.GAME_WIDTH + getLocation().getX(),getLocation().getY()));
	}
	public void moveRight()
	{
		setLocation(new Point2D.Double(getLocation().getX() + 5,getLocation().getY()));
		if(location.getX() > Global.GAME_WIDTH)
			setLocation(new Point2D.Double(getLocation().getX() - Global.GAME_WIDTH,getLocation().getY()));
	}
	public void boost(){
		if(boostCount>0){
			setLocation(new Point2D.Double(getLocation().getX(),getLocation().getY() - 5));
			setVelY(getVelY()+15);
			boostCount--;
		}
	}
	public void freeBoost(){
		setLocation(new Point2D.Double(getLocation().getX(),getLocation().getY() - 5));
		setVelY(getVelY()+15);
	}
	
	public Point2D getLocation() {
		return location;
	}

	public void setLocation(Point2D location) {
		this.location = location;
	}
}
