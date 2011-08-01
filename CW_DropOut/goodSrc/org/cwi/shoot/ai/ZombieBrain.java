package org.cwi.shoot.ai;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import org.cwi.shoot.ai.objective.Objective;
import org.cwi.shoot.config.GameMode;
import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Weapon;
import org.cwi.shoot.util.VectorTools;

public class ZombieBrain extends AbstractBrain {
	private static final int OBJECTIVE_REFRESH = 50;
	private static final int ACTIVE_WEIGHT = 50;
	private int objectiveTurns = 50;
	private Objective activeObjective = null;
	
	public void makeMove(GameMode mode, GameMap map, Player p){
		if (objectiveTurns >= OBJECTIVE_REFRESH){
			getObjectives(mode, map, p);
			addDefaultObjectives(map, p);
			Collections.sort(objectives, new ObjectiveComparator());
			objectiveTurns = 0;
		}
		objectiveTurns++;
		Player enemy = getClosestEnemy(map,p);
		Point2D.Double location = p.getLocation();
		Point2D.Double newLoc;
		Point2D.Double destLoc = null;
		
		//Pick location.  Objective -> Enemy or Weapon -> Weapon
		if(!objectives.isEmpty()) destLoc = objectives.get(0).getTargetPoint(p, map);
		
		if(destLoc!=null){
			newLoc = getSmartDirectionToLoc(location,destLoc,map);
			if(enemy!=null) {
				turn(VectorTools.getOrientationToPoint(location,enemy.getLocation()),p);
				if(p.getNumWeapons()>1 && p.getCurrWeapon().getTypes().contains(Weapon.WeaponType.PISTOL)) p.nextWeapon();
				if(enemy.getHealth()>0) {
//					if(enemy.getCurrWeapon() != null && location.distance(enemy.getLocation())<=enemy.getCurrWeapon().getEffRange()) {
//							newLoc = scaleVector(getDirectionToLoc(location,enemy.getLocation()),-1);
//					}
					if(p.getCurrWeapon() != null && (p.getCurrWeapon().getEffRange()==0 || location.distance(enemy.getLocation())<=p.getCurrWeapon().getEffRange()*1.5)) {
							shoot(map,p);
					}
				}
			}
			Point2D.Double vector = VectorTools.getVectorBetween(location, newLoc);
			vector = VectorTools.scaleVector(VectorTools.normalize(vector), 1.75);
			
			move(VectorTools.addVectors(location,vector),p);
		}
	}

	private void getObjectives(GameMode mode, GameMap map, Player p) {
		objectives = mode.getObjectives(map, p);
		if (objectives == null) objectives = new LinkedList<Objective>();
	}
	
	class ObjectiveComparator implements Comparator<Objective>{
		@Override
		public int compare(Objective o1, Objective o2) {
			return o1.compareTo(o2) + (o1 == activeObjective ? ACTIVE_WEIGHT : o2 == activeObjective ? -ACTIVE_WEIGHT : 0);
		}
	}
}