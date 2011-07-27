package org.cwi.shoot.ai.mike;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cwi.shoot.ai.AbstractBrain;
import org.cwi.shoot.config.GameMode;
import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Weapon;

public class SmartBrain extends AbstractBrain {
//	private static int pursueWeight = 100;
//	private static int fleeWeight = 100;
//	private static int getWeaponWeight = 100;
//	
//	private static int reloadWeight = 100;
	
	private static double burstSpreadWeight = 1.0;
	private static double powerWeight = 1.0;
	private static double splashWeight = 1.0;
	private static double speedWeight = 1.0;
	
	private Player[] enemies = null;
	private Player[] allies = null;
	Map<Player,Point2D.Double> prevLocs = new HashMap<Player,Point2D.Double>();


	@Override
	public void makeMove(GameMode mode, GameMap map, Player p) {
		double health = p.getHealth();
		Player nearestEnemy = getClosestEnemy(map, p);
		if (enemies == null) enemies = getEnemies(map, p);
		if (allies == null) allies = getAllies(map, p);
		int bestWeapon = selectWeapon(p, nearestEnemy, map);
		Weapon w = bestWeapon == -1 ? null : p.getWeapons().get(bestWeapon);
		Player[] closeEnemies = getEnemiesWithinRange(enemies, p, w);
		
		Player target = nearestEnemy;
		double lowestHealth = 0;
		for (Player o : closeEnemies){
			if (o.getHealth() < lowestHealth){
				lowestHealth = o.getHealth();
				target = o;
			}
		}
		
		if (target != null && target.getHealth()*closeEnemies.length > health) run(p, map, nearestEnemy, w);
		else pursue(p, map, target, w);
	}
	
	private int selectWeapon(Player p, Player enemy, GameMap map){
		if (enemy == null) return -1;
		double distToEnemy = p.getLocation().distance(enemy.getLocation());
		int thrownIndex = -1;
		int bestValue = 0, bestValueIndex = 0;
		List<Weapon> options = p.getWeapons();
		for (int i = 0; i<options.size(); i++){
			Weapon w = options.get(i);
			if (w.getType().equals("Thrown")) thrownIndex = i;
			int value = 0;
			if (w.getEffRange()*2 >= distToEnemy && w.canShoot()){
				value += w.getRoundsPerShot()/w.getSpread()*burstSpreadWeight;
				value += w.getPower()*powerWeight;
				value += w.getSplash()*splashWeight;
				value += w.getBulletSpeed()*speedWeight;
			}
			if (value > bestValue){
				bestValue = value;
				bestValueIndex = i;
			}
		}
		if (canHit(p, map, enemy,options.get(bestValueIndex))){
			//Direct
			return bestValueIndex;
		}
		else return thrownIndex; //Over wall
		
	}
	
	private Player[] getEnemiesWithinRange(Player[] enemies, Player p, Weapon w){
		if (w == null) return new Player[0];
		List<Player> inRange = new LinkedList<Player>();
		for (Player o : enemies){
			if (p.getLocation().distance(o.getLocation()) <= w.getEffRange()*2) inRange.add(o);
		}
		return inRange.toArray(new Player[inRange.size()]);
	}
	
	private void run(Player p, GameMap map, Player nearestEnemy, Weapon w){
		Point2D.Double dest = getClosestWeaponLoc(map,p);
		if (dest == null){
			dest = getSmartDirectionToLoc(p.getLocation(),addVectors(p.getLocation(),scaleVector(getDirectionToLoc(p.getLocation(),nearestEnemy.getLocation()),-10)),map);
		}
		move(dest, p);
		if (w != null && canHit(p, map, nearestEnemy, w)){
			switchWeapon(p, w);
			turn(getSlopeBetweenPoints(p.getLocation(),predictNextLoc(nearestEnemy)),p);
			if (canHit(p, map, nearestEnemy,w)) shoot(map,p);
		}
		updateLocations();
	}
	
	private void pursue(Player p, GameMap map, Player target, Weapon w){
		Point2D.Double dest = getClosestWeaponLoc(map, p);
		if (w == null || !canHit(p, map, target, w)) move(getSmartDirectionToLoc(p.getLocation(),dest, map), p);
		else{
			double numTurns = p.getLocation().distance(dest)/MAX_MOVE_DISTANCE;
			Point2D.Double targetLoc = addVectors(target.getLocation(),scaleVector(getVectorBetween(target.getLocation(),predictNextLoc(target)),numTurns));
			Point2D.Double straight = addVectors(p.getLocation(),scaleVector(getSmartDirectionToLoc(p.getLocation(),target.getLocation(),map),numTurns));
			if (targetLoc.distance(dest) - targetLoc.distance(straight) >= w.getEffRange()){
				dest = straight;
			}
			
			move(getSmartDirectionToLoc(p.getLocation(),dest, map), p);
			switchWeapon(p,w);
			if (canHit(p, map, target, p.getCurrWeapon())){
				turn(getOrientationToPoint(p.getLocation(),target.getLocation()), p);
				shoot(map, p);
			}
		}
		updateLocations();
	}
	
	private boolean canHit(Player p, GameMap map, Player enemy, Weapon wep){
		boolean direct = true;//wep.getType().equals("Thrown") || normalize(getDirectionToLoc(p.getLocation(),enemy.getLocation())).equals(normalize(getSmartDirectionToLoc(p.getLocation(),enemy.getLocation(), map)));
		return direct && p.getLocation().distance(predictNextLoc(enemy)) <= wep.getEffRange()*(isApproaching(p, enemy) ? 2.1 : 1.9);
	}
	
	private Point2D.Double predictNextLoc(Player enemy){
		Point2D.Double lastLoc = prevLocs.get(enemy);
		if (lastLoc == null) return enemy.getLocation();
		return addVectors(enemy.getLocation(),getDirectionToLoc(lastLoc,enemy.getLocation()));
	}
	
	private void updateLocations(){
		for (Player p : enemies){
			prevLocs.put(p,p.getLocation());
		}
	}
	
	private boolean isApproaching(Player p, Player enemy){
		if (!prevLocs.containsKey(enemy)) return false;
		return p.getLocation().distance(prevLocs.get(enemy)) < p.getLocation().distance(enemy.getLocation());
	}

}
