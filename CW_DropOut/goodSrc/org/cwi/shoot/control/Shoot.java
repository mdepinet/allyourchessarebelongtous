package org.cwi.shoot.control;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.io.File;

import org.cwi.shoot.config.GameMode;
import org.cwi.shoot.config.GameOptions;
import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.menu.MainMenu;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Weapon;
import org.cwi.shoot.profile.Profile;
import org.cwi.shoot.threads.WeaponRemoverThread;
import org.cwi.shoot.util.VectorTools;
import org.cwi.shoot.util.WeaponLoader;
import org.cwi.shoot.view.GameFrame;
import org.cwi.shoot.view.PauseFrame;
import org.cwi.shoot.view.StatsFrame;

public class Shoot implements KeyListener, MouseListener, MouseMotionListener, ComponentListener, FocusListener {
	static final int UPDATE_RATE = 30;  // number of game update per second
	static final long UPDATE_PERIOD = 1000000000L / UPDATE_RATE;  // nanoseconds
	
	GameFrame frame;
	private StatsFrame statsFrame;
	GameMap map;
	GameMode mode;
	private int holdCounter;
	private Point mouseLoc;
	private boolean stop;
	private PauseFrame pause;
	
	//used by server
	private long _ID;
	private String mapName;
	private int numTeams;
	private int numPlayersPerTeam;
	private String weaponSet;
	
	public Shoot() {
		stop = false;
		new MainMenu(this, null);
	}
	
	public void startGame(Profile profile, File mapFile, File nameSetFile, GameMode mode, char[] teams, int team, String weaponSet, int numTeammates) {
		WeaponLoader.weaponSet = weaponSet;
		WeaponLoader.unloadAll();
		
		numTeams = teams.length;
		numPlayersPerTeam = numTeammates;
		this.weaponSet = weaponSet;
		mapName = mapFile.getName();
		
		GameOptions setup = new GameOptions(mode, mapFile, nameSetFile, teams.length, team, profile, numTeammates);
		holdCounter = -1;
		map = new GameMap(setup);
		this.mode = mode;
		mouseLoc = new Point();
		if(profile.getScreenSize()==null) frame = new GameFrame("Shoot", map, -1, -1);
		else frame = new GameFrame("Shoot", map, profile.getScreenSize().x, profile.getScreenSize().y);
		frame.setFocusable(true);
		frame.addFocusListener(this);
		frame.getCanvas().addKeyListener(this);
		frame.getCanvas().setFocusable(true);
		frame.getCanvas().addMouseListener(this);
		frame.getCanvas().addMouseMotionListener(this);
		frame.getCanvas().requestFocusInWindow();
		
		statsFrame = new StatsFrame(mode, map.getPlayers(), teams);
		statsFrame.setFocusable(true);
		statsFrame.addFocusListener(this);
		frame.addComponentListener(this);
		frame.addWindowListener(new WindowAdapter(){
	          public void windowIconified(WindowEvent e){
	                statsFrame.setVisible(false);
	          }
	          public void windowDeiconified(WindowEvent e){
	                statsFrame.setVisible(true);
	          }
	    });
		gameStart();
	}

	public void gameStart() { 
		// Create a new thread
		Thread gameThread =  new Thread() {
			@Override
			public void run() {
				gameLoop();
			}
		};
		gameThread.start();
	}
	public void resetFrame(Profile profile) {
		frame.dispose();
		frame = new GameFrame("Shoot", map, profile.getScreenSize().x, profile.getScreenSize().y);
		frame.setFocusable(true);
		frame.addFocusListener(this);
		frame.getCanvas().addKeyListener(this);
		frame.getCanvas().setFocusable(true);
		frame.getCanvas().addMouseListener(this);
		frame.getCanvas().addMouseMotionListener(this);
		frame.getCanvas().requestFocusInWindow();
		frame.addComponentListener(this);
		frame.addWindowListener(new WindowAdapter(){
	          public void windowIconified(WindowEvent e){
	                statsFrame.setVisible(false);
	          }
	          public void windowDeiconified(WindowEvent e){
	                statsFrame.setVisible(true);
	          }
	    });
	}
	   
	// Run the game loop here.
	private void gameLoop() {
		long beginTime, timeTaken, timeLeft;
		while (!stop) {
			beginTime = System.nanoTime();
			if(map.getPlayer()!=null){ 
				double HRZ_SCALE = (double)GameFrame.WIDTH / map.getPixelWidth();
				double VERT_SCALE = (double)GameFrame.HEIGHT / map.getPixelHeight();
				Point2D.Double playLoc;
				if(frame.isSmallerScreen()) {
					playLoc = new Point2D.Double(frame.getBounds().getWidth()/2*frame.getBounds().getWidth() / map.getPixelWidth(),frame.getBounds().getHeight()/2*frame.getBounds().getHeight() / map.getPixelHeight());
					map.getPlayer().setOrientation(VectorTools.getOrientationToPoint(playLoc,new Point2D.Double(mouseLoc.x*frame.getBounds().getWidth() / map.getPixelWidth(),mouseLoc.y*frame.getBounds().getHeight() / map.getPixelHeight())));
				}
				else {
					playLoc = new Point2D.Double(map.getPlayer().getLocation().x*HRZ_SCALE,map.getPlayer().getLocation().y*VERT_SCALE);
					map.getPlayer().setOrientation(VectorTools.getOrientationToPoint(playLoc,new Point2D.Double(mouseLoc.x*HRZ_SCALE,mouseLoc.y*VERT_SCALE)));
				}
		        if(holdCounter>=0) {
		        	holdCounter++;
		        	Weapon w = map.getPlayer().getCurrWeapon();
		        	if(holdCounter>15 && w != null && w.canShoot()) map.shoot(map.getPlayer());
		        }
	        }
	        else holdCounter = -1;
			int winner;
			if((winner = mode.getWinningTeam(statsFrame.getPlayers())) != -1) {
				mode.showGameEndDialog(map, winner);
				map.win();
				for(int i = 0; i < statsFrame.getPlayers().size(); i++) {
					if(statsFrame.getPlayers().get(i).getName().equals("Turret")) statsFrame.getPlayers().remove(i--);
				}
			}
	        map.gameUpdate();
	        statsFrame.updateStats(map.getPlayers());
	        // Refresh the display
	        frame.getCanvas().updateGraphics(map, mode);
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
		switch(e.getKeyCode()) {
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
		if(map.getPlayer()==null) {
			if(e.getKeyCode()==KeyEvent.VK_P) {
				if(map.isPaused()) map.unpause();
				else map.pause();
				new PauseFrame(this);
			}	
			return;
		}
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
			case KeyEvent.VK_ENTER:
				map.getPlayer().getCurrWeapon().reload();
				break;
			case KeyEvent.VK_R:
				map.getPlayer().getCurrWeapon().reload();
				break;
			case KeyEvent.VK_P:
				if(map.isPaused()) {
					pause.dispose();
					map.unpause();
				}
				else {
					map.pause();
					pause = new PauseFrame(this);
				}
				break;
			case KeyEvent.VK_T:
				if(map.getPlayer().getNumTurrets()>0) {
					map.getPlayer().setNumTurrets(map.getPlayer().getNumTurrets()-1);
					Player turret = new Player("Turret", map.getPlayers().size()+1);
					turret.respawn(new Point2D.Double(map.getPlayer().getLocation().x+Math.round(Math.cos(map.getPlayer().getOrientation()+Math.PI/2))*Player.radius*3 , map.getPlayer().getLocation().y+Player.radius*3*Math.sin(map.getPlayer().getOrientation()+Math.PI/2)), mode);
					turret.setTeam(map.getPlayer().getTeam());
					map.getPlayers().add(turret);
					map.getPlayer().setDeployedTurret(true);
				}
				break;
			case KeyEvent.VK_F:
				if(map.getPlayer().getCurrWeapon().getClipCount()!=-1) {
					Weapon wep = map.getPlayer().dropWeapon(map.getPlayer().getCurrWeapon());
					Point point = GameMap.getGridPoint(new Point2D.Double(map.getPlayer().getLocation().x+Math.round(Math.cos(map.getPlayer().getOrientation()+Math.PI/2))*Player.radius*3 , map.getPlayer().getLocation().y+Player.radius*3*Math.sin(map.getPlayer().getOrientation()+Math.PI/2)));
					if(map.getMap()[point.x][point.y]=='_') {
						map.getMap()[point.x][point.y] = wep.getCharacter();
						map.getDroppedWeps().add(new Point2D.Double(point.x, point.y));
						new WeaponRemoverThread(map.getPlayer().getCurrWeapon(), new Point2D.Double(map.getPlayerGridX(map.getPlayer()),map.getPlayerGridY(map.getPlayer())), map).start();
					}
				}
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
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON2)
			System.exit(0);
		if(map == null)
			return;
		if(map.getPlayer()==null) {
			holdCounter = -1;
			return;
		}
		holdCounter++;
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		if(map==null) return;
		if(map.getPlayer()==null || map.getPlayer().getCurrWeapon() == null) return;
		if(map.getPlayer().getCurrWeapon().canShoot()) map.shoot(map.getPlayer());
		holdCounter=-1;
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		if(map.getPlayer()==null) return;
//		map.getPlayer().setOrientation(Math.atan2((e.getY()-map.getPlayer().getLocation().y), (e.getX()-map.getPlayer().getLocation().x))-Math.PI/2);
		mouseLoc=new Point(e.getX(),e.getY());
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		if(map.getPlayer()==null) return;
		mouseLoc=new Point(e.getX(),e.getY());
//		map.getPlayer().setOrientation(Math.atan2((e.getY()-map.getPlayer().getLocation().y), (e.getX()-map.getPlayer().getLocation().x))-Math.PI/2);
	}
	@Override
	public void componentHidden(ComponentEvent arg0) {
		statsFrame.setVisible(false);
	}
	@Override
	public void componentMoved(ComponentEvent arg0) {
		Rectangle frameRect = frame.getBounds();
		statsFrame.setBounds(new Rectangle(frameRect.x-StatsFrame.WIDTH, frameRect.y+25, StatsFrame.WIDTH, StatsFrame.HEIGHT));
	}
	@Override
	public void componentResized(ComponentEvent arg0) {
		Rectangle frameRect = frame.getBounds();
		statsFrame.setBounds(new Rectangle(frameRect.x-StatsFrame.WIDTH, frameRect.y+25, StatsFrame.WIDTH, StatsFrame.HEIGHT));
	}
	@Override
	public void componentShown(ComponentEvent arg0) {
		Rectangle frameRect = frame.getBounds();
		statsFrame.setBounds(new Rectangle(frameRect.x-StatsFrame.WIDTH, frameRect.y+25, StatsFrame.WIDTH, StatsFrame.HEIGHT));
		statsFrame.setVisible(true);
	}
	
	public static void main(String[] args) {
		new Shoot();
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		statsFrame.setVisible(true);
		frame.setVisible(true);
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		
		
	}
	
	public void resumeGame() {
		map.unpause();
	}
	public GameFrame getFrame() {
		return frame;
	}
	public StatsFrame getStatsFrame() {
		return statsFrame;
	}
	public void close() {
		stop = true;
	}

	public long getID() {
		return _ID;
	}
	public void setID(long _ID) {
		this._ID = _ID;
		for (Player p : getMap().getPlayers()){
			p.setGAME_ID(_ID);
		}
	}
	public boolean isFull(){
		return false;
		//TODO
	}
	public boolean allowsPlayerTakeover(){
		return true;
		//TODO
	}
	public boolean allowsTeamTakeover(){
		return true;
	}

	public GameMap getMap() {
		return map;
	}
	public String getMapName() {
		return mapName;
	}
	public GameMode getMode() {
		return mode;
	}
	public int getNumTeams() {
		return numTeams;
	}
	public int getNumPlayersPerTeam() {
		return numPlayersPerTeam;
	}
	public String getWeaponSet() {
		return weaponSet;
	}
}
