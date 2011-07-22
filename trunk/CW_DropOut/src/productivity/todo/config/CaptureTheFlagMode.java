package productivity.todo.config;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Scanner;

import productivity.todo.model.GameMap;
import productivity.todo.model.Player;
import productivity.todo.model.Weapon;

public class CaptureTheFlagMode extends GameMode {

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
		// TODO Auto-generated method stub

	}

	@Override
	public int getWinningTeam() {
		for(int i = 0; i < gameMap.getPlayers().size();i++) {
			Player p = gameMap.getPlayers().get(i);
			if(p.getCurrentWeapon().getName().indexOf("Flag")!=-1)
			{
				if(p.getLocation().distance(getTeamFlagLocation(p.getTeam()))<GameMap.GRID_PIXELS-p.getRadius())
					return p.getTeam();
			}
		}
		return -1;
	}
	public Point2D.Double getTeamFlagLocation(int team)
	{
		for(int i = 0; i < gameMap.getMap().length;i++) {
			for(int j = 0; j < gameMap.getMap()[i].length;j++) {
				if(gameMap.getMap()[i][j] == 'X' || gameMap.getMap()[i][j] == '_') continue;
				if(new Weapon(gameMap.getMap()[i][j]).getName().indexOf(""+team)!=-1)
					return new Point2D.Double(i*25+GameMap.GRID_PIXELS/2, j*25 + GameMap.GRID_PIXELS/2);
			}
		}
		return null;
	}

}
