import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JPanel;

public class JumpCanvas extends JPanel {

	private static final long serialVersionUID = 1L;
	private Player player;
	private ArrayList<Platform> platforms;
	private boolean cheatsOpen = false;
	
	public JumpCanvas()
	{
		setBackground(Color.blue);
		setPreferredSize(new Dimension(Global.GAME_WIDTH,Global.GAME_HEIGHT));
		platforms = new ArrayList<Platform>();
		player = new Player();
		platforms.add(new Platform(new Point2D.Double(player.getLocation().getX(), 10), 0));
		updatePlatforms();
	}
	public void setCheatOpen(boolean b){
		cheatsOpen = b;
	}
	
	public void updatePlatforms()
	{
		while(platforms.size()<12)
		{
			int q = (int)(Math.random()*Math.min((Global.score.getScore()/1000)+1,25));
			platforms.add(new Platform(new Point2D.Double(Math.random()*360 + 20,platforms.get(platforms.size()-1).getLocation().getY()+40), q));
		}
		
	}
	
	public void updateGame()
	{
		player.update();
		if(player.getLocation().getY()<=10)
		{
			if(Global.score.getScore()>0 && Global.highScoreManager.isHighScore(Global.score))
				new ScoreFrame(Global.score);
//			Global.score.setScore(0);
			Global.score = new Score(0);
			for (Platform p : platforms){
				p.setColor(Color.green);
			}
			player.setBoostCount(3);
		}
		for(int i = 0; i < platforms.size();i++)
		{
			if(player.getLocation().getY()>170 && player.getVelocity().getY()>0)
				platforms.get(i).update(player.getVelocity().getY());
			if(!platforms.get(i).isOnScreen())
				platforms.remove(i--);
			if(player.getVelocity().getY()<0 && platforms.get(i).collidesWith(player))
				player.bounce(platforms.get(i));
		}
		if(player.getLocation().getY()>170 && player.getVelocity().getY()>0)
			Global.score.setScore(Global.score.getScore() + (int)player.getVelocity().getY());
		updatePlatforms();
		repaint();
	}
	
	public void moveLeft()
	{
		player.moveLeft();
	}
	public void moveRight()
	{
		player.moveRight();
	}
	public void boost(){
		player.boost();
	}
	public void openCheatWindow(){
		if (!cheatsOpen) new CheatFrame(player,this);
		cheatsOpen = true;
	}
	
	public void paint(Graphics g)
	{
		g.fillRect(0, 0, Global.GAME_WIDTH, Global.GAME_HEIGHT);
		g.setFont(g.getFont().deriveFont((float)30.0));
		for(Platform p: platforms)
			p.draw(g);
		g.setColor(Color.white);
		g.drawString("" + Global.score.getScore(), 0, 30);
		String vel = "" + Math.sqrt(Math.pow(player.getVelY(),2)+Math.pow(player.getVelX(),2));
		g.drawString(vel.substring(0,vel.indexOf(".")), Global.GAME_WIDTH-60,30);
		player.draw(g);
	}
}
