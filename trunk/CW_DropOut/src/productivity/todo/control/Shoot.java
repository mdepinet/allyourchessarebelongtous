package productivity.todo.control;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.io.File;

import productivity.todo.config.GameMode;
import productivity.todo.menu.MainMenuFrame;
import productivity.todo.model.GameMap;
import productivity.todo.view.GameFrame;

public class Shoot implements KeyListener, MouseListener, MouseMotionListener {
	static final int UPDATE_RATE = 30;  // number of game update per second
	static final long UPDATE_PERIOD = 1000000000L / UPDATE_RATE;  // nanoseconds
	GameFrame frame;
	GameMap map;
	private int holdCounter;
	private Point mouseLoc;
	public Shoot()
	{
		new MainMenuFrame(this);
	}
	public void startGame(File mapFile, GameMode mode)
	{
		holdCounter = -1;
		map = new GameMap(mapFile);
		mode.setGameMap(map);
		map.setGameMode(mode);
		map.init();
		mouseLoc = new Point();
		frame = new GameFrame("Shoot", map);
		frame.getCanvas().addKeyListener(this);
		frame.getCanvas().setFocusable(true);
		frame.getCanvas().addMouseListener(this);
		frame.getCanvas().addMouseMotionListener(this);
		frame.getCanvas().requestFocusInWindow();
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
	         if(map.getPlayer()!=null){
		         map.getPlayer().setOrientation(Math.atan2((mouseLoc.getY()-map.getPlayer().getLocation().y), (mouseLoc.getX()-map.getPlayer().getLocation().x))-Math.PI/2);
		         if(holdCounter>=0)
		         {
		        	 holdCounter++;
		        	 if(holdCounter > 15 && map.getPlayer().getCurrentWeapon().canShoot())
		     			map.shoot(map.getPlayer());
		         }
	         }
	         else
	        	 holdCounter = -1;
	         map.gameUpdate();
	         
	         // Refresh the display
	         frame.getCanvas().updateGraphics();
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
		   if(map.getPlayer()==null) return;
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
				case KeyEvent.VK_S:
					map.getPlayer().setDirection(new Point2D.Double(map.getPlayer().getDirection().getX(),1));
					break;
				case KeyEvent.VK_W:
					map.getPlayer().setDirection(new Point2D.Double(map.getPlayer().getDirection().getX(),-1));
					break;
				case KeyEvent.VK_A:
					map.getPlayer().setDirection(new Point2D.Double(-1,map.getPlayer().getDirection().getY()));
					break;
				case KeyEvent.VK_D:
					map.getPlayer().setDirection(new Point2D.Double(1,map.getPlayer().getDirection().getY()));
					break;
				default:
					break;
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {
			if(map.getPlayer()==null) return;
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
				case KeyEvent.VK_S:
					map.getPlayer().setDirection(new Point2D.Double(map.getPlayer().getDirection().getX(),0));
					break;
				case KeyEvent.VK_W:
					map.getPlayer().setDirection(new Point2D.Double(map.getPlayer().getDirection().getX(),0));
					break;
				case KeyEvent.VK_A:
					map.getPlayer().setDirection(new Point2D.Double(0,map.getPlayer().getDirection().getY()));
					break;
				case KeyEvent.VK_D:
					map.getPlayer().setDirection(new Point2D.Double(0,map.getPlayer().getDirection().getY()));
					break;
				case KeyEvent.VK_SHIFT:
					map.getPlayer().nextWeapon();
					break;
				default:
					if(e.getKeyChar()>47 && e.getKeyChar()<58)
						map.getPlayer().switchToWeapon(e.getKeyChar()-48);
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
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		if(map.getPlayer()==null)
		{
			holdCounter = -1;
			return;
		}
		holdCounter++;
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		if(map.getPlayer()==null) return;
		if(map.getPlayer().getCurrentWeapon().canShoot())
			map.shoot(map.getPlayer());
		holdCounter=-1;
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		if(map.getPlayer()==null) return;
		map.getPlayer().setOrientation(Math.atan2((e.getY()-map.getPlayer().getLocation().y), (e.getX()-map.getPlayer().getLocation().x))-Math.PI/2);
		mouseLoc=new Point(e.getX(),e.getY());
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		if(map.getPlayer()==null) return;
		mouseLoc=new Point(e.getX(),e.getY());
		map.getPlayer().setOrientation(Math.atan2((e.getY()-map.getPlayer().getLocation().y), (e.getX()-map.getPlayer().getLocation().x))-Math.PI/2);
	}
	
}