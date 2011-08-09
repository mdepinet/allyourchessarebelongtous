package org.cwi.shoot.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cwi.shoot.control.Shoot;
import org.cwi.shoot.profile.Profile;

public class PauseFrame extends JFrame implements ActionListener {
	private Shoot control;
	private Rectangle screenDimensions;
	private Profile profile;
	
	public PauseFrame(Shoot control) {
		super("Pause");
		
		this.control = control;
		screenDimensions = control.getFrame().getBounds();
		
		setBounds(new Rectangle(200,200));
		
		JPanel labelPanel = new JPanel();
		JLabel label = new JLabel("PAUSED");
		labelPanel.add(label);
		getContentPane().add(labelPanel, BorderLayout.NORTH);
		control.getFrame().setFocusable(false);
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setPreferredSize(new Dimension(25,25));
		JButton button = new JButton("Resume Game");
		button.addActionListener(this);
		button.setActionCommand("resume");
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(button);
		button = new JButton("Options");
		button.addActionListener(this);
		button.setActionCommand("options");
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(button);
		button = new JButton("Quit Game");
		button.addActionListener(this);
		button.setActionCommand("exit");
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(button);
		
		JPanel qPanel = new JPanel();
		button = new JButton("Quit Shoot");
		button.addActionListener(this);
		button.setActionCommand("quit");
		qPanel.add(button);
		
		getContentPane().add(panel, BorderLayout.CENTER);
		getContentPane().add(qPanel, BorderLayout.SOUTH);
		
		setLocationRelativeTo(getRootPane());
		setUndecorated(true);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("resume")) {
			if(profile!=null) control.resetFrame(profile);
			control.getFrame().setFocusable(true);
			control.resumeGame();
			this.dispose();
		}
		else if(e.getActionCommand().equals("options")) {
			new OptionsFrame(control, this);
		}
		else if(e.getActionCommand().equals("exit")) {
			control.getStatsFrame().dispose();
			control.getFrame().dispose();
			this.dispose();
			control.close();
			Shoot.main(null);
		}
		else if(e.getActionCommand().equals("quit")) {
			System.exit(0);
		}
	}
	public void changeProfSettings(Profile prof) {
		profile = prof;
	}
}
