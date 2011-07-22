package productivity.todo.ai;

import java.awt.geom.Point2D;

import productivity.todo.model.GameMap;
import productivity.todo.model.Player;

public class DefaultBrain extends AbstractBrain {
	public void makeMove(GameMap map, Player p){
		Point2D.Double location = p.getLocation();
		Point2D.Double newLoc;
		Player enemy = getClosestEnemy(map,p);
		Point2D.Double destLoc;
		Point2D.Double wLoc;
		Point2D.Double pLoc;
		if(enemy==null)
			destLoc = getClosestWeaponLoc(map, p);
		else
		{
			pLoc = enemy.getLocation();
			wLoc = getClosestWeaponLoc(map, p);
			destLoc = (wLoc==null || location.distance(pLoc) < location.distance(wLoc)) ? pLoc : wLoc;
		}
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
}
