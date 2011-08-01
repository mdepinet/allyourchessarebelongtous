package org.cwi.shoot.ai;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.cwi.shoot.ai.objective.KillObjective;
import org.cwi.shoot.ai.objective.LocationObjective;
import org.cwi.shoot.ai.objective.Objective;
import org.cwi.shoot.ai.path.MapGraph;
import org.cwi.shoot.config.GameMode;
import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Weapon;
import org.cwi.shoot.util.VectorTools;



public abstract class AbstractBrain implements Controller {
	public static final int KILL_OBJECTIVE_WEIGHT = 10;
	public static final int WEAPON_OBJECTIVE_WEIGHT = 4;
	protected static final double MAX_MOVE_DISTANCE = 2.;
	protected static MapGraph graph = new MapGraph();
	protected List<Objective> objectives = new ArrayList<Objective>();
	protected Objective activeObjective = null;
	private static boolean graphCreated = false;
	private static final int ACTIVE_WEIGHT = 50;
	
	public static Point2D.Double getSmartDirectionToLoc(Point2D.Double from, Point2D.Double to, GameMap m) {
		if (m!= null && !graphCreated){
			graph.createGraph(m.getMap());
			graphCreated = true;
		}
		if (graphCreated){
			Point next = graph.getNextLocation(GameMap.getGridPoint(from), GameMap.getGridPoint(to));
			return GameMap.fromGridPoint(next);
		}
		else return getDirectionToLoc(from,to);
	}
	protected static Point2D.Double getDirectionToLoc(Point2D.Double from, Point2D.Double to) {
		return VectorTools.addVectors(from,VectorTools.normalize(VectorTools.getVectorBetween(from,to)));
	}
		
	protected Player getClosestEnemy(GameMap map, Player p){
		return map.getClosestNonTeamPlayer(p.getTeam(), p.getLocation());
	}
	protected Player[] getEnemies(GameMap map, Player p){
		return map.getEnemies(p);
	}
	protected Player getClosestAlly(GameMap map, Player p){
		return map.getClosestTeamPlayer(p.getTeam(), p.getLocation());
	}
	protected Player[] getAllies(GameMap map, Player p){
		return map.getAllies(p);
	}
	protected Point2D.Double getClosestWeaponLoc(GameMap map, Player p){
		return map.getClosestWeaponLoc(p);
	}
	protected Weapon getClosestWeapon(GameMap map, Player p){
		return map.getClosestWeapon(p);
	}
	public final void move(Point2D.Double newLoc, Player p){
		if (newLoc == null || p == null) return;
		Point2D.Double currLoc = p.getLocation();
		if (currLoc.distance(newLoc) > MAX_MOVE_DISTANCE){
			Point2D.Double vector = VectorTools.getVectorBetween(currLoc, newLoc);
			vector = VectorTools.scaleVector(VectorTools.normalize(vector), MAX_MOVE_DISTANCE);
			p.setLocation(VectorTools.addVectors(currLoc,vector));
		}
		else p.setLocation(newLoc);
	}
	public final void turn(double orientation, Player p){
		p.setOrientation(orientation);
	}
	public final void shoot(GameMap map, Player p){
		if (p.getCurrWeapon().canShoot()) map.shoot(p);
	}
	public final void switchWeapon(Player p, Weapon weapon){
		p.setCurrWeapon(weapon);
	}
	
	protected void addDefaultObjectives(GameMap map, Player p){
		if (objectives == null) objectives = new LinkedList<Objective>();
		Player enemy = getClosestEnemy(map,p);
		Point2D.Double location = p.getLocation();
		Point2D.Double wepLoc = getClosestWeaponLoc(map,p);
		
		if (enemy!= null) objectives.add(new KillObjective(KILL_OBJECTIVE_WEIGHT,GameMap.getGridPoint(location).distance(GameMap.getGridPoint(enemy.getLocation())),enemy));
		if (wepLoc != null) objectives.add(new LocationObjective(WEAPON_OBJECTIVE_WEIGHT,GameMap.getGridPoint(location).distance(GameMap.getGridPoint(wepLoc)),wepLoc));
	}
	
	protected Objective getDefaultObjective(GameMap map, Player p, GameMode mode){
		objectives = mode.getObjectives(map, p);
		if (objectives == null) objectives = new LinkedList<Objective>();
		addDefaultObjectives(map, p);
		Collections.sort(objectives, new ObjectiveComparator());
		activeObjective = objectives.get(0);
		return activeObjective;
	}
	
	
	private class ObjectiveComparator implements Comparator<Objective>{
		@Override
		public int compare(Objective o1, Objective o2) {
			return -(o1.compareTo(o2) + (o1 == activeObjective ? ACTIVE_WEIGHT : o2 == activeObjective ? -ACTIVE_WEIGHT : 0));
		}
	}
}
