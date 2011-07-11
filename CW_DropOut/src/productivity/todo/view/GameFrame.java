package productivity.todo.view;

import java.awt.event.MouseListener;

import javax.swing.JFrame;

import productivity.todo.model.GameMap;
import productivity.todo.view.GameCanvas;

public class GameFrame extends JFrame {

	private static final long serialVersionUID = -6499907386086607974L;
	private GameCanvas canvas;

	public GameFrame(String s, GameMap map)
	{
		super(s);
		this.setBounds(300,300,map.WIDTH,map.HEIGHT+28);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		canvas = new GameCanvas();
		canvas.setGameMap(map);
		getContentPane().add(canvas);
		this.setVisible(true);
	}
	public GameCanvas getCanvas(){
		return canvas;
	}
}
