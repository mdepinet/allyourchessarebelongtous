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
	private ArrayList<Bullet> bullets;
	public GameCanvas()
	{
		setBackground (Color.WHITE);
		bullets = new ArrayList<Bullet>();
	}
	public void paint (Graphics g) {
		Graphics2D g2;
		g2 = (Graphics2D) g;
		g2.setColor(Color.BLACK);
		for(Player p: gameMap.getPlayers()) {
			g2.fillOval((int)p.getLocation().getX()-8,(int)p.getLocation().getY()-8, 16, 16);
			Rectangle gun2;
			if(p.getWeapon().getType().equals("rifle")) gun2 = new Rectangle((int)p.getLocation().getX()-8,(int)p.getLocation().getY(), 4, 12);
			else if(p.getWeapon().getType().equals("grenade")) gun2 = new Rectangle((int)p.getLocation().getX()-8,(int)p.getLocation().getY(), 7, 8);
			else  gun2 = new Rectangle((int)p.getLocation().getX()-8,(int)p.getLocation().getY(), 4, 12);
			AffineTransform transform = new AffineTransform();
			transform.rotate(p.getOrientation(), p.getLocation().x, gameMap.getPlayer().getLocation().y);
			g2.draw(transform.createTransformedShape(gun2));
			//g2.rotate(p.getOrientation(),(int)p.getLocation().getX()-8,(int)p.getLocation().getY()-8);
		}
		g2.fillOval((int)gameMap.getPlayer().getLocation().getX()-8,(int)gameMap.getPlayer().getLocation().getY()-8, 16, 16);

		Rectangle gun = new Rectangle((int)gameMap.getPlayer().getLocation().getX()-8,(int)gameMap.getPlayer().getLocation().getY(), 4, 12);
		AffineTransform transform = new AffineTransform();
		transform.rotate(gameMap.getPlayer().getOrientation(), gameMap.getPlayer().getLocation().x, gameMap.getPlayer().getLocation().y);
		g2.draw(transform.createTransformedShape(gun));
		gameMap.getPlayer().getWeapon().update();
		for(int i=0;i<bullets.size();i++) {
			Bullet b = bullets.get(i);
			b.update();
			if(b.getLocation().x>500 || b.getLocation().x<0 || b.getLocation().y <0 || b.getLocation().y>500) bullets.remove(i--);
			else {
				Rectangle bullet = new Rectangle((int)b.getLocation().x,(int)b.getLocation().y, 1, 6);
				AffineTransform transformb = new AffineTransform();
				transformb.rotate(Math.atan2(b.getVelocity().y,b.getVelocity().x)+Math.PI/2, b.getLocation().x, b.getLocation().y);
				g2.draw(transformb.createTransformedShape(bullet));
			}
		}
	}
	public GameMap getGameMap() {
		return gameMap;
	}
	public void setGameMap(GameMap gameMap) {
		this.gameMap = gameMap;
	}
	public void shoot(int x, int y, double angle, Point2D.Double shootLoc, Weapon weapon) {
		Bullet bullet = new Bullet(weapon);
		bullet.setLocation(shootLoc);
		bullet.setVelocity(new Point2D.Double(Math.cos(angle+Math.PI/2)*weapon.getBulletSpeed(),Math.sin(angle+Math.PI/2)*weapon.getBulletSpeed()));
		
		bullets.add(bullet);
		/*Graphics2D g2 = (Graphics2D)getGraphics();
		Rectangle bullet = new Rectangle((int)map.getPlayer().getLocation().getX()-8,(int)map.getPlayer().getLocation().getY(), 1, 6);
		AffineTransform transform = new AffineTransform();
		transform.rotate(map.getPlayer().getOrientation(), map.getPlayer().getLocation().x, map.getPlayer().getLocation().y);
		g2.draw(transform.createTransformedShape(bullet));
		bulletUpdate(bullet);*/
	}
	
}
