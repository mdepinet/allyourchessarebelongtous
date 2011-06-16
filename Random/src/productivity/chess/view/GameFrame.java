package productivity.chess.view;

import javax.swing.JFrame;

public class GameFrame extends JFrame {
	public GameFrame(String s)
	{
		super(s);
		this.setBounds(300,300,340,360);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		getContentPane().add(new GameCanvas());
		this.setVisible(true);
	}
}
