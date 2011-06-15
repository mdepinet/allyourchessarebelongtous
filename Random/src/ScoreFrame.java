import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;

public class ScoreFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private Score highScore;
	private JTextField textField;
	private JLabel hideLabel;
	private int scorePosition;
	
	public ScoreFrame(Score score)
	{
		super("High Scores");
		highScore = score;
		Toolkit toolkit = Toolkit.getDefaultToolkit();  
		Dimension screenSize = toolkit.getScreenSize();
		setBounds(new Rectangle((int)screenSize.getWidth()/2 - Global.SCORE_WIDTH, (int)screenSize.getHeight()/2 - Global.SCORE_HEIGHT, Global.SCORE_WIDTH,Global.SCORE_HEIGHT));
		setResizable(false);
		getContentPane().setLayout(null);
		generateScoreLabels();
		setVisible(true);
	}
	public void generateScoreLabels()
	{
		int yLoc = 10;
		int num = 1;
		JLabel label = null;
		Global.highScoreManager.addScore(highScore);
		ArrayList<Score> list = Global.highScoreManager.getScores();
		Collections.sort(list);
		for(int i = 0; i < list.size();i++)
		{
			if(list.get(i).getPlayer()==null || list.get(i).getPlayer().equals("*"))
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
	public void actionPerformed(ActionEvent e) {
		if(textField.getText()!=null && textField.getText()!="")
		{
			highScore.setPlayer(textField.getText() + (highScore.cheat ? "*" : ""));
			Global.highScoreManager.writeScoresToFile();
			JLabel label = new JLabel(scorePosition + ". ", SwingConstants.RIGHT);
			label.setBounds(5, (int)textField.getBounds().getMinY(), 25, 20);
			label.setFont(label.getFont().deriveFont((float)15));
			add(label);
			label = new JLabel(highScore.getPlayer());
			label.setBounds(30, (int)textField.getBounds().getMinY(), 100, 20);
			label.setFont(label.getFont().deriveFont((float)15));
//			if(Global.score.isDidCheat())
//				label.setText(label.getText()+" *");
			add(label);
			textField.setVisible(false);
			hideLabel.setVisible(false);
		}
	}
}
