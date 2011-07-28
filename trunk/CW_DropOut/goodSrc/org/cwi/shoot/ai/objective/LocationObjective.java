package org.cwi.shoot.ai.objective;

import java.awt.geom.Point2D;

import org.cwi.shoot.model.Player;

public class LocationObjective extends Objective {
	private static final double EPSILON = 2;
	private Point2D.Double location;
	
	public LocationObjective(double weight, double cost, Point2D.Double location){
		super(weight, cost);
		this.location = location;
	}
	protected LocationObjective(double weight, double cost) {
		super(weight, cost);
		location = null;
	}
	
	@Override
	public Point2D.Double getTargetPoint(Player owner) {
		return location == null ? owner.getLocation() : location;
	}
	
	@Override
	public boolean isAccomplished(Player owner) {
		return owner.getLocation().distance(location) <= EPSILON;
	}

}
