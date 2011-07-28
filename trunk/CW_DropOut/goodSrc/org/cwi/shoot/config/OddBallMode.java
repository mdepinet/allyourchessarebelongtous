package org.cwi.shoot.config;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cwi.shoot.ai.objective.KillObjective;
import org.cwi.shoot.ai.objective.LocationObjective;
import org.cwi.shoot.ai.objective.Objective;
import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Weapon;
import org.cwi.shoot.util.VectorTools;

public class OddBallMode extends GameMode{
	public static final int POINTS_TO_WIN = 151;
	private static final int BALL_RADIUS = 3;
	private Map<Integer, Integer> teamPoints = new HashMap<Integer, Integer>();
	private Player lastBallHolder = null;
	private long pickedUpTime = 0;
	private int currentStreak = 0;
	private Point oddLoc = null;
	
	
	@Override
	public void onReset(GameMap map, GameOptions setup){
		teamPoints = new HashMap<Integer, Integer>();
		currentStreak = 0;
		lastBallHolder = null;
		super.onReset(map, setup);
	}
	@Override
	public void loadGameObjects(GameMap map){
		super.loadGameObjects(map);
		findOddLoc();
	}
	private void findOddLoc(){
		OUTTER: for (int r = 0; r<modeMap.length; r++){
			for (int c = 0; c<modeMap[r].length; c++){
				if (modeMap[r][c] == 'I'){
					oddLoc = new Point(r,c);
					break OUTTER;
				}
			}
		}
	}
	
	
	@Override
	public String getModeName() {
		return "Odd Ball";
	}

	@Override
	public String getScoreForPlayer(Player player) {
		return player.getName() + ": " + player.getStats().getNumKills();
	}

	@Override
	public String getScoreForTeam(int team, List<Player> players) {
		Integer i = teamPoints.get(team);
		i = (i == null ? 0 : i);
		return ""+(lastBallHolder != null && lastBallHolder.getTeam() == team ? i+currentStreak : i);
	}

	@Override
	public void update(List<Player> players) {
		if (lastBallHolder == null){
			pickedUpTime = System.currentTimeMillis();
			for (int i = 0; i<players.size(); i++){
				if (oddLoc == null) findOddLoc();
				if (GameMap.getGridPoint(players.get(i).getLocation()).equals(oddLoc) && lastBallHolder == null){
					Weapon w = new Weapon('I', oddLoc);
					players.get(i).addWeapon(w, this);
					players.get(i).setCurrWeapon(w);
				}
				if (players.get(i).getCurrWeapon() != null && players.get(i).getCurrWeapon().getName().equals("OddBall")){
					lastBallHolder = players.get(i);
					break;
				}
			}
		}
		else{
			if (lastBallHolder.getCurrWeapon() != null && lastBallHolder.getCurrWeapon().getName().equals("OddBall")){
				currentStreak = (int) ((System.currentTimeMillis() - pickedUpTime)/1000.);
				oddLoc = GameMap.getGridPoint(lastBallHolder.getLocation());
			}
			else{
				if (!teamPoints.containsKey(lastBallHolder.getTeam())) teamPoints.put(lastBallHolder.getTeam(),currentStreak);
				else teamPoints.put(lastBallHolder.getTeam(),teamPoints.get(lastBallHolder.getTeam())+currentStreak);
				currentStreak = 0;
				lastBallHolder = null;
			}
		}
		
	}

	@Override
	public int getWinningTeam(List<Player> players) {
		if (currentStreak >= POINTS_TO_WIN) return lastBallHolder.getTeam();
		for (Map.Entry<Integer,Integer> entry : teamPoints.entrySet()){
			if (entry.getValue() + ((lastBallHolder != null && lastBallHolder.getTeam() == entry.getKey()) ? currentStreak : 0) >= POINTS_TO_WIN) return entry.getKey();
		}
		return -1;
	}

	@Override
	public boolean canGetWeapon(Player p, Weapon w) {
		return true;
	}

	@Override
	public List<Character> getAdditionalMapChars() {
		return Arrays.asList(new Character[]{'I'});
	}

	@Override
	public int getMaxNumTeams() {
		return 10;
	}

	@Override
	public List<Objective> getObjectives(GameMap map, Player p) {
		LinkedList<Objective> objectives = new LinkedList<Objective>();
		if (lastBallHolder == null){
			if (oddLoc != null) objectives.add(new LocationObjective(100,p.getLocation().distance(GameMap.fromGridPoint(oddLoc)),GameMap.fromGridPoint(oddLoc)));
		}
		else{
			if (p == lastBallHolder){
				//Run away somehow
			}
			else if (p.getTeam() == lastBallHolder.getTeam()){
				if (oddLoc != null) objectives.add(new LocationObjective(100,p.getLocation().distance(GameMap.fromGridPoint(oddLoc)),GameMap.fromGridPoint(oddLoc)));
			}
			else{
				objectives.add(new KillObjective(100,p.getLocation().distance(GameMap.fromGridPoint(oddLoc)),lastBallHolder));
			}
		}
		return objectives;
	}

	@Override
	public void onPlayerDeath(Player p) {
	}

	@Override
	public void onPlayerRespawn(Player p) {
	}
	
	@Override
	public void drawModeMapPre(Graphics2D g) {
	}
	
	@Override
	public void drawModeMapPost(Graphics2D g, List<Player> players) {
		Point2D.Double drawLoc = null;
		if (lastBallHolder == null){
			if (oddLoc != null){
				drawLoc = GameMap.fromGridPoint(oddLoc);
			}
		}
		else{
			drawLoc = VectorTools.addVectors(lastBallHolder.getLocation(),VectorTools.scaleVector(new Point2D.Double(Math.cos(lastBallHolder.getOrientation()),Math.sin(lastBallHolder.getOrientation())),Player.radius));
		}
		if (drawLoc != null){
			g.setColor(Color.CYAN);
			g.fillOval((int) (drawLoc.x-BALL_RADIUS), (int) (drawLoc.y-BALL_RADIUS), BALL_RADIUS*2, BALL_RADIUS*2);
		}
	}
}
