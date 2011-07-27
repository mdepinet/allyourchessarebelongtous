package org.cwi.shoot.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.cwi.shoot.map.GameMap;

import productivity.todo.config.GameMode;
import productivity.todo.config.ZombiesWGuns;
import productivity.todo.model.Player;
import productivity.todo.model.PlayerType;
import productivity.todo.model.Weapon;

public class StatsFrame extends JFrame {
	public static final int WIDTH = 200;
	public static final int HEIGHT = 500;
	private GameMode gameMode;
	private List<Player> players;
	private List<JLabel> labels;
	private char[] teams;
	public StatsFrame(GameMode mode, List<Player> p, char[] t) {
		this.setBounds(new Rectangle(300,300,WIDTH,HEIGHT));
		this.setUndecorated(true);
		this.setBackground(Color.WHITE);
		this.setVisible(true);
		getContentPane().setLayout(new FlowLayout());
		gameMode = mode;
		labels = new ArrayList<JLabel>();
		players = new ArrayList<Player>();
		players.addAll(p);
		teams = t;
		JLabel label;
		for(int i = 0; i < teams.length;i++)
		{
			label = new JLabel("0", new ImageIcon(new Weapon(teams[i], new Point()).getImage()), JLabel.CENTER);
			label.setPreferredSize(new Dimension(100,40));
			getContentPane().add(label);
			labels.add(label);
		}
		if(!(gameMode instanceof ZombiesWGuns)) {
			for(Player x: players) {
				label = new JLabel(x.getName() + ": " + x.getStats().getNumKills());
				label.setPreferredSize(new Dimension(200, 30));
				getContentPane().add(label);
				labels.add(label);
			}
		}
		else {
			Player x = players.get(0);
			label = new JLabel(x.getName() + ": " + x.getStats().getNumKills());
			label.setPreferredSize(new Dimension(200, 30));
			getContentPane().add(label);
			labels.add(label);
		}
		
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
			labels.get(i).setText(gameMode.getScoreForTeam((teams[i]-75)));
		}
		if(gameMode instanceof ZombiesWGuns) labels.get(1).setText(gameMode.getScoreForPlayer(getPlayer()));
		else for(int i = 0; i < players.size();i++) labels.get(i+teams.length).setText(gameMode.getScoreForPlayer(players.get(i)));
			
	}
}


//Draw player info box
//backg.setColor(new Color(0f,0f,0f,0.3f));
//backg.fillRect(GameMap.WIDTH-200,GameMap.HEIGHT-50,200,50);
//backg.setColor(new Color(0f,0f,0f,0.5f));
//if(gameMap.getPlayer()!=null)
//{
//	backg.setColor(new Color((gameMap.getPlayer().getHealth()>50) ? (float)(1-gameMap.getPlayer().getHealth()/100) : 1.0f,(gameMap.getPlayer().getHealth()<=50) ? (float)(gameMap.getPlayer().getHealth()/50):1.0f,0f,0.5f));
//	backg.fillRect(GameMap.WIDTH-105, GameMap.HEIGHT-35, (int)gameMap.getPlayer().getHealth(), 10);
//	backg.setColor(new Color(0f,0f,0f,0.5f));
//	backg.drawString("Health:", GameMap.WIDTH-145, GameMap.HEIGHT-26);
//	backg.drawString(gameMap.getPlayer().getCurrentWeapon().getName(), GameMap.WIDTH-115, GameMap.HEIGHT-38);
//	if(gameMap.getPlayer().getCurrentWeapon().getClipCount()>=0)
//		backg.drawString(""+gameMap.getPlayer().getCurrentWeapon().getClipCount(), GameMap.WIDTH-20, GameMap.HEIGHT-38);
//	if(gameMap.getPlayer().getCurrentWeapon().getClipSize()==0 && !gameMap.getPlayer().getCurrentWeapon().getType().equalsIgnoreCase("melee"))
//		backg.drawString("Reloading...", GameMap.WIDTH-100, GameMap.HEIGHT-10);
//	else{
//		int clipSize = gameMap.getPlayer().getCurrentWeapon().getClipSize()*((gameMap.getPlayer().getCurrentWeapon().getType().equalsIgnoreCase("shotgun"))? 1 : gameMap.getPlayer().getCurrentWeapon().getRoundsPerShot());
//		for(int i=0; i < clipSize;i++)
//		{	
//			if(i<20 || i<clipSize/2)
//				backg.fillRect(GameMap.WIDTH-7-(i*6),GameMap.HEIGHT-23, 4, 10);
//			else
//				backg.fillRect(GameMap.WIDTH-7-((i-Math.max(20, clipSize/2)))*6,GameMap.HEIGHT-11, 4, 10);
//		}
//	}
//}
