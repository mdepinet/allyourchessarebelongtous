package org.cwi.shoot.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.cwi.shoot.map.GameMap;


public class GameFrame extends JFrame {
	private static final long serialVersionUID = -6499907386086607974L;
	public static final int WIDTH = 750, HEIGHT = 750;
	private GameCanvas canvas;

	public GameFrame(String s, GameMap map)
	{
		super(s);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds(250,200,WIDTH+6,HEIGHT+28);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel cPane = new JPanel(new BorderLayout());
		cPane.setBorder(new LineBorder(Color.BLACK, 5));
		setContentPane(cPane);
		this.setResizable(false);
		canvas = new GameCanvas();
		getContentPane().add(canvas);
		setLocationRelativeTo(getRootPane());
		setUndecorated(true);
		this.setVisible(true);
		canvas.init();
	}
	public GameCanvas getCanvas(){
		return canvas;
	}
}
