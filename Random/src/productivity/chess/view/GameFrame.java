package productivity.chess.view;

import java.awt.event.MouseListener;

import javax.swing.JFrame;

import productivity.chess.model.GameBoard;

public class GameFrame extends JFrame {

	private static final long serialVersionUID = -6499907386086607974L;
	private GameCanvas canvas;

	public GameFrame(String s, GameBoard board, MouseListener ml)
	{
		super(s);
		this.setBounds(300,300,340,360);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		canvas = new GameCanvas();
		canvas.addMouseListener(ml);
		canvas.setBoard(board);
		getContentPane().add(canvas);
		this.setVisible(true);
	}
	
	public GameCanvas getCanvas(){
		return canvas;
	}
}
