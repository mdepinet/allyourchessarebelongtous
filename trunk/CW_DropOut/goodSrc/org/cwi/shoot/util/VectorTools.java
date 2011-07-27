package org.cwi.shoot.util;

import java.awt.geom.Point2D;

public class VectorTools {
	
	public static Point2D.Double scaleVector(Point2D.Double point, double scalar) {
		return new Point2D.Double(point.x*scalar,point.y*scalar);
	}
	
	public static Point2D.Double addVectors(Point2D.Double one, Point2D.Double two) {
		return new Point2D.Double(one.x+two.x,one.y+two.y);
	}
	
	public static double getSlopeBetweenPoints(Point2D.Double from, Point2D.Double to) {
		return (to.y-from.y) / (to.x-from.x);
	}
	
	public static double getAngleBetweenVectors(Point2D.Double from, Point2D.Double to) {
		double answer = from.x*to.x+from.y*to.y; //crossProduct
		answer /= length(from)*length(to); //Divide by lengths to get cos(Theta)
		return Math.acos(answer);
	}
	
	public static double length(Point2D.Double vector){
		return Math.hypot(vector.x, vector.y);
	}
	
	public static Point2D.Double normalize(Point2D.Double vector){
		return scaleVector(vector,1./length(vector));
	}
	
	public static Point2D.Double getVectorBetween(Point2D.Double p1, Point2D.Double p2){
		return new Point2D.Double(p2.x-p1.x,p2.y-p1.y);
	}
}
