package productivity.todo.model;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

public class GameMap{
	private Set<Player> players;
	private Player player;
	public static final int HEIGHT = 500;
	public static final int WIDTH = 500;
	
	public GameMap()
	{
		players = new HashSet<Player>();
		player = new Player();
	}
	public Set<Player> getPlayers() {
		return players;
	}

	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public void gameUpdate()
	{
		for(Player p: players)
			p.update();
		player.update();
	}
	
	public void setPlayers(Set<Player> players) {
		this.players = players;
	}
	
}
