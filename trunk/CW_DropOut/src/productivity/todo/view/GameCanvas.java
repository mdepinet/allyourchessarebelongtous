package productivity.todo.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Set;

import productivity.todo.model.Bullet;
import productivity.todo.model.GameMap;
import productivity.todo.model.Player;
import productivity.todo.model.PlayerType;
import productivity.todo.model.Weapon;

public class GameCanvas extends Canvas {
	private GameMap gameMap;
	private Image backbuffer;
	private Graphics2D backg;
	public static final int GRID_PIXELS = 25;
	public static final int FRAMES_PER_SECOND = 30;

	public GameCanvas()
	{
		setBackground (Color.WHITE);
	}
	public void init()
	{
		backbuffer = createImage( GameMap.WIDTH, GameMap.HEIGHT );
	    backg = (Graphics2D)backbuffer.getGraphics();
	    backg.setBackground( Color.white );
	    backg.clearRect(0, 0, GameMap.WIDTH, GameMap.HEIGHT);
	    backg.setFont(backg.getFont().deriveFont(GRID_PIXELS));
	}
	public void updateGraphics()
	{
		backg.setColor(Color.BLACK);
		backg.clearRect(0, 0, GameMap.WIDTH, GameMap.HEIGHT);
		for(int i = 0; i < gameMap.getPlayers().size();i++) {
			Player p = gameMap.getPlayers().get(i);
			if (p.getHealth() > 0){
				backg.fillOval((int)p.getLocation().getX()-8,(int)p.getLocation().getY()-8, 16, 16);
				Rectangle gun2;
				if(p.getWeapon().getType().equals("rifle")) gun2 = new Rectangle((int)p.getLocation().getX()-8,(int)p.getLocation().getY(), 4, 12);
				else if(p.getWeapon().getType().equals("grenade")) gun2 = new Rectangle((int)p.getLocation().getX()-8,(int)p.getLocation().getY(), 7, 8);
				else  gun2 = new Rectangle((int)p.getLocation().getX()-8,(int)p.getLocation().getY(), 4, 12);
				AffineTransform transform = new AffineTransform();
				transform.rotate(p.getOrientation(), p.getLocation().x, p.getLocation().y);
				backg.draw(transform.createTransformedShape(gun2));
			}
			//backg.rotate(p.getOrientation(),(int)p.getLocation().getX()-8,(int)p.getLocation().getY()-8);
		}
		for(int i = 0; i < gameMap.getMap().length;i++)
		{
			for(int j = 0; j < gameMap.getMap()[i].length;j++)
				if(gameMap.getMap()[i][j] == 'X') backg.fillRect(i*GRID_PIXELS, j*GRID_PIXELS, GRID_PIXELS, GRID_PIXELS);
				else if( gameMap.getMap()[i][j] != '_')  { backg.drawRect(i*GRID_PIXELS, j*GRID_PIXELS, GRID_PIXELS, GRID_PIXELS); backg.drawString("" + gameMap.getMap()[i][j], i*GRID_PIXELS, (j+1)*GRID_PIXELS); }
		}
		backg.setColor(new Color(0f,0f,0f,0.3f));
		backg.fillRect(GameMap.WIDTH-200,GameMap.HEIGHT-50,200,50);
		backg.setColor(new Color(0f,0f,0f,0.5f));
		backg.drawString("Health: " + (gameMap.getPlayer().getType() == PlayerType.PERSON ? (int)gameMap.getPlayer().getHealth() : 0) + "%", GameMap.WIDTH-115, GameMap.HEIGHT-25);
		if(gameMap.getPlayer().getHealth()>0 && gameMap.getPlayer().getType() == PlayerType.PERSON)
		{
			
			backg.drawString(gameMap.getPlayer().getCurrentWeapon().getName(), GameMap.WIDTH-115, GameMap.HEIGHT-38);
			if(gameMap.getPlayer().getCurrentWeapon().getClipCount()>=0)
				backg.drawString(""+gameMap.getPlayer().getCurrentWeapon().getClipCount(), GameMap.WIDTH-20, GameMap.HEIGHT-38);
			for(int i=0; i < gameMap.getPlayer().getCurrentWeapon().getClipSize()*((gameMap.getPlayer().getCurrentWeapon().getType().equalsIgnoreCase("shotgun"))? 1 : gameMap.getPlayer().getCurrentWeapon().getRoundsPerShot());i++)
			{	
				if(i<20)
					backg.fillRect(GameMap.WIDTH-7-(i*6),GameMap.HEIGHT-23, 4, 10);
				else
					backg.fillRect(GameMap.WIDTH-7-((i-20)*6),GameMap.HEIGHT-11, 4, 10);
			}
		}
		backg.setColor(Color.black);
		for(int i=0;i<gameMap.getBullets().size();i++) {
			Bullet b = gameMap.getBullets().get(i);
			Rectangle bullet = new Rectangle((int)b.getLocation().x,(int)b.getLocation().y, 1, 6);
			AffineTransform transformb = new AffineTransform();
			transformb.rotate(Math.atan2(b.getVelocity().y,b.getVelocity().x)+Math.PI/2, b.getLocation().x, b.getLocation().y);
			backg.draw(transformb.createTransformedShape(bullet));
		}
		repaint();
	}
	public void update(Graphics g) {
		g.drawImage(backbuffer,0,0,this);
	}
	public void paint (Graphics g) {
		update(g);
	}
	public GameMap getGameMap() {
		return gameMap;
	}
	public void setGameMap(GameMap gameMap) {
		this.gameMap = gameMap;
	}
	
}
