import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.Timer;



public class Jump implements ActionListener, KeyListener {
	
	private JumpCanvas canvas;
	private int keyPressed;
	public Jump()
	{
		JFrame frame = new JFrame("Jump");
		
		JMenuBar menu = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem scores = new JMenuItem("View High Scores");
		menu.add(file);
		file.add(scores);
		frame.setJMenuBar(menu);
		scores.addActionListener(new ListenScores());
		Global.score = new Score(0);
		Global.highScoreManager = new HighScoreManager();
		Toolkit toolkit = Toolkit.getDefaultToolkit();  
		Dimension screenSize = toolkit.getScreenSize();
		frame.setBounds(new Rectangle((int)screenSize.getWidth()/2 - Global.GAME_WIDTH, (int)screenSize.getHeight()/2 - Global.GAME_HEIGHT, Global.GAME_WIDTH,Global.GAME_HEIGHT+20));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		canvas = new JumpCanvas();
		frame.add(canvas);
		frame.pack();
		frame.addKeyListener(this);
		Timer timer = new Timer(30, this);
		frame.setVisible(true);
		timer.start();
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_LEFT)
		{
			keyPressed = 1;
		}
		if(e.getKeyCode()==KeyEvent.VK_RIGHT)
		{
			keyPressed = 2;
		}
		if(e.getKeyCode()==KeyEvent.VK_UP){
			keyPressed = 3;
		}
		if(e.getKeyCode()==KeyEvent.VK_0) keyPressed = 4;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keyPressed = 0;
	}

	public void keyTyped(KeyEvent e) {}

	public void actionPerformed(ActionEvent e)
	{
		switch(keyPressed)
		{
			case 1:
				canvas.moveLeft();
				break;
			case 2:
				canvas.moveRight();
				break;
			case 3:
				canvas.boost();
				break;
			case 4:
				canvas.openCheatWindow();
			default:
				break;
		}
		canvas.updateGame();
	}
	
	public static void main(String[] args)
	{
		new Jump();
	}
}

class ListenScores implements ActionListener{
	public void actionPerformed(ActionEvent e) {
		new ScoreFrame(new Score(0));
	}
}
