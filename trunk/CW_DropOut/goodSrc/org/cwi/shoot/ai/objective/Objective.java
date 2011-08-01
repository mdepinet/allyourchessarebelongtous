package org.cwi.shoot.ai.objective;

import java.awt.geom.Point2D;

import org.cwi.shoot.ai.AbstractBrain;
import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;

public abstract class Objective implements Comparable<Objective> {
	private static final double WEIGHT_SCALE = 25;
	
	protected double weight;
	protected double cost;
	
	protected Objective(double weight, double cost){
		this.weight = weight;
		this.cost = cost;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}
	public int compareTo(Objective other) {
		return (int)((weight*WEIGHT_SCALE-cost) - (other.getWeight()*WEIGHT_SCALE-other.getCost()));
	}
	
	public abstract Point2D.Double getTargetPoint(Player owner, GameMap map);
	public abstract boolean isAccomplished(Player owner);
	public void execute(AbstractBrain brain, Player p, GameMap map){
		brain.move(getTargetPoint(p, map),p);
	}
	
	public String toString(){
		return "Unknown objective";
	}
}
