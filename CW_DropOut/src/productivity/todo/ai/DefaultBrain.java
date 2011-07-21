package productivity.todo.ai;

import java.awt.geom.Point2D;

import productivity.todo.model.GameMap;
import productivity.todo.model.Player;

public class DefaultBrain extends AbstractBrain {
	public void makeMove(GameMap map, Player p){
		int team = p.getTeam();
		Point2D.Double location = p.getLocation();
		Point2D.Double newLoc;
		Player enemy = map.getClosestTeamPlayer(team, location);
		Point2D.Double destLoc;
		Point2D.Double wLoc;
		Point2D.Double pLoc;
		if(enemy==null)
			destLoc = map.getClosestWeapon(location);
		else
		{
			pLoc = enemy.getLocation();
			wLoc = map.getClosestWeapon(location);
			destLoc = (wLoc==null || location.distance(pLoc) < location.distance(wLoc)) ? pLoc : wLoc;
		}
		if(destLoc!=null)
		{
			newLoc = addPoints(location,getDirectionToLoc(location,destLoc));
			if(enemy!=null)
			{
				p.setOrientation(getAngleBetweenPoints(location,enemy.getLocation())+Math.PI/2);
				if(p.getNumWeapons()>1 && p.getCurrentWeapon().getType().equals("Pistol"))
					p.nextWeapon();
				if(enemy.getHealth()>0)
				{
					if(location.distance(enemy.getLocation())<=enemy.getCurrentWeapon().getEffRange())
					{
						if(Math.abs(Math.sin(p.getOrientation()+Math.PI) - Math.sin(enemy.getOrientation()))<=Math.PI/5)
							newLoc = addPoints(location,multiplyPointByScalar(getDirectionToLoc(location,enemy.getLocation()),-1));
					}
					if(location.distance(enemy.getLocation())<=p.getCurrentWeapon().getEffRange())
					{
						if(p.getCurrentWeapon().canShoot())
							map.shoot(p);
					}
				}
			}
			p.setLocation(newLoc);
		}
		else {
			//get weapons
		}
	}
}
