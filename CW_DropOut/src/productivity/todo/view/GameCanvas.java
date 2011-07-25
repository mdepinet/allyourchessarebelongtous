package productivity.todo.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import javax.imageio.ImageIO;

import productivity.todo.model.Bullet;
import productivity.todo.model.Explosion;
import productivity.todo.model.GameMap;
import productivity.todo.model.Player;
import productivity.todo.model.PlayerType;
import productivity.todo.model.Weapon;

public class GameCanvas extends Canvas {
	private GameMap gameMap;
	private Image backbuffer;
	private Graphics2D backg;
	private BufferedImage explosionImage;
	public static final int GRID_PIXELS = 25;
	public static final int FRAMES_PER_SECOND = 30;

	public GameCanvas()
	{
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
		for(int i = 0; i < gameMap.getMap().length;i++)
		{
			for(int j = 0; j < gameMap.getMap()[i].length;j++)
				if(gameMap.getMap()[i][j] == 'X') backg.fillRect(i*GRID_PIXELS, j*GRID_PIXELS, GRID_PIXELS, GRID_PIXELS);
				else if(("" + gameMap.getMap()[i][j]).matches("[L-O]")) { 
						backg.drawOval(i*GRID_PIXELS-GRID_PIXELS/2, j*GRID_PIXELS-GRID_PIXELS/2, GRID_PIXELS*2, GRID_PIXELS*2);
						Image img;
						if((img = new Weapon(gameMap.getMap()[i][j], new Point(i,j)).getImage())!=null)
							backg.drawImage(img,i*GRID_PIXELS, j*GRID_PIXELS+GRID_PIXELS/4, 30, 15, this);
					}
				else if( gameMap.getMap()[i][j] != '_')  { backg.drawRect(i*GRID_PIXELS, j*GRID_PIXELS, GRID_PIXELS, GRID_PIXELS); backg.drawString("" + gameMap.getMap()[i][j], i*GRID_PIXELS+GRID_PIXELS/3, (j+1)*GRID_PIXELS-GRID_PIXELS/4); }
		}
		for(int i = 0; i < gameMap.getPlayers().size();i++) {
			Player p = gameMap.getPlayers().get(i);
			if (p.getHealth() > 0){
				Rectangle gun2;
				if(p.getCurrentWeapon().getType().equals("rifle")) gun2 = new Rectangle((int)p.getLocation().getX()-8,(int)p.getLocation().getY(), 4, 12);
				else if(p.getCurrentWeapon().getType().equals("grenade")) gun2 = new Rectangle((int)p.getLocation().getX()-8,(int)p.getLocation().getY(), 7, 8);
				else  gun2 = new Rectangle((int)p.getLocation().getX()-8,(int)p.getLocation().getY(), 4, 12);
				AffineTransform transform = new AffineTransform();
				transform.rotate(p.getOrientation(), p.getLocation().x, p.getLocation().y);
				backg.draw(transform.createTransformedShape(gun2));

				switch(p.getTeam())
				{
					case 1: backg.setColor(new Color(0f,0f,0.5f)); break;
					case 2: backg.setColor(new Color(0.5f,0f,0f)); break;
					case 3: backg.setColor(new Color(0f,0.5f,0f)); break;
					case 4: backg.setColor(new Color(.8f,0.4f,0f)); break;
					default: break;
				}
				 FontMetrics metrics = backg.getFontMetrics(backg.getFont());
				backg.fillOval((int)p.getLocation().getX()-8,(int)p.getLocation().getY()-8, 16, 16);
				backg.drawString(p.getName(), (int)(p.getLocation().getX()-metrics.stringWidth(p.getName())/2), (int)(p.getLocation().getY()-p.getRadius()-2));
				backg.setColor(Color.BLACK);
				if(p.getCurrentWeapon().getImage()!=null)
				{
					backg.drawImage(p.getCurrentWeapon().getImage(), (int)p.getLocation().x+6, (int)p.getLocation().y-15, 30, 15, this);
				}
			}
			//backg.rotate(p.getOrientation(),(int)p.getLocation().getX()-8,(int)p.getLocation().getY()-8);
		}
		for(Explosion e: gameMap.getExplosions())
		{
			backg.drawImage(createTranslucentImage(explosionImage,e.getAlpha()), e.getRect().x, e.getRect().y, e.getRect().width, e.getRect().height, null);
		}
		backg.setColor(new Color(0f,0f,0f,0.3f));
		backg.fillRect(GameMap.WIDTH-200,GameMap.HEIGHT-50,200,50);
		backg.setColor(new Color(0f,0f,0f,0.5f));
		if(gameMap.getPlayer()!=null)
		{
			backg.setColor(new Color((gameMap.getPlayer().getHealth()>50) ? (float)(1-gameMap.getPlayer().getHealth()/100) : 1.0f,(gameMap.getPlayer().getHealth()<=50) ? (float)(gameMap.getPlayer().getHealth()/50):1.0f,0f,0.5f));
			backg.fillRect(GameMap.WIDTH-105, GameMap.HEIGHT-35, (int)gameMap.getPlayer().getHealth(), 10);
			backg.setColor(new Color(0f,0f,0f,0.5f));
			backg.drawString("Health:", GameMap.WIDTH-145, GameMap.HEIGHT-26);
			backg.drawString(gameMap.getPlayer().getCurrentWeapon().getName(), GameMap.WIDTH-115, GameMap.HEIGHT-38);
			if(gameMap.getPlayer().getCurrentWeapon().getClipCount()>=0)
				backg.drawString(""+gameMap.getPlayer().getCurrentWeapon().getClipCount(), GameMap.WIDTH-20, GameMap.HEIGHT-38);
			if(gameMap.getPlayer().getCurrentWeapon().getClipSize()==0 && !gameMap.getPlayer().getCurrentWeapon().getType().equalsIgnoreCase("melee"))
				backg.drawString("Reloading...", GameMap.WIDTH-100, GameMap.HEIGHT-10);
			else{
				for(int i=0; i < gameMap.getPlayer().getCurrentWeapon().getClipSize()*((gameMap.getPlayer().getCurrentWeapon().getType().equalsIgnoreCase("shotgun"))? 1 : gameMap.getPlayer().getCurrentWeapon().getRoundsPerShot());i++)
				{	
					if(i<20)
						backg.fillRect(GameMap.WIDTH-7-(i*6),GameMap.HEIGHT-23, 4, 10);
					else
						backg.fillRect(GameMap.WIDTH-7-((i-20)*6),GameMap.HEIGHT-11, 4, 10);
				}
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
