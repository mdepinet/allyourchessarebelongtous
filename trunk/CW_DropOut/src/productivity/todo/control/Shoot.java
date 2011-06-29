package productivity.todo.control;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;

import productivity.todo.model.GameMap;
import productivity.todo.view.GameFrame;

public class Shoot implements KeyListener {
	static final int UPDATE_RATE = 30;  // number of game update per second
	static final long UPDATE_PERIOD = 1000000000L / UPDATE_RATE;  // nanoseconds
	GameFrame frame;
	GameMap map;
	public Shoot()
	{
		map = new GameMap();
		frame = new GameFrame("Shoot", map);
		frame.addKeyListener(this);
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
					map.getPlayer().setDirection(new Point2D.Double(map.getPlayer().getDirection().getX(),1));
					break;
				case KeyEvent.VK_UP:
					map.getPlayer().setDirection(new Point2D.Double(map.getPlayer().getDirection().getX(),-1));
					break;
				case KeyEvent.VK_LEFT:
					map.getPlayer().setDirection(new Point2D.Double(-1,map.getPlayer().getDirection().getY()));
					break;
				case KeyEvent.VK_RIGHT:
					map.getPlayer().setDirection(new Point2D.Double(1,map.getPlayer().getDirection().getY()));
					break;
				default:
					map.getPlayer().setDirection(new Point2D.Double(map.getPlayer().getDirection().getX(),map.getPlayer().getDirection().getY()));
					break;
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {
			switch(e.getKeyCode())
			{
				case KeyEvent.VK_DOWN:
					map.getPlayer().setDirection(new Point2D.Double(map.getPlayer().getDirection().getX(),0));
					break;
				case KeyEvent.VK_UP:
					map.getPlayer().setDirection(new Point2D.Double(map.getPlayer().getDirection().getX(),0));
					break;
				case KeyEvent.VK_LEFT:
					map.getPlayer().setDirection(new Point2D.Double(0,map.getPlayer().getDirection().getY()));
					break;
				case KeyEvent.VK_RIGHT:
					map.getPlayer().setDirection(new Point2D.Double(0,map.getPlayer().getDirection().getY()));
					break;
				default:
					map.getPlayer().setDirection(new Point2D.Double(map.getPlayer().getDirection().getX(),map.getPlayer().getDirection().getY()));
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
}
