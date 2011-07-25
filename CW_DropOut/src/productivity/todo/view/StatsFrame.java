package productivity.todo.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import productivity.todo.model.Player;
import productivity.todo.model.Weapon;

public class StatsFrame extends JFrame {
	public static final int WIDTH = 200;
	public static final int HEIGHT = 500;
	private List<Player> players;
	private List<JLabel> labels;
	private char[] teams;
	public StatsFrame(List<Player> p, char[] t) {
		this.setBounds(new Rectangle(300,300,WIDTH,HEIGHT));
		this.setUndecorated(true);
		this.setBackground(Color.WHITE);
		this.setVisible(true);
		getContentPane().setLayout(new FlowLayout());
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
		
		for(Player x: players) {
			label = new JLabel(x.getName() + ": " + x.getStats().getNumKills());
			label.setPreferredSize(new Dimension(200, 30));
			getContentPane().add(label);
			labels.add(label);
		}
		
	}
	public void updatePlayer(Player p)
	{
		for(int i = 0; i < players.size();i++)
			if(players.get(i).getName().equals(p.getName()))
				players.set(i, p);
	}
	public void updateStats(List<Player> p) {
		for(Player x:p)
			updatePlayer(x);
		Collections.sort(players);
		for(int i = 0; i < teams.length;i++) {
			labels.get(i).setText(""+getTeamKills(teams[i]-75));
		}
		for(int i = 0; i < players.size();i++)
			labels.get(i+teams.length).setText(players.get(i).getName() + ": " + players.get(i).getStats().getNumKills());
			
	}
	
	public int getTeamKills(int team) {
		int kills = 0;
		for(Player p : players) {
			if(p.getTeam()==team)
				kills += p.getStats().getNumKills();
		}
		return kills;
	}
}
