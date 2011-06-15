import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class CheatFrame extends JFrame implements ActionListener, KeyListener {
	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JTextArea textArea;
	private int selectedCheat = 0;
	private Player player;
	private JumpCanvas jc;
	private ArrayList<String> entries;
	private int upCount = -1;
	
	private static final int RESET_BOOSTS = 101;
	private static final int FREE_BOOST = 102;
	private static final int TAKE_HIGHEST_SCORE = 103;
	private static final int LOW_GRAVITY = 104;
	private static final int RESET_SCORES = 105;
	private static final int EXIT = 100;
	private static final int GET_NUM_BOOSTS = 106;
	private static final String RESET_BOOSTS_CODE = "moreBoosts!";
	private static final String FREE_BOOST_CODE = "freeBoost!";
	private static final String TAKE_HIGHEST_SCORE_CODE = "allyourbasearebelongtous";
	private static final String LOW_GRAVITY_CODE = "lowGravy!";
	private static final String RESET_SCORES_CODE = "resetHighScoresPlease!:)";
	private static final String EXIT_CODE = "exit";
	private static final String GET_NUM_BOOSTS_CODE = "getNumBoostsRemaining()";
	
	public CheatFrame(Player p, JumpCanvas jc)
	{
		super("Cheat Window");
		Toolkit toolkit = Toolkit.getDefaultToolkit();  
		Dimension screenSize = toolkit.getScreenSize();
		setBounds(new Rectangle((int)screenSize.getWidth()/2 - Global.CHEAT_WIDTH, (int)screenSize.getHeight()/2 - Global.CHEAT_HEIGHT, Global.CHEAT_WIDTH,Global.CHEAT_HEIGHT));
		setResizable(true);
		textField = new JTextField();
		textArea = new JTextArea();
		textArea.setEditable(false);
		//textArea.setPreferredSize(new Dimension(Global.CHEAT_WIDTH-30,Global.CHEAT_HEIGHT-65));
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(Global.CHEAT_WIDTH-30,Global.CHEAT_HEIGHT-65));
		getContentPane().setLayout(new BorderLayout());
		add(textField,BorderLayout.NORTH);
		add(scrollPane,BorderLayout.SOUTH);
		textField.addActionListener(this);
		textField.addKeyListener(this);
		setVisible(true);
		player = p;
		this.jc = jc;
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new ListenClose());
		entries = new ArrayList<String>();
	}
	public int getSelectedCheat(){return selectedCheat;}
	public void setSelectedCheat(int sc){selectedCheat = sc;}
/*	public void generateScoreLabels()
	{
		int yLoc = 10;
		int num = 1;
		JLabel label = null;
		Global.highScoreManager.addScore(highScore);
		ArrayList<Score> list = Global.highScoreManager.getScores();
		Collections.sort(list);
		for(int i = 0; i < list.size();i++)
		{
			if(list.get(i).getPlayer()==null)
			{
				hideLabel = new JLabel(num + ". ", SwingConstants.RIGHT);
				scorePosition = num;
				hideLabel.setBounds(5, yLoc, 25, 20);
				hideLabel.setFont(hideLabel.getFont().deriveFont((float)15));
				add(hideLabel);
				textField = new JTextField();
				textField.addActionListener(this);
				textField.setBounds(30, yLoc, 150, 20);
				add(textField);
			}
			else {
				label = new JLabel(num + ". ", SwingConstants.RIGHT);
				label.setBounds(5, yLoc, 25, 20);
				label.setFont(label.getFont().deriveFont((float)15));
				add(label);
				label = new JLabel(list.get(i).getPlayer());
				label.setBounds(30, yLoc, 100, 20);
				label.setFont(label.getFont().deriveFont((float)15));
				add(label);
			}
			label = new JLabel("" + list.get(i).getScore(), SwingConstants.RIGHT);
			label.setBounds(180, yLoc, 100, 20);
			label.setFont(label.getFont().deriveFont((float)15));
			add(label);
			num++;
			yLoc+=25;
		}
	}
*/
	public void actionPerformed(ActionEvent e) {
		if(textField.getText()!=null && textField.getText()!="")
		{
			String s = textField.getText();
			upCount = -1;
			entries.add(0,s);
			if (s.equals(LOW_GRAVITY_CODE)){
				selectedCheat = LOW_GRAVITY;
			}
			else if (s.equals(FREE_BOOST_CODE)){
				selectedCheat = FREE_BOOST;
			}
			else if (s.equals(RESET_BOOSTS_CODE)){
				selectedCheat = RESET_BOOSTS;
			}
			else if (s.equals(TAKE_HIGHEST_SCORE_CODE)){
				selectedCheat = TAKE_HIGHEST_SCORE;
			}
			else if (s.equals(RESET_SCORES_CODE)){
				selectedCheat = RESET_SCORES;
			}
			else if (s.equals(EXIT_CODE)){
				selectedCheat = EXIT;
			}
			else if(s.equals(GET_NUM_BOOSTS_CODE)) {
				selectedCheat = GET_NUM_BOOSTS;
			}
			else selectedCheat = 0;
		}
		processCheat();
	}
	public void processCheat(){
		String cheatResult = "Nothing happened...";
		switch(selectedCheat){
		default:
			break;
		case(LOW_GRAVITY):
			Global.GRAVITY -= 0.1;
			if(Global.GRAVITY <= 0) Global.GRAVITY = 0.5;
			cheatResult = "Gravity set to "+Global.GRAVITY;
			Global.score.setPlayer("*");
			Global.score.cheat = true;
			break;
		case(RESET_BOOSTS):
			player.setBoostCount(3);
			cheatResult = "Boost count reset";
			Global.score.setPlayer("*");
			Global.score.cheat = true;
			break;
		case(FREE_BOOST):
			player.freeBoost();
			cheatResult = "Boosted for free!";
			Global.score.setPlayer("*");
			Global.score.cheat = true;
			break;
		case(TAKE_HIGHEST_SCORE):
			Global.score = Global.highScoreManager.getScores().get(0);
			cheatResult = "Stole the high score!  How could you!?";
			Global.score.setPlayer("*");
			Global.score.cheat = true;
			break;
		case(RESET_SCORES):
			for (Score s : Global.highScoreManager.getScores()){
				s.setScore(300);
				s.setPlayer("");
			}
			Global.highScoreManager.writeScoresToFile();
			cheatResult = "Reset High Scores...";
			break;
		case(GET_NUM_BOOSTS):
			cheatResult = "Boosts remaining : " + player.getBoostCount();
			break;
		case(EXIT):
			dispose();
			break;
		}
		textArea.setText(textField.getText()+" === "+cheatResult+"\n" + textArea.getText());
		textField.setText("");
	}
	public JumpCanvas getJc(){return jc;}
	
	private boolean keyDown;
	public void keyPressed(KeyEvent e) {
		if (!keyDown && e.getKeyCode()==KeyEvent.VK_UP){
			processEntries(++upCount);
		}
		else if (!keyDown && e.getKeyCode()==KeyEvent.VK_DOWN){
			processEntries(--upCount);
		}
		keyDown = true;
	}
	public void keyReleased(KeyEvent e) {
		keyDown = false;
	}
	public void keyTyped(KeyEvent e) {
	}
	private void processEntries(int count){
		if (count >= 0 && count < entries.size()){
			textField.setText(entries.get(count));
		}
	}
}

class ListenClose extends WindowAdapter{
	public void windowClosing(WindowEvent e){
		((CheatFrame)e.getWindow()).getJc().setCheatOpen(false);
		e.getWindow().dispose();
	}
}
