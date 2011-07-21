package productivity.todo.ai;

import java.awt.geom.Point2D;

import productivity.todo.model.GameMap;


public abstract class AbstractBrain implements Controller {
	
	public static Point2D.Double getSmartDirectionToLoc(Point2D.Double loc, GameMap m)
	{
		throw new UnsupportedOperationException("Nobody has taken the time to write this yet...");
	}
	public static Point2D.Double multiplyPointByScalar(Point2D.Double point, double scalar)
	{
		return new Point2D.Double(point.x*scalar,point.y*scalar);
	}
	public static Point2D.Double addPoints(Point2D.Double one, Point2D.Double two)
	{
		return new Point2D.Double(one.x+two.x,one.y+two.y);
	}
	public static double getAngleBetweenPoints(Point2D.Double from, Point2D.Double to)
	{
		Point2D.Double ret = new Point2D.Double(from.x-to.x,from.y-to.y);
		return Math.atan2(ret.y,ret.x);
	}
	public static Point2D.Double getDirectionToLoc(Point2D.Double from, Point2D.Double to)
	{
		double angle = getAngleBetweenPoints(from,to);
		return new Point2D.Double(-(Math.cos(angle)),-(Math.sin(angle)));
	}
	
}
