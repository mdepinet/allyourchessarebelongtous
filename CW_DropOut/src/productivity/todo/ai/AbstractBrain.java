package productivity.todo.ai;

import java.awt.Point;
import java.awt.geom.Point2D;

import productivity.todo.ai.path.MapGraph;
import productivity.todo.model.GameMap;


public abstract class AbstractBrain implements Controller {
	protected static MapGraph graph = new MapGraph();
	
	public static Point2D.Double getSmartDirectionToLoc(Point2D.Double from, Point2D.Double to, GameMap m) {
		graph.createGraph(m.getMap());
		Point next = graph.getNextLocation(m.getGridPoint(from), m.getGridPoint(to));
		double angle = getAngleBetweenPoints(from, m.fromGridPoint(next));
		return new Point2D.Double(-(Math.cos(angle)),-(Math.sin(angle)));
	}
	
	public static Point2D.Double multiplyPointByScalar(Point2D.Double point, double scalar) {
		return new Point2D.Double(point.x*scalar,point.y*scalar);
	}
	
	public static Point2D.Double addPoints(Point2D.Double one, Point2D.Double two) {
		return new Point2D.Double(one.x+two.x,one.y+two.y);
	}
	
	public static double getAngleBetweenPoints(Point2D.Double from, Point2D.Double to) {
		Point2D.Double ret = new Point2D.Double(from.x-to.x,from.y-to.y);
		return Math.atan2(ret.y,ret.x);
	}
	
	public static Point2D.Double getDirectionToLoc(Point2D.Double from, Point2D.Double to) {
		double angle = getAngleBetweenPoints(from,to);
		return new Point2D.Double(-(Math.cos(angle)),-(Math.sin(angle)));
	}
	
}
