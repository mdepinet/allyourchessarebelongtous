package productivity.todo.config;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Scanner;

import productivity.todo.ai.Objective;
import productivity.todo.model.GameMap;
import productivity.todo.model.Player;
import productivity.todo.model.Weapon;

public class CaptureTheFlagMode extends GameMode {

	int[] teamScores = new int[4];
	
	public CaptureTheFlagMode() {}
	
	public CaptureTheFlagMode(GameMap map) {
		super(map);
		
	}
	
	@Override
	public void loadGameObjects() {
		Scanner scan = null;
		try
		{
			scan = new Scanner(gameMap.getMapChosen());
		}
		catch(IOException e)
		{}
		for(int i = 0; i < gameMap.getMap().length;i++)
		{
			for(int j = 0; j < gameMap.getMap()[i].length && scan.hasNext() ; j++) {
				String next = scan.next();
				if(next.matches("[L-O]"))
					gameMap.getMap()[j][i] = next.charAt(0);
			}
			if(scan.hasNextLine())
				scan.nextLine();
		}
	}

	@Override
	public void update() {
		for(Player p: gameMap.getPlayers()) {
			if(!(p.getCurrentWeapon()!=null && p.getCurrentWeapon().getName().indexOf("Flag")!=-1))
			{
				if(getClosestOtherTeamFlagLocation(p)!=null)
					p.addObjective(new Objective(getClosestOtherTeamFlagLocation(p), 20., getCostForPath(gameMap.getGridPoint(p.getLocation()),getClosestOtherTeamFlagLocation(p))));
			}
			else
			{
				if(getTeamFlagLocation(p.getTeam())!=null)
					p.addObjective(new Objective(gameMap.getGridPoint(getTeamFlagLocation(p.getTeam())), 50., getCostForPath(gameMap.getGridPoint(p.getLocation()),gameMap.getGridPoint(getTeamFlagLocation(p.getTeam())))));
			}
		}
	}
	private double getCostForPath(Point from, Point to) {
		
		return from.distance(to);
	}
	@Override
	public int getWinningTeam() {
		for(int i = 0; i < gameMap.getPlayers().size();i++) {
			Player p = gameMap.getPlayers().get(i);
			if(p.getCurrentWeapon()!=null && p.getCurrentWeapon().getName().indexOf("Flag")!=-1)
			{
				if(p.getLocation().distance(getTeamFlagLocation(p.getTeam()))<GameMap.GRID_PIXELS-p.getRadius()) {
					if(++teamScores[p.getTeam()-1]==3) {
						teamScores = new int[4];
						return p.getTeam();
					}
					else {
						gameMap.resetGame();
						return -1;
					}
				}
			}
		}
		return -1;
	}
	public Point getClosestOtherTeamFlagLocation(Player p)
	{
		double minDist = Double.MAX_VALUE;
		Point loc = null;
		for(int i = 0; i < gameMap.getMap().length;i++) {
			for(int j = 0; j < gameMap.getMap()[i].length;j++) {
				if(gameMap.getMap()[i][j] == 'X' || gameMap.getMap()[i][j] == '_') continue;
				if(new Weapon(gameMap.getMap()[i][j], new Point(i,j)).getName().indexOf("Team "+p.getTeam()+" Flag")!=-1) continue;
				if(new Weapon(gameMap.getMap()[i][j], new Point(i,j)).getName().indexOf("Flag")!=-1) {
					if(gameMap.getGridPoint(p.getLocation()).distance(new Point(i,j))<minDist) {
						minDist = gameMap.getGridPoint(p.getLocation()).distance(new Point(i,j));
						loc = new Point(i,j);
					}
				}
			}
		}
		return loc;
	}
	public Point2D.Double getTeamFlagLocation(int team)
	{
		for(int i = 0; i < gameMap.getMap().length;i++) {
			for(int j = 0; j < gameMap.getMap()[i].length;j++) {
				if(gameMap.getMap()[i][j] == 'X' || gameMap.getMap()[i][j] == '_') continue;
				if(new Weapon(gameMap.getMap()[i][j], new Point(i,j)).getName().indexOf("Team "+team+" Flag")!=-1)
					return new Point2D.Double(i*25+GameMap.GRID_PIXELS/2, j*25 + GameMap.GRID_PIXELS/2);
			}
		}
		return null;
	}

	@Override
	public String getModeName() { return "Capture The Flag"; }

	@Override
	public String getScoreForPlayer(Player player) {
		
		return player.getName() + ": " + player.getStats().getNumKills();
	}

	@Override
	public String getScoreForTeam(int team) {
		return "" + teamScores[team-1];
	}

}
