package productivity.todo.config;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Scanner;

import productivity.todo.model.GameMap;

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
		// TODO Auto-generated method stub
		return -1;
	}

}
