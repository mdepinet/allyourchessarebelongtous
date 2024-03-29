package org.cwi.shoot.view;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.cwi.shoot.config.GameMode;
import org.cwi.shoot.config.GameOptions;
import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Bullet;
import org.cwi.shoot.model.Explosion;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Player.PlayerType;
import org.cwi.shoot.model.Weapon;

public class GameCanvas extends Canvas {
	private static final long serialVersionUID = 4179148853338726809L;
	public static final int GRID_PIXELS = GameMap.GRID_PIXELS;
	public static final int FRAMES_PER_SECOND = 30;
	
	private Image backbuffer;
	private Graphics2D backg;
	private BufferedImage explosionImage;
	private boolean smallerFrame = false;
	private Point2D.Double playerLoc = new Point2D.Double();
	private Rectangle bounds;
	
	public GameCanvas(Rectangle bounds) {
		if(bounds.getWidth() < GameFrame.WIDTH || bounds.getHeight() < GameFrame.HEIGHT) {
			smallerFrame = true;
			this.bounds = bounds;
		}
		setBackground (Color.WHITE);
		explosionImage = null;
        try {
        	explosionImage = ImageIO.read(new File("resource/images/explosion.gif"));  
        } catch(IOException e) {}
	}
	
    public BufferedImage createTranslucentImage(BufferedImage image, double transperancy) {  
        BufferedImage aimg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TRANSLUCENT);
        Graphics2D g = aimg.createGraphics(); 
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)transperancy));
        g.drawImage(image, null, 0, 0);  
        g.dispose();
        return aimg;  
    }
    
	public void init() {
		backbuffer = createImage( GameFrame.WIDTH, GameFrame.HEIGHT );
	    backg = (Graphics2D)backbuffer.getGraphics();
	    backg.setBackground( Color.white );
	    backg.clearRect(0, 0, WIDTH, HEIGHT);
	    backg.setFont(backg.getFont().deriveFont(GRID_PIXELS));
	}
	
	public void updateGraphics(GameMap gameMap, GameMode mode) {
		if(!smallerFrame) {
			updateNormalScreen(gameMap, mode);
			repaint();
		}
		else {
			updateSmallerScreen(gameMap, mode);
			repaint();
		}
	}
	public void updateNormalScreen(GameMap gameMap, GameMode mode) {
		double HRZ_SCALE = (double)GameFrame.WIDTH / gameMap.getPixelWidth();
		double VERT_SCALE = (double)GameFrame.HEIGHT / gameMap.getPixelHeight();
		backg.setColor(Color.BLACK);
		backg.clearRect(0, 0, GameFrame.WIDTH, GameFrame.HEIGHT);
		
		//Draw mode specific stuff
		mode.drawModeMapPre(backg, null);
		backg.setColor(Color.black);
		//Draw map
		for(int i = 0; i < gameMap.getMap().length;i++) {
			for(int j = 0; j < gameMap.getMap()[i].length; j++)
				if(gameMap.getMap()[i][j] == GameOptions.WALL_CHARACTER)
					backg.fillRect((int) Math.round(i*GRID_PIXELS*HRZ_SCALE),
							(int) Math.round(j*GRID_PIXELS*VERT_SCALE),
							(int) Math.round(GRID_PIXELS*HRZ_SCALE),
							(int) Math.round(GRID_PIXELS*VERT_SCALE));
				else if(gameMap.getMap()[i][j] != GameOptions.BLANK_CHARACTER && !mode.getAdditionalMapChars().contains(gameMap.getMap()[i][j])) {
					backg.drawRect((int) Math.round(i*GRID_PIXELS*HRZ_SCALE),
							(int) Math.round(j*GRID_PIXELS*VERT_SCALE),
							(int) Math.round(GRID_PIXELS*HRZ_SCALE),
							(int) Math.round(GRID_PIXELS*VERT_SCALE));
					backg.drawString("" + gameMap.getMap()[i][j], (int) Math.round(HRZ_SCALE*(i*GRID_PIXELS+GRID_PIXELS/3)), (int) Math.round(VERT_SCALE*((j+1)*GRID_PIXELS-GRID_PIXELS/4)));
				}
		}
		
		//Draw players
		for(int i = 0; i < gameMap.getPlayers().size();i++) {
			Player p = gameMap.getPlayers().get(i);
			if (p.getHealth() > 0 && p!=null){
				//Draw weapon
				if (p.getCurrWeapon() != null){
					AffineTransform transform = new AffineTransform();
					backg.setColor(Color.BLACK);
					Rectangle gun2;
					gun2 = new Rectangle((int)Math.round(HRZ_SCALE*(p.getLocation().getX()-Player.radius)),(int)Math.round(VERT_SCALE*p.getLocation().getY()), (int) Math.round(4*HRZ_SCALE), (int) Math.round(12*VERT_SCALE));
					transform.rotate(p.getOrientation(), (int)Math.round(HRZ_SCALE*(p.getLocation().getX())),(int)Math.round(VERT_SCALE*p.getLocation().getY()));
					if (!p.getCurrWeapon().getTypes().contains(Weapon.WeaponType.THROWN)) backg.draw(transform.createTransformedShape(gun2));
					
				}

				//Draw player and name
				switch(p.getTeam()) {
					case 1: backg.setColor(new Color(0f,0f,0.5f)); break;
					case 2: backg.setColor(new Color(0.5f,0f,0f)); break;
					case 3: backg.setColor(new Color(0f,0.5f,0f)); break;
					case 4: backg.setColor(new Color(.8f,0.4f,0f)); break;
					default: break;
				}
				FontMetrics metrics = backg.getFontMetrics(backg.getFont());
				if(p.isTurret()) backg.fillRect((int)Math.round(HRZ_SCALE*(p.getLocation().getX()-Player.radius)),(int)Math.round(VERT_SCALE*(p.getLocation().getY()-Player.radius)), (int) Player.radius*2, (int) Player.radius*2);
				else backg.fillOval((int)Math.round(HRZ_SCALE*(p.getLocation().getX()-Player.radius)),(int)Math.round(VERT_SCALE*(p.getLocation().getY()-Player.radius)), (int) Player.radius*2, (int) Player.radius*2);
				backg.drawString(p.getName(), (int)Math.round(HRZ_SCALE*(p.getLocation().getX()-metrics.stringWidth(p.getName())/2)), (int)Math.round(VERT_SCALE*(p.getLocation().getY()-Player.radius-2)));
			}
		}
		
		mode.drawModeMapPost(backg, gameMap.getPlayers(), null);
		
		//Draw explosions
		for(Explosion e: gameMap.getExplosions()) {
			backg.drawImage(createTranslucentImage(explosionImage,e.getAlpha()), (int) Math.round(HRZ_SCALE*e.getRect().x), (int) Math.round(VERT_SCALE*e.getRect().y), (int) Math.round(HRZ_SCALE*e.getRect().width), (int) Math.round(VERT_SCALE*e.getRect().height), null);
		}
		
		//Draw bullets
		backg.setColor(Color.black);
		for(int i=0;i<gameMap.getBullets().size();i++) {
			Bullet b = gameMap.getBullets().get(i);
			BufferedImage bulletImg = null;
			if (b.getBulletImgLoc() != null && !b.getBulletImgLoc().equals("")){
				bulletImg = Bullet.getBulletImg(b.getBulletImgLoc());
			}
			AffineTransform transformb = new AffineTransform();
			if (bulletImg == null){
				Rectangle bullet = new Rectangle((int)Math.round(b.getLocation().x*HRZ_SCALE),(int) Math.round(b.getLocation().y*VERT_SCALE),(int) Math.max(Math.round(1*HRZ_SCALE),1), (int) Math.max(Math.round(6*VERT_SCALE),1));
				transformb.rotate(Math.atan2(b.getVelocity().y,b.getVelocity().x)+Math.PI/2, (int)Math.round(b.getLocation().x*HRZ_SCALE),(int) Math.round(b.getLocation().y*VERT_SCALE));
				backg.draw(transformb.createTransformedShape(bullet));
			}
			else{
				backg.drawImage(bulletImg, (int)b.getLocation().x, (int)b.getLocation().y, 6, 6, this);
			}
		}
	}
	public void updateSmallerScreen(GameMap gameMap, GameMode mode) {
		double HRZ_SCALE = (double)GameFrame.WIDTH / gameMap.getPixelWidth();
		double VERT_SCALE = (double)GameFrame.HEIGHT / gameMap.getPixelHeight();
		backg.setColor(Color.BLACK);
		backg.fillRect((int)-getBounds().getWidth(), (int)-getBounds().getHeight(), (int)(GameFrame.WIDTH + getBounds().getWidth()), (int)(GameFrame.HEIGHT + getBounds().getHeight()));
		backg.clearRect(0-(int)playerLoc.x, 0-(int)playerLoc.y, GameFrame.WIDTH, GameFrame.HEIGHT);
		
		//Draw mode specific stuff
		mode.drawModeMapPre(backg, playerLoc);
		backg.setColor(Color.black);
		//Draw map
		for(int i = 0; i < gameMap.getMap().length;i++) {
			for(int j = 0; j < gameMap.getMap()[i].length; j++)
				if(gameMap.getMap()[i][j] == GameOptions.WALL_CHARACTER)
					backg.fillRect((int) Math.round(i*GRID_PIXELS*HRZ_SCALE)-(int)playerLoc.x,
							(int) Math.round(j*GRID_PIXELS*VERT_SCALE)-(int)playerLoc.y,
							(int) Math.round(GRID_PIXELS*HRZ_SCALE),
							(int) Math.round(GRID_PIXELS*VERT_SCALE));
				else if(gameMap.getMap()[i][j] != GameOptions.BLANK_CHARACTER && !mode.getAdditionalMapChars().contains(gameMap.getMap()[i][j])) {
					backg.drawRect((int) Math.round(i*GRID_PIXELS*HRZ_SCALE)-(int)playerLoc.x,
							(int) Math.round(j*GRID_PIXELS*VERT_SCALE)-(int)playerLoc.y,
							(int) Math.round(GRID_PIXELS*HRZ_SCALE),
							(int) Math.round(GRID_PIXELS*VERT_SCALE));
					backg.drawString("" + gameMap.getMap()[i][j], (int) Math.round(HRZ_SCALE*(i*GRID_PIXELS+GRID_PIXELS/3))-(int)playerLoc.x, (int) Math.round(VERT_SCALE*((j+1)*GRID_PIXELS-GRID_PIXELS/4))-(int)playerLoc.y);
				}
		}
		
		//Draw players
		for(int i = 0; i < gameMap.getPlayers().size();i++) {
			Player p = gameMap.getPlayers().get(i);
			if (p.getHealth() > 0 && p!=null){
				//Draw weapon
				if (p.getCurrWeapon() != null && p.getType()!=PlayerType.HUMAN){
					AffineTransform transform = new AffineTransform();
					backg.setColor(Color.BLACK);
					Rectangle gun2;
					gun2 = new Rectangle((int)Math.round(HRZ_SCALE*(p.getLocation().getX()-playerLoc.x-Player.radius)),(int)Math.round(VERT_SCALE*(p.getLocation().getY()-playerLoc.y)), (int) Math.round(4*HRZ_SCALE), (int) Math.round(12*VERT_SCALE));
					transform.rotate(p.getOrientation(), (int)Math.round(HRZ_SCALE*(p.getLocation().getX()-playerLoc.x)),(int)Math.round(VERT_SCALE*p.getLocation().getY()-playerLoc.y));
					if (!p.getCurrWeapon().getTypes().contains(Weapon.WeaponType.THROWN)) backg.draw(transform.createTransformedShape(gun2));
					
				}

				//Draw player and name
				switch(p.getTeam()) {
					case 1: backg.setColor(new Color(0f,0f,0.5f)); break;
					case 2: backg.setColor(new Color(0.5f,0f,0f)); break;
					case 3: backg.setColor(new Color(0f,0.5f,0f)); break;
					case 4: backg.setColor(new Color(.8f,0.4f,0f)); break;
					default: break;
				}
				if(p.getType()!=PlayerType.HUMAN) {
					FontMetrics metrics = backg.getFontMetrics(backg.getFont());	
					if(p.isTurret()) backg.fillRect((int)Math.round(HRZ_SCALE*(p.getLocation().getX()-Player.radius))-(int)playerLoc.x,(int)Math.round(VERT_SCALE*(p.getLocation().getY()-Player.radius))-(int)playerLoc.y, (int) Player.radius*2, (int) Player.radius*2);
					else backg.fillOval((int)Math.round(HRZ_SCALE*(p.getLocation().getX()-Player.radius))-(int)playerLoc.x,(int)Math.round(VERT_SCALE*(p.getLocation().getY()-Player.radius))-(int)playerLoc.y, (int) Player.radius*2, (int) Player.radius*2);
					backg.drawString(p.getName(), (int)Math.round(HRZ_SCALE*(p.getLocation().getX()-metrics.stringWidth(p.getName())/2))-(int)playerLoc.x, (int)Math.round(VERT_SCALE*(p.getLocation().getY()-Player.radius-2))-(int)playerLoc.y);
				}
				else {
					playerLoc = new Point2D.Double(HRZ_SCALE*(p.getLocation().getX()-Player.radius)-bounds.getWidth()/2, VERT_SCALE*(p.getLocation().getY()-Player.radius)-bounds.getHeight()/2);
					
					backg.fillOval((int)bounds.getWidth()/2,(int)bounds.getHeight()/2, (int) Player.radius*2, (int) Player.radius*2);
					backg.drawString(p.getName(), (int)bounds.getWidth()/2,(int)bounds.getHeight()/2);
					if(p.getCurrWeapon()!=null) {
						AffineTransform transform = new AffineTransform();
						backg.setColor(Color.BLACK);
						Rectangle gun2;
						gun2 = new Rectangle((int)(bounds.getWidth()/2+Player.radius*2-4),(int)(bounds.getHeight()/2-Player.radius/2), (int) Math.round(4*HRZ_SCALE), (int) Math.round(12*VERT_SCALE));
						transform.rotate(p.getOrientation()+Math.PI, (int)bounds.getWidth()/2+Player.radius,(int)bounds.getHeight()/2+Player.radius);
						if (!p.getCurrWeapon().getTypes().contains(Weapon.WeaponType.THROWN)) backg.draw(transform.createTransformedShape(gun2));
					}
				}
			}
		}
		
		mode.drawModeMapPost(backg, gameMap.getPlayers(), playerLoc);
		
		//Draw explosions
		for(Explosion e: gameMap.getExplosions()) {
			backg.drawImage(createTranslucentImage(explosionImage,e.getAlpha()), (int) Math.round(HRZ_SCALE*e.getRect().x)-(int)playerLoc.x, (int) Math.round(VERT_SCALE*e.getRect().y)-(int)playerLoc.y, (int) Math.round(HRZ_SCALE*e.getRect().width), (int) Math.round(VERT_SCALE*e.getRect().height), null);
		}
		
		//Draw bullets
		backg.setColor(Color.black);
		for(int i=0;i<gameMap.getBullets().size();i++) {
			Bullet b = gameMap.getBullets().get(i);
			BufferedImage bulletImg = null;
			if (b.getBulletImgLoc() != null && !b.getBulletImgLoc().equals("")){
				bulletImg = Bullet.getBulletImg(b.getBulletImgLoc());
			}
			AffineTransform transformb = new AffineTransform();
			if (bulletImg == null){
				Rectangle bullet = new Rectangle((int)Math.round(HRZ_SCALE*(b.getLocation().getX()-playerLoc.x)),(int)Math.round(VERT_SCALE*(b.getLocation().getY()-playerLoc.y)),(int) Math.max(Math.round(1*HRZ_SCALE),1), (int) Math.max(Math.round(6*VERT_SCALE),1));
				transformb.rotate(Math.atan2(b.getVelocity().y,b.getVelocity().x)+Math.PI/2, (int)Math.round(b.getLocation().x*HRZ_SCALE)-(int)playerLoc.x,(int) Math.round(b.getLocation().y*VERT_SCALE)-(int)playerLoc.y);
				backg.draw(transformb.createTransformedShape(bullet));
			}
			else{
				backg.drawImage(bulletImg, (int)b.getLocation().x-(int)playerLoc.x, (int)b.getLocation().y-(int)playerLoc.y, 6, 6, this);
			}
		}
	}
	public void update(Graphics g) {
		g.drawImage(backbuffer,0,0,this);
	}
	public void paint (Graphics g) {
		update(g);
	}
}
