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
import productivity.todo.model.Weapon;

public class GameCanvas extends Canvas {
	private GameMap gameMap;
	private Image backbuffer;
	private Graphics2D backg;

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
	}
	public void updateGraphics()
	{
		backg.setColor(Color.BLACK);
		backg.clearRect(0, 0, GameMap.WIDTH, GameMap.HEIGHT);
		for(Player p: gameMap.getPlayers()) {
			backg.fillOval((int)p.getLocation().getX()-8,(int)p.getLocation().getY()-8, 16, 16);
			Rectangle gun2;
			if(p.getWeapon().getType().equals("rifle")) gun2 = new Rectangle((int)p.getLocation().getX()-8,(int)p.getLocation().getY(), 4, 12);
			else if(p.getWeapon().getType().equals("grenade")) gun2 = new Rectangle((int)p.getLocation().getX()-8,(int)p.getLocation().getY(), 7, 8);
			else  gun2 = new Rectangle((int)p.getLocation().getX()-8,(int)p.getLocation().getY(), 4, 12);
			AffineTransform transform = new AffineTransform();
			transform.rotate(p.getOrientation(), p.getLocation().x, gameMap.getPlayer().getLocation().y);
			backg.draw(transform.createTransformedShape(gun2));
			//backg.rotate(p.getOrientation(),(int)p.getLocation().getX()-8,(int)p.getLocation().getY()-8);
		}
		for(int i = 0; i < 20;i++)
		{
			for(int j = 0; j < 20;j++)
				if(gameMap.getMap()[i][j]) backg.fillRect(i*25, j*25, 25, 25);
		}
		backg.fillOval((int)gameMap.getPlayer().getLocation().getX()-8,(int)gameMap.getPlayer().getLocation().getY()-8, 16, 16);

		Rectangle gun = new Rectangle((int)gameMap.getPlayer().getLocation().getX()-8,(int)gameMap.getPlayer().getLocation().getY(), 4, 12);
		AffineTransform transform = new AffineTransform();
		transform.rotate(gameMap.getPlayer().getOrientation(), gameMap.getPlayer().getLocation().x, gameMap.getPlayer().getLocation().y);
		backg.draw(transform.createTransformedShape(gun));
		gameMap.getPlayer().getWeapon().update();
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