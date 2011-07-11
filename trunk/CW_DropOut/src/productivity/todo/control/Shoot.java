package productivity.todo.control;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import productivity.todo.model.GameMap;
import productivity.todo.view.GameFrame;

public class Shoot implements KeyListener, MouseListener, MouseMotionListener {
	static final int UPDATE_RATE = 30;  // number of game update per second
	static final long UPDATE_PERIOD = 1000000000L / UPDATE_RATE;  // nanoseconds
	GameFrame frame;
	GameMap map;
	public Shoot()
	{
		map = new GameMap();
		frame = new GameFrame("Shoot", map);
		frame.getCanvas().addKeyListener(this);
		frame.getCanvas().setFocusable(true);
		frame.getCanvas().addMouseListener(this);
		frame.getCanvas().addMouseMotionListener(this);
		gameStart();
	}
	 public void gameStart() { 
	      // Create a new thread
	      Thread gameThread =  new Thread() {
	         // Override run() to provide the running behavior of this thread.
	         @Override
	         public void run() {
	            gameLoop();
	         }
	      };
	      // Start the thread. start() calls run(), which in turn calls gameLoop().
	      gameThread.start();
	   }
	   
	   // Run the game loop here.
	   private void gameLoop() {
	      
	   
	      // Game loop
	      long beginTime, timeTaken, timeLeft;
	      while (true) {
	         beginTime = System.nanoTime();
	         map.gameUpdate();
	         // Refresh the display
	         frame.getCanvas().repaint();
	         // Delay timer to provide the necessary delay to meet the target rate
	         timeTaken = System.nanoTime() - beginTime;
	         timeLeft = (UPDATE_PERIOD - timeTaken) / 1000000L;  // in milliseconds
	         if (timeLeft < 10) timeLeft = 10;   // set a minimum
	         try {
	            // Provides the necessary delay and also yields control so that other thread can do work.
	            Thread.sleep(timeLeft);
	         } catch (InterruptedException ex) { }
	      }
	   }
	   @Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode())
			{
				case KeyEvent.VK_DOWN:
					map.getPlayerByName("player1").setDirection(new Point2D.Double(map.getPlayer().getDirection().getX(),1));
					break;
				case KeyEvent.VK_UP:
					map.getPlayerByName("player1").setDirection(new Point2D.Double(map.getPlayer().getDirection().getX(),-1));
					break;
				case KeyEvent.VK_LEFT:
					map.getPlayerByName("player1").setDirection(new Point2D.Double(-1,map.getPlayer().getDirection().getY()));
					break;
				case KeyEvent.VK_RIGHT:
					map.getPlayerByName("player1").setDirection(new Point2D.Double(1,map.getPlayer().getDirection().getY()));
					break;
				case KeyEvent.VK_S:
					map.getPlayerByName("player1").setDirection(new Point2D.Double(map.getPlayer().getDirection().getX(),1));
					break;
				case KeyEvent.VK_W:
					map.getPlayerByName("player1").setDirection(new Point2D.Double(map.getPlayer().getDirection().getX(),-1));
					break;
				case KeyEvent.VK_A:
					map.getPlayerByName("player1").setDirection(new Point2D.Double(-1,map.getPlayer().getDirection().getY()));
					break;
				case KeyEvent.VK_D:
					map.getPlayerByName("player1").setDirection(new Point2D.Double(1,map.getPlayer().getDirection().getY()));
					break;
				default:
					map.getPlayerByName("").setDirection(new Point2D.Double(map.getPlayer().getDirection().getX(),map.getPlayer().getDirection().getY()));
					break;
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {
			switch(e.getKeyCode())
			{
				case KeyEvent.VK_DOWN:
					map.getPlayerByName("player1").setDirection(new Point2D.Double(map.getPlayer().getDirection().getX(),0));
					break;
				case KeyEvent.VK_UP:
					map.getPlayerByName("player1").setDirection(new Point2D.Double(map.getPlayer().getDirection().getX(),0));
					break;
				case KeyEvent.VK_LEFT:
					map.getPlayerByName("player1").setDirection(new Point2D.Double(0,map.getPlayer().getDirection().getY()));
					break;
				case KeyEvent.VK_RIGHT:
					map.getPlayerByName("player1").setDirection(new Point2D.Double(0,map.getPlayer().getDirection().getY()));
					break;
				default:
					map.getPlayerByName("").setDirection(new Point2D.Double(map.getPlayer().getDirection().getX(),map.getPlayer().getDirection().getY()));
					break;
			}
		}
		@Override
		public void keyTyped(KeyEvent e) {
			
		}
	public static void main(String[] args)
	{
		new Shoot();
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		//System.out.println("Click");
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		//System.out.println(e.getX());
		//map.getPlayer().setOrientation((e.getY()-map.getPlayer().getLocation().y)/(e.getX()-map.getPlayer().getLocation().x));
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if(map.getPlayer().getWeapon().canShoot())
			frame.getCanvas().shoot(e.getX(),e.getY(), map.getPlayer().getOrientation(),map.getPlayer().getGunLocation(), map.getPlayer().getWeapon());
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		map.getPlayer().setOrientation(Math.atan2((e.getY()-map.getPlayer().getLocation().y), (e.getX()-map.getPlayer().getLocation().x))-Math.PI/2);

	}
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		map.getPlayer().setOrientation(Math.atan2((e.getY()-map.getPlayer().getLocation().y), (e.getX()-map.getPlayer().getLocation().x))-Math.PI/2);
	}
	
}
