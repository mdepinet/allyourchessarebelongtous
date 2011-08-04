package org.cwi.shoot.menu;

import java.awt.BorderLayout;
import java.awt.Button;
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
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cwi.shoot.control.Shoot;

public class MainMenu extends JFrame implements ActionListener, ListSelectionListener {
	public static final String IMG_LOC = "resource/images/";
	private ArrayList<JButton> buttonGroup;
	private Shoot control;
	
	public MainMenu(Shoot control) {
		super("Shoot");
		
		this.control = control;

		setBounds(new Rectangle(400,300,800,600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		
		JPanel optionsPanel = new JPanel();
		optionsPanel.setPreferredSize(new Dimension(400,450));
		optionsPanel.setLayout(new FlowLayout());
		getContentPane().add(optionsPanel, BorderLayout.NORTH);

		buttonGroup = new ArrayList<JButton>();
		JPanel panel = new JPanel(new FlowLayout());
		JPanel buttonPanel = new JPanel(new FlowLayout());
		JButton button;
		for(int i = 1; i < 4;i++) {
			String text = "";
			if(i==1) text = "SOLO";
			if(i==2) text = "MULTI";
			if(i==3) text = "OPTIONS";
			button = new JButton(text, new ImageIcon(IMG_LOC + "MenuButton" + ".png"));
			button.setVerticalTextPosition(JButton.BOTTOM);
			button.setHorizontalTextPosition(JButton.CENTER);
			button.setPreferredSize(new Dimension(185,100));
			button.setForeground((i==1) ? Color.BLACK : Color.GRAY);
			button.setBackground((i==1) ? Color.GREEN : Color.BLUE);
			button.setActionCommand("option" + i);
			button.addActionListener(this);
			button.setOpaque(false);
			panel.add(button);
			buttonGroup.add(button);
		}
		buttonPanel.setOpaque(false);
		panel.add(buttonPanel);
		panel.setOpaque(false);
		getContentPane().add(panel, BorderLayout.CENTER);
		
		setVisible(true);
	}
	
	public void paint(Graphics g) {
		BufferedImage backgImg = null;
		try {
			backgImg = ImageIO.read(new File(IMG_LOC + "MainMenu.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		g.drawImage(backgImg, 0, 0, this);
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getActionCommand().equals("option1")) {
			GameSetup setup = new GameSetup(control);
			this.dispose();
		}
	}
}
