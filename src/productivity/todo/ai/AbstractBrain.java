package productivity.todo.ai;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import productivity.todo.ai.path.MapGraph;
import productivity.todo.model.GameMap;
import productivity.todo.model.Player;
import productivity.todo.model.Weapon;


public abstract class AbstractBrain implements Controller {
	protected static final double MAX_MOVE_DISTANCE = 2.;
	protected static final double MIN_MOVE_DISTANCE = 0.5;
	protected static MapGraph graph = new MapGraph();
	protected List<Objective> objectives = new ArrayList<Objective>();
	
	protected static Point2D.Double getSmartDirectionToLoc(Point2D.Double from, Point2D.Double to, GameMap m) {
		graph.createGraph(m.getMap());
		Point next = graph.getNextLocation(m.getGridPoint(from), m.getGridPoint(to));
		return m.fromGridPoint(next);
	}
	
	protected static Point2D.Double scaleVector(Point2D.Double point, double scalar) {
		return new Point2D.Double(point.x*scalar,point.y*scalar);
	}
	
	protected static Point2D.Double addVectors(Point2D.Double one, Point2D.Double two) {
		return new Point2D.Double(one.x+two.x,one.y+two.y);
	}
	
	protected static double getSlopeBetweenPoints(Point2D.Double from, Point2D.Double to) {
		return (to.y-from.y) / (to.x-from.x);
	}
	
	protected static double getAngleBetweenVectors(Point2D.Double from, Point2D.Double to) {
		double answer = from.x*to.x+from.y*to.y; //crossProduct
		answer /= length(from)*length(to); //Divide by lengths to get cos(Theta)
		return Math.acos(answer);
	}
	
	protected static double getOrientationToPoint(Point2D.Double from, Point2D.Double to) {
//		Point2D.Double down = new Point2D.Double(0,1);
//		Point2D.Double vectorTo = getVectorBetween(from, to);
//		return getAngleBetweenVectors(down, vectorTo);
		return Math.atan2(to.y-from.y,to.x-from.x) - Math.PI/2;
	}
	
	protected static double length(Point2D.Double vector){
		return Math.hypot(vector.x, vector.y);
	}
	
	protected static Point2D.Double getDirectionToLoc(Point2D.Double from, Point2D.Double to) {
		return addVectors(from,normalize(getVectorBetween(from,to)));
	}
	
	protected static Point2D.Double normalize(Point2D.Double vector){
		return scaleVector(vector,1./length(vector));
	}
	
	protected static Point2D.Double getVectorBetween(Point2D.Double p1, Point2D.Double p2){
		return new Point2D.Double(p2.x-p1.x,p2.y-p1.y);
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
	public void addObjective(Objective o) {
		objectives.remove(o);
		objectives.add(o);
	}
	public final void move(Point2D.Double newLoc, Player p){
		if (newLoc == null || p == null) return;
		Point2D.Double currLoc = p.getLocation();
		if (currLoc.distance(newLoc) > MAX_MOVE_DISTANCE){
			Point2D.Double vector = getVectorBetween(currLoc, newLoc);
			vector = scaleVector(normalize(vector), MAX_MOVE_DISTANCE);
			p.setLocation(addVectors(currLoc,vector));
		}
		else if (currLoc.distance(newLoc) < MIN_MOVE_DISTANCE){
			//Do nothing (Don't fidget!)
		}
		else p.setLocation(newLoc);
	}
	public final void turn(double orientation, Player p){
		p.setOrientation(orientation);
	}
	public final void shoot(GameMap map, Player p){
		if (p.getCurrentWeapon().canShoot()) map.shoot(p);
	}
	public final void switchWeapon(Player p, int weapon){
		p.setCurrWeapon(weapon);
	}
	
}
