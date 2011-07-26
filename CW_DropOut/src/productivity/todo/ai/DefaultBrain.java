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
		Point2D.Double destLoc;
//		if(!objectives.isEmpty()) destLoc = map.fromGridPoint(objectives.get(0).getLocation());
//		else {
			if(enemy!=null) {
				Point2D.Double pLoc = enemy.getLocation();
				Point2D.Double wLoc = getClosestWeaponLoc(map, p);
				destLoc = (wLoc==null || location.distance(pLoc) < location.distance(wLoc)) ? pLoc : wLoc;
			}
			else {
				destLoc = getClosestWeaponLoc(map, p);
			}
//		}
		if(destLoc!=null)
		{
			newLoc = getSmartDirectionToLoc(location,destLoc,map);
			if(enemy!=null)
			{
				turn(getOrientationToPoint(location,enemy.getLocation()),p);
				if(p.getNumWeapons()>1 && p.getCurrentWeapon().getType().equals("Pistol"))
					p.nextWeapon();
				if(enemy.getHealth()>0)
				{
					if(location.distance(enemy.getLocation())<=enemy.getCurrentWeapon().getEffRange())
					{
							newLoc = scaleVector(getDirectionToLoc(location,enemy.getLocation()),-1);
					}
					if(location.distance(enemy.getLocation())<=p.getCurrentWeapon().getEffRange()*1.5)
					{
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