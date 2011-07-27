package org.cwi.shoot.view;

import javax.swing.JFrame;

import org.cwi.shoot.map.GameMap;


public class GameFrame extends JFrame {
	private static final long serialVersionUID = -6499907386086607974L;
	public static final int WIDTH = 750, HEIGHT = 750;
	private GameCanvas canvas;

	public GameFrame(String s, GameMap map)
	{
		super(s);
		this.setBounds(250,200,WIDTH+6,HEIGHT+28);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		canvas = new GameCanvas();
		getContentPane().add(canvas);
		this.setVisible(true);
		canvas.init();
	}
	public GameCanvas getCanvas(){
		return canvas;
	}
}
