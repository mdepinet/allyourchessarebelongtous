package org.cwi.shoot.config;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cwi.shoot.ai.Objective;
import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Weapon;
import org.cwi.shoot.model.Weapon.WeaponType;

public class CaptureTheFlagMode extends GameMode {
	private static final int capturesToWin = 3;
	private Map<Integer, Integer> flagsCaptured;
	private static final char[] FLAG_CHARS= {'L','M','N','O'};
	public CaptureTheFlagMode(){
		flagsCaptured = new HashMap<Integer,Integer>();
	}
	@Override
	public String getModeName() {
		return "Capture the Flag";
	}

	@Override
	public String getScoreForPlayer(Player player) {
		//TODO
		return null;
	}

	@Override
	public String getScoreForTeam(int team, List<Player> players) {
		int score = flagsCaptured.get(team)==null ? 0 : flagsCaptured.get(team);
		return GameMap.teamNames[team-1]+": "+score;
	}

	@Override
	public void loadGameObjects(GameMap map) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(List<Player> players) {
		for(Player p : players){
			if(isFlag(p.getCurrWeapon())){
				//Check if he's turning the flag in
			}
			else{
				Point gridPoint = GameMap.getGridPoint(p.getLocation());
				char c = modeMap[gridPoint.x][gridPoint.y];
				if(Arrays.binarySearch(FLAG_CHARS, c)>=0){
					//the player is standing on a flag
					p.addWeapon(new Weapon(c,gridPoint), this);
					modeMap[gridPoint.x][gridPoint.y] = '_';
				}
			}
		}
	}
	private boolean isFlag(Weapon w){
		return w.getType()==WeaponType.OBJECTIVE;
	}
	
	private boolean playerHasFlag(Player p) {
		return p.getCurrWeapon().getType().equals(WeaponType.OBJECTIVE);
	}

	@Override
	public int getWinningTeam(List<Player> players) {
		for(int i : flagsCaptured.keySet())
			if(flagsCaptured.get(i)>=capturesToWin)
				return i;
		return -1;
	}

	@Override
	public boolean canGetWeapon(Player p, Weapon w) {
		// TODO Auto-generated method stub
		if(w.getType()!=WeaponType.OBJECTIVE)
			return true;
		//if it's a flag with your team's number, you can't pick it up
		if(w.getName().contains(""+p.getTeam()))
			return false;
		return true;
	}

	@Override
	public char[] getAdditionalMapChars() {
		return FLAG_CHARS;
	}

	@Override
	public int getMaxNumTeams() {
		return 4;
	}

	@Override
	public void addObjectives(GameMap map, Player p) {
		// TODO: Needs optimization- there is no reason to go through the entire 30x30 grid to find 4 flags
		for(int r = 0; r<modeMap.length;r++)
			for(int c =0; c<modeMap[r].length;c++){
				if(modeMap[r][c]!='_')
					p.addObjective(new Objective(new Point(r,c), 1, 1));
			}
	}

	@Override
	public void onPlayerDeath(Player p) {
		if(playerHasFlag(p)){
			Point loc = GameMap.getGridPoint(p.getLocation());
			modeMap[loc.x][loc.y]=p.getCurrWeapon().getCharacter();
		}

	}

	@Override
	public void onPlayerRespawn(Player p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawModeMapPre(Graphics2D g) {
		// TODO Auto-generated method stub

	}
	
	public void drawModeMapPost(Graphics2D g, List<Player> p) {
		// TODO Auto-generated method stub

	}
}
