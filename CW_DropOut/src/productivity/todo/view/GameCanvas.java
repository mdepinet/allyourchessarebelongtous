package productivity.todo.view;

import java.awt.*;
import java.util.Set;

import productivity.todo.model.GameMap;
import productivity.todo.model.Player;

public class GameCanvas extends Canvas {
	private GameMap gameMap;
	public GameCanvas()
	{
		setBackground (Color.WHITE);
	}
	public void paint (Graphics g) {
		Graphics2D g2;
		g2 = (Graphics2D) g;
		g2.setColor(Color.BLACK);
		for(Player p: gameMap.getPlayers())
			g2.fillOval((int)p.getLocation().getX()-8,(int)p.getLocation().getY()-8, 16, 16);
		g2.fillOval((int)gameMap.getPlayer().getLocation().getX()-8,(int)gameMap.getPlayer().getLocation().getY()-8, 16, 16);
	}
	public GameMap getGameMap() {
		return gameMap;
	}
	public void setGameMap(GameMap gameMap) {
		this.gameMap = gameMap;
	}
}
