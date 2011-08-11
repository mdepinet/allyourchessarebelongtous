package org.cwi.shoot.ai.objective;

import java.awt.geom.Point2D;

import org.cwi.shoot.ai.AbstractBrain;
import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.util.VectorTools;

public class KillObjective extends Objective {
	private static final double EPSILON = 2;
	private static final double ANGLE_CHANGE = Math.PI/8;
	
	private Player target;
	
	protected KillObjective(double weight, double cost){
		super(weight,cost);
		target = null;
	}
	public KillObjective(double weight, double cost, Player target){
		super(weight,cost);
		this.target = target;
	}
	
	public Player getTarget(){
		return target;
	}
	
	@Override
	public Point2D.Double getTargetPoint(Player assassin, GameMap map) {
		if (assassin.getCurrWeapon() != null && assassin.getLocation().distance(target.getLocation()) < assassin.getCurrWeapon().getEffRange()+EPSILON){
			double angle = VectorTools.getOrientationToPoint(target.getLocation(), assassin.getLocation())+ANGLE_CHANGE;
			Point2D.Double nextPoint = VectorTools.addVectors(target.getLocation(),VectorTools.scaleVector(new Point2D.Double(Math.cos(angle), Math.sin(angle)), assassin.getCurrWeapon().getEffRange()-EPSILON));
			if(map.isValid(nextPoint, (int)Player.radius))
				return nextPoint;
		}
		Point2D.Double moveToward = target.getLocation();//AbstractBrain.getSmartDirectionToLoc(assassin.getLocation(),target.getLocation(),null);
		if (moveToward == null) return null;
		else return moveToward;
	}
	
	@Override
	public void execute(AbstractBrain brain, Player p, GameMap map){
		
	}
	
	@Override
	public boolean isAccomplished(Player assassin){
		return target.getHealth() <= 0;
	}
	
	public String toString(){
		return "KillObjective for "+target;
	}

}
