package org.cwi.shoot.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.cwi.shoot.config.GameMode;
import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Player.PlayerType;
import org.cwi.shoot.model.Weapon;
import org.cwi.shoot.model.Weapon.WeaponType;


public class StatsFrame extends JFrame {
	private static final long serialVersionUID = -6714533167738097176L;
	public static final int WIDTH = 200;
	public static final int HEIGHT = 500;
	private GameMode gameMode;
	private PlayerInfoCanvas playerInfoCanvas;
	private List<Player> players;
	private List<JLabel> labels;
	private char[] teams;
	public StatsFrame(GameMode mode, List<Player> p, char[] t) {
		this.setBounds(new Rectangle(300,300,WIDTH,HEIGHT));
		this.setUndecorated(true);
		this.setFocusable(false);
		this.setBackground(Color.WHITE);
		this.setVisible(true);
		this.playerInfoCanvas = new PlayerInfoCanvas(p.get(0));
		playerInfoCanvas.setPreferredSize(new Dimension(180,60));
		getContentPane().setLayout(new BorderLayout());
		Container north = new Container(); north.setLayout(new FlowLayout());
		Container center = new Container(); center.setLayout(new FlowLayout());
		Container south = new Container(); south.setLayout(new FlowLayout());
		gameMode = mode;
		labels = new ArrayList<JLabel>();
		players = new ArrayList<Player>();
		players.addAll(p);
		teams = t;
		JLabel label;
		for(int i = 0; i < teams.length;i++)
		{
			Weapon w = new Weapon(teams[i], new Point());
			label = new JLabel("0", new ImageIcon(Weapon.getWeaponImg(w.getImgLoc())), JLabel.CENTER);
			label.setPreferredSize(new Dimension(100,25));
			north.add(label);
			labels.add(label);
		}
		north.setPreferredSize(new Dimension(200, 30*teams.length));
		getContentPane().add(north, BorderLayout.NORTH);
		for(Player x: players) {
			label = new JLabel(x.getName() + ": " + x.getStats().getNumKills());
			label.setPreferredSize(new Dimension(200, 20));
			center.add(label);
			labels.add(label);
		}
		getContentPane().add(center, BorderLayout.CENTER);
		south.add(playerInfoCanvas);
		getContentPane().add(south, BorderLayout.SOUTH);
		playerInfoCanvas.init();
	}
	public Player getPlayer() {
		for(int i = 0; i < players.size(); i++)
			if(players.get(i).getType() == PlayerType.HUMAN) 
				return players.get(i);
		return null;
	}
	public void updatePlayer(Player p)
	{
		for(int i = 0; i < players.size();i++)
			if(players.get(i).getName().equals(p.getName()))
				players.set(i, p);
	}
	public void updateStats(List<Player> p) {
		for(int i = 0; i<p.size(); i++) {
			Player x = p.get(i);
			updatePlayer(x);
		}
		Collections.sort(players);
		for(int i = 0; i < teams.length;i++) {
			labels.get(i).setText(gameMode.getScoreForTeam(teams[i]-75, players));
		}
		for(int i = 0; i < players.size();i++) labels.get(i+teams.length).setText(gameMode.getScoreForPlayer(players.get(i)));
		
		if(players.get(0).getType()==PlayerType.HUMAN) playerInfoCanvas.setPlayer(players.get(0));
		playerInfoCanvas.updateGraphics();
	}
	
	class PlayerInfoCanvas extends Canvas {
		private static final long serialVersionUID = 1564713213101913746L;
		private Image backbuffer;
		private Graphics2D backg;
		private Player player;
		
		public PlayerInfoCanvas(Player p) {
			this.player = p;
		}
		public void setPlayer(Player p) {
			this.player = p;
		}
		public void init() {
			backbuffer = createImage( GameFrame.WIDTH, GameFrame.HEIGHT );
		    backg = (Graphics2D)backbuffer.getGraphics();
		    backg.setBackground( Color.white );
		    backg.clearRect(0, 0, WIDTH, HEIGHT);
		    backg.setFont(backg.getFont().deriveFont(GameMap.GRID_PIXELS));
		}
		
		public void updateGraphics() {
			if (player == null) player = getPlayer();
			backg.setBackground(new Color(1f,1f,1f,1f));
			backg.clearRect(0,0,getWidth(), getHeight());
			backg.setColor(new Color(0f,0f,0f,1f));
			backg.setStroke(new BasicStroke(3f));
			backg.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 20, 20);
			if(player!=null && player.getCurrWeapon()!=null)
			{
				backg.setColor(new Color((player.getHealth()>50) ? (float)(1-player.getHealth()/100) : 1.0f,(player.getHealth()<=50) ? (float)(player.getHealth()/50):1.0f,0f,1f));
				backg.fillRect(getWidth()-108, getHeight()-40, (int)player.getHealth(), 10);
				backg.setColor(new Color(0f,0f,0f,1f));
				backg.drawString("Health:", getWidth()-150, getHeight()-31);
				backg.drawString(player.getCurrWeapon().getName(), getWidth()-120, getHeight()-43);
				if(player.getCurrWeapon().getClipCount()>=0)
					backg.drawString(""+player.getCurrWeapon().getClipCount(), getWidth()-25, getHeight()-43);
				if(player.getCurrWeapon().getClipSize()==0 && player.getCurrWeapon().getEffRange()>0)
					backg.drawString("Reloading...", getWidth()-105, getHeight()-15);
				else{
					int clipSize = player.getCurrWeapon().getClipSize()*((player.getCurrWeapon().getTypes().contains(WeaponType.SHOTGUN))? 1 : player.getCurrWeapon().getRoundsPerShot());
					for(int i=0; i < clipSize;i++)
					{	
						if(i<20 || i<clipSize/2)
							backg.fillRect(getWidth()-12-(i*6),getHeight()-28, 4, 10);
						else
							backg.fillRect(getWidth()-12-((i-Math.max(20, clipSize/2)))*6,getHeight()-16, 4, 10);
					}
				}
			}
			repaint();
		}
		public void update(Graphics g) {
			g.drawImage(backbuffer,0,0,this);
		}
		public void paint (Graphics g) {
			update(g);
		}
	}
}
