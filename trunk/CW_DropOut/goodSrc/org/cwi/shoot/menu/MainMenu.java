package org.cwi.shoot.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cwi.shoot.control.Shoot;
import org.cwi.shoot.profile.Profile;
import org.cwi.shoot.view.OptionsFrame;

public class MainMenu extends JFrame implements ActionListener, ListSelectionListener {
	private static final long serialVersionUID = 1L;
	public static final String IMG_LOC = "resource/images/";
	private ArrayList<JButton> buttonGroup;
	private Shoot control;
	private JPanel buttonPanel;
	private JPanel panel;
	private Profile profile;
	
	public MainMenu(Shoot control, Profile prof) {
		super("Shoot");
		
		this.control = control;
		
		if(prof==null) profile = new Profile(OptionsFrame.getProfileNames().size()==0 ? "" : OptionsFrame.getProfileNames().get(0).substring(0,OptionsFrame.getProfileNames().get(0).indexOf(".pprf")));
		else profile = prof;
		
		setBounds(new Rectangle(800,625));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel cPane = new JPanel(new BorderLayout());
		cPane.setBorder(new LineBorder(Color.BLACK, 5));
		setContentPane(cPane);
		
		
		BackGPanel backgPanel = new BackGPanel();
		
		BackGPanel optionsPanel = new BackGPanel();
		optionsPanel.setPreferredSize(new Dimension(800,470));
		optionsPanel.setLayout(new FlowLayout());
		optionsPanel.setOpaque(true);
		backgPanel.add(optionsPanel, BorderLayout.NORTH);

		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.setBackground(Color.WHITE);
		buttonGroup = new ArrayList<JButton>();
		panel = new JPanel(new FlowLayout());
		buttonPanel = new JPanel(new FlowLayout());
		JButton button;
		for(int i = 1; i < 5;i++) {
			String text = "";
			if(i==1) text = "SOLO";
			if(i==2) text = "MULTIPLAYER";
			if(i==3) text = "PROFILE MANAGEMENT";
			if(i==4) text = "EXIT";
			button = new JButton(text, new ImageIcon(IMG_LOC + "MenuButton" + ".png"));
			button.setVerticalTextPosition(JButton.BOTTOM);
			button.setHorizontalTextPosition(JButton.CENTER);
			button.setPreferredSize(new Dimension(185,100));
			button.setForeground(Color.BLACK);
			button.setBackground(Color.WHITE);
			button.setActionCommand("option" + i);
			button.addActionListener(this);
			button.setOpaque(false);
			panel.add(button);
			buttonGroup.add(button);
		}
		buttonPanel.setOpaque(false);
		panel.add(buttonPanel);
		panel.setOpaque(false);
		southPanel.add(panel, BorderLayout.CENTER);
		JLabel label = new JLabel("Welcome" + (profile.getRankAndName().equals("") || profile == null ? ". Please create a profile before playing." : " " + profile.getRankAndName()));
		southPanel.add(label, BorderLayout.NORTH);
		backgPanel.add(southPanel, BorderLayout.SOUTH);
		
		getContentPane().add(backgPanel);
		getContentPane().setBackground(Color.WHITE);
		
		setLocationRelativeTo(getRootPane());
		setUndecorated(true);
		setVisible(true);
	}
	
	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	
	private class BackGPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		public BackGPanel() {
		}
		
		public void paintComponent(Graphics g) {
			BufferedImage backgImg = null;
			try {
				backgImg = ImageIO.read(new File(IMG_LOC + "MainMenu.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			g.drawImage(backgImg, 0, 0, this);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("option1")) {
			if(profile.equals("") || profile==null) {
				JOptionPane.showMessageDialog(buttonGroup.get(0), "You must create a profile before playing.\nYou may do so in 'PROFILE MANAGEMENT'.", "No Profile found", 0);
				return;
			}
			new GameSetupFrame2(control, profile);
			this.dispose();
		}
		else if(e.getActionCommand().equals("option3")) {
			new OptionsFrame(control);
			this.dispose();
		}
		if(e.getActionCommand().equals("option4")) {
			this.dispose();
			System.exit(0);
		}
	}
}
