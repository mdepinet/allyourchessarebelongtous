package productivity.todo.ai;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Collections;

import productivity.todo.config.CaptureTheFlagMode;
import productivity.todo.config.TeamDeathmatchMode;
import productivity.todo.model.GameMap;
import productivity.todo.model.Player;

public class DefaultBrain extends AbstractBrain {
	public void makeMove(GameMap map, Player p){
		addObjectives(map,p);
		Player enemy = getClosestEnemy(map,p);
		Point2D.Double location = p.getLocation();
		Point2D.Double newLoc;
		Point2D.Double destLoc = map.fromGridPoint(objectives.get(0).getLocation());
		if(destLoc!=null)
		{
			newLoc = addPoints(location,getSmartDirectionToLoc(location,destLoc,map));
			if(enemy!=null)
			{
				turn(getAngleBetweenPoints(location,enemy.getLocation())+Math.PI/2,p);
				if(p.getNumWeapons()>1 && p.getCurrentWeapon().getType().equals("Pistol"))
					p.nextWeapon();
				if(enemy.getHealth()>0)
				{
					if(location.distance(enemy.getLocation())<=enemy.getCurrentWeapon().getEffRange())
					{
						if(Math.abs(Math.sin(p.getOrientation()+Math.PI) - Math.sin(enemy.getOrientation()))<=Math.PI/5)
							newLoc = addPoints(location,multiplyPointByScalar(getSmartDirectionToLoc(location,enemy.getLocation(),map),-1));
					}
					if(location.distance(enemy.getLocation())<=p.getCurrentWeapon().getEffRange()*1.5)
					{
						if(p.getCurrentWeapon().canShoot())
							shoot(map,p);
					}
				}
			}
			move(newLoc,p);
		}
		else {
			//get weapons
		}
	}
	private double getCostForPath(Point from, Point to) {
		
		return from.distance(to);
	}
	private void addObjectives(GameMap map, Player p) {
		 if(map.getGameMode() instanceof TeamDeathmatchMode) {
			 Player enemy = getClosestEnemy(map,p);
			 Point2D.Double location = getClosestWeaponLoc(map, p);
			 if(enemy!=null) addObjective(new Objective(map.getGridPoint(enemy.getLocation()), 10., getCostForPath(map.getGridPoint(p.getLocation()),map.getGridPoint(enemy.getLocation()))));
			 if(location!=null) addObjective(new Objective(map.getGridPoint(location), 5., getCostForPath(map.getGridPoint(p.getLocation()),map.getGridPoint(location))));
		 }
		 else if(map.getGameMode() instanceof CaptureTheFlagMode) {
			 
		 }
		 Collections.sort(objectives);
	}
}