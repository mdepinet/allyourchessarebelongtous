package org.cwi.shoot.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.cwi.shoot.map.GameMap;


public class GameFrame extends JFrame {
	private static final long serialVersionUID = -6499907386086607974L;
	public static int WIDTH = 750, HEIGHT = 750;
	public static final int MIN_WIDTH = 300, MIN_HEIGHT = 300;
	private GameCanvas canvas;

	public GameFrame(String s, GameMap map, int width, int height)
	{
		super(s);
		if(width == -1 && height == -1)	this.setBounds(250,200,WIDTH+10,HEIGHT+10);
		else this.setBounds(new Rectangle(width+10,height+10));
		WIDTH = map.getMap()[0].length * GameMap.GRID_PIXELS;
		HEIGHT = map.getMap().length * GameMap.GRID_PIXELS;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel cPane = new JPanel(new BorderLayout());
		cPane.setBorder(new LineBorder(Color.BLACK, 5));
		setContentPane(cPane);
		this.setResizable(false);
		canvas = new GameCanvas(this.getBounds());
		getContentPane().add(canvas);
		setLocationRelativeTo(getRootPane());
		setUndecorated(true);
		this.setVisible(true);
		canvas.init();
	}
	public GameCanvas getCanvas(){
		return canvas;
	}
	public boolean isSmallerScreen() {
		return getBounds().getWidth() < WIDTH || getBounds().getHeight() < HEIGHT;
	}
}
