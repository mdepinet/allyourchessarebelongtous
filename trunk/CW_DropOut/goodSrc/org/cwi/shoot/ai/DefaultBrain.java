package org.cwi.shoot.ai;

import java.awt.geom.Point2D;

import org.cwi.shoot.ai.objective.Objective;
import org.cwi.shoot.config.GameMode;
import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Weapon;
import org.cwi.shoot.util.VectorTools;

public class DefaultBrain extends AbstractBrain {
	public void makeMove(GameMode mode, GameMap map, Player p){
		Player enemy = getClosestEnemy(map,p);
		Point2D.Double location = p.getLocation();
		Point2D.Double newLoc;
		Point2D.Double destLoc = null;
		
		//Pick objective
		Objective objective = getDefaultObjective(map, p, mode);
		destLoc = objective.getTargetPoint(p);
		
		if(destLoc!=null){
			newLoc = getSmartDirectionToLoc(location,destLoc,map);
			if(enemy!=null) {
				turn(VectorTools.getOrientationToPoint(location,enemy.getLocation()),p);
				if(p.getNumWeapons()>1 && p.getCurrWeapon().getTypes().contains(Weapon.WeaponType.PISTOL)) p.nextWeapon();
				if(enemy.getHealth()>0) {
					if(p.getCurrWeapon() != null && location.distance(enemy.getLocation())<=p.getCurrWeapon().getEffRange()*1.5) {
							shoot(map,p);
					}
				}
			}
			move(newLoc,p);
		}
	}
}