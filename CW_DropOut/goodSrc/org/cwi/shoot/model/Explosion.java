package org.cwi.shoot.model;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

import org.cwi.shoot.map.Updatable;

public class Explosion implements Updatable{
	public static int explosionDamage = 150;
	private double alpha;
	private double scale;
	private double maxSize;
	Point2D.Double location;
	private int scheduler;
	private boolean active;
	public Explosion(Point2D.Double loc, double max)
	{
		location = loc;
		maxSize = max;
		alpha = 1;
		scale = 0.0;
		active = true;
	}
	/**
	 * Update takes care of expanding then fading out and disappearing
	 */
	public void update()
	{
		scheduler++;
		if(scheduler > 30) { active = false; return; }
		if(scheduler > 15)
		{	
			scale = alpha = 1-((scheduler - 15)/15.);
		}
		else scale = scheduler/15.;
	}
	/**
	 * Get the smallest rectangle surrounding this explosion on the map
	 * @return The smallest rectangle surround this explosion on the map
	 */
	public Rectangle getRect()
	{
		return new Rectangle((int)(location.x-(scale*maxSize*0.5)), (int)(location.y-(scale*maxSize*0.5)), (int)(scale*maxSize), (int)(scale*maxSize));
	}
	public double getAlpha() {
		return alpha;
	}
	public double getMaxSize() {
		return maxSize;
	}
	public void setMaxSize(double maxSize) {
		this.maxSize = maxSize;
	}
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	public double getScale() {
		return scale;
	}
	public void setScale(double scale) {
		this.scale = scale;
	}
	public Point2D.Double getLocation() {
		return location;
	}
	public void setLocation(Point2D.Double location) {
		this.location = location;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
}
