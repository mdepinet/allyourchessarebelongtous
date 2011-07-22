package productivity.todo.ai;

import java.awt.Point;
import java.awt.geom.Point2D;

import productivity.todo.ai.path.MapGraph;
import productivity.todo.model.GameMap;
import productivity.todo.model.Player;
import productivity.todo.model.Weapon;


public abstract class AbstractBrain implements Controller {
	private static final double MAX_MOVE_DISTANCE = 2.;
	protected static MapGraph graph = new MapGraph();
	
	protected static Point2D.Double getSmartDirectionToLoc(Point2D.Double from, Point2D.Double to, GameMap m) {
		graph.createGraph(m.getMap());
		Point next = graph.getNextLocation(m.getGridPoint(from), m.getGridPoint(to));
		double angle = getAngleBetweenPoints(from, m.fromGridPoint(next));
		return new Point2D.Double(-(Math.cos(angle)),-(Math.sin(angle)));
	}
	
	protected static Point2D.Double multiplyPointByScalar(Point2D.Double point, double scalar) {
		return new Point2D.Double(point.x*scalar,point.y*scalar);
	}
	
	protected static Point2D.Double addPoints(Point2D.Double one, Point2D.Double two) {
		return new Point2D.Double(one.x+two.x,one.y+two.y);
	}
	
	protected static double getAngleBetweenPoints(Point2D.Double from, Point2D.Double to) {
		Point2D.Double ret = new Point2D.Double(from.x-to.x,from.y-to.y);
		return Math.atan2(ret.y,ret.x);
	}
	
	protected static Point2D.Double getDirectionToLoc(Point2D.Double from, Point2D.Double to) {
		double angle = getAngleBetweenPoints(from,to);
		return new Point2D.Double(-(Math.cos(angle)),-(Math.sin(angle)));
	}
	
	protected static double getLengthAsVector(Point2D.Double vector){
		return Math.hypot(vector.x, vector.y);
	}
		
	protected Player getClosestEnemy(GameMap map, Player p){
		return map.getClosestTeamPlayer(p.getTeam(), p.getLocation());
	}
	protected Point2D.Double getClosestWeaponLoc(GameMap map, Player p){
		return map.getClosestWeaponLoc(p);
	}
	protected Weapon getClosestWeapon(GameMap map, Player p){
		return map.getClosestWeapon(p);
	}
	
	public final void move(Point2D.Double newLoc, Player p){
		Point2D.Double currLoc = p.getLocation();
		if (currLoc.distance(newLoc) > MAX_MOVE_DISTANCE){
			Point2D.Double vector = new Point2D.Double(currLoc.x-newLoc.x, currLoc.y-newLoc.y);
			multiplyPointByScalar(vector, MAX_MOVE_DISTANCE/getLengthAsVector(vector));
			p.setLocation(addPoints(currLoc,vector));
		}
		else p.setLocation(newLoc);
	}
	public final void turn(double orientation, Player p){
		p.setOrientation(orientation);
	}
	public final void shoot(GameMap map, Player p){
		map.shoot(p);
	}
	
}
