package org.cwi.shoot.config;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Weapon;

public class SurvivalMode extends GameMode {
	private static final float DAMAGE_MOD = .25f;
	private static final String HEALTH_IMG = "resource/images/health.gif";
	private List<Player> dead;
	private Set<Weapon> weps;
	private List<Point> healthPacks;
	private Image health;
	
	public SurvivalMode(){
		super();
		dead = new ArrayList<Player>();
		weps = new HashSet<Weapon>();
		healthPacks = new ArrayList<Point>();
		try {
			health = ImageIO.read(new File(HEALTH_IMG));
		} catch (IOException e) {}
	}
	@Override
	public String getModeName() {
		return "Survival";
	}

	public void onStartup(GameMap map, GameOptions setup){
		super.onStartup(map,setup);
		Player.setRegenSpeed(0);
	}
	public void loadGameObjects(GameMap map){
		super.loadGameObjects(map);
		for(int r=0; r <modeMap.length; r++)
			for(int c=0; c <modeMap[r].length; c++)
				if(modeMap[r][c]=='+')
					healthPacks.add(new Point(r,c));
	}
	@Override
	public String getScoreForPlayer(Player player) {
		return player.getName() + "'s Health: " + (int)(Math.ceil(player.getHealth()));
	}

	@Override
	public String getScoreForTeam(int team, List<Player> players) {
		int alive = 0;
		for(int i = 0; i < players.size();i++)
			if(players.get(i).getTeam()==team && players.get(i).getHealth()>0)
				alive++;
		return alive+" left";
				
	}

	@Override
	public void update(List<Player> players) {
		for(Player p : players){
			Weapon wep = p.getCurrWeapon();
			if(weps.add(wep))
				wep.setPower((int)(wep.getPower()*DAMAGE_MOD));
			Point point = GameMap.getGridPoint(p.getLocation());
			if(modeMap[point.x][point.y]=='+' && p.getHealth()<100){
				p.setHealth(p.getHealth()+50);
				modeMap[point.x][point.y]= GameOptions.BLANK_CHARACTER;
				healthPacks.remove(point);
			}
		}
	}

	@Override
	public int getWinningTeam(List<Player> players) {
		Map<Integer, Integer> membersAlive = new TreeMap<Integer, Integer>();
		for(Player p : players){
			if(p.getHealth()>0){
				int team = p.getTeam();
				if (membersAlive.containsKey(team)) membersAlive.put(team, membersAlive.get(team)+1);
				else membersAlive.put(team, 1);
			}
		}
		int numAlive = 0;
		int winTeam = -1;
		for(int i : membersAlive.keySet())
			if(membersAlive.get(i)!=null){
				numAlive++;
				winTeam=i;
			}
		if(numAlive != 1)
			return -1;
		return winTeam;
	}
	public void onReset(GameMap map, GameOptions setup){
		for(Player p : map.getPlayers())
			p.die(this);
		for(Player p : dead){
			map.getPlayers().add(p);
			map.spawn(p);
		}
		weps.clear();
		dead.clear();
		healthPacks.clear();
	}
	@Override
	public boolean canGetWeapon(Player p, Weapon w) {
		return true;
	}

	@Override
	public char[] getAdditionalMapChars() {
		return new char[]{'+'};
	}

	@Override
	public int getMaxNumTeams() {
		return 10;
	}

	@Override
	public void addObjectives(GameMap map, Player p) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onPlayerDeath(Player p) {
		dead.add(p);
		// TODO Auto-generated method stub
	}

	@Override
	public void onPlayerRespawn(Player p) {
		// TODO Auto-generated method stub

	}
	@Override
	public void drawModeMapPre(Graphics2D g) {
		for(Point p : healthPacks){
			Point2D.Double converted = GameMap.fromGridPoint(p);
			g.drawImage(health,(int)converted.x,(int)converted.y, 15, 15, null);

		}	
	}
	@Override
	public void drawModeMapPost(Graphics2D g, List<Player> players) {

	}
	public boolean handlesRespawn(){
		return true;
	}

}
