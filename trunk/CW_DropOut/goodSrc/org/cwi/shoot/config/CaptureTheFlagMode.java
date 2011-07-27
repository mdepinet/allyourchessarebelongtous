package org.cwi.shoot.config;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Player.PlayerType;
import org.cwi.shoot.model.Weapon;
import org.cwi.shoot.model.Weapon.WeaponType;
import org.cwi.shoot.threads.RespawnThread;

public class CaptureTheFlagMode extends GameMode {
	private static final int capturesToWin = 3;
	private Map<Player, Integer> flagsCaptured;
	private Map<Integer, Point> flagSpawnLocs;
	//indices of the longs are the teams which those flags belong to
	private long[] flagRespawnTimes;
	private static final char[] FLAG_CHARS= {'L','M','N','O'};
	public CaptureTheFlagMode(){
		flagsCaptured = new HashMap<Player,Integer>();
		flagSpawnLocs = new HashMap<Integer, Point>();
		flagRespawnTimes= new long[4];
	}
	@Override
	public String getModeName() {
		return "Capture the Flag";
	}
	@Override
	public void loadGameObjects(GameMap map) {
		char[][] charMap = map.getMap();
		Set<Character> myChars = new HashSet<Character>();
		char[] myCharsArray = getAdditionalMapChars();
		for (char c : myCharsArray){
			myChars.add(c);
		}
		modeMap = new char[charMap.length][charMap[0].length];
		for (int r = 0; r < charMap.length; r++){
			for (int c = 0; c < charMap[r].length; c++){
				if (myChars.contains(charMap[r][c])){
					modeMap[r][c] = charMap[r][c];
					flagSpawnLocs.put(modeMap[r][c]-75, new Point(r,c));
					charMap[r][c] = '_';
				}
			}
		}
	}

	@Override
	public String getScoreForPlayer(Player player) {
		return player.getName()+": "+ (flagsCaptured.get(player)==null ? 0 : flagsCaptured.get(player));
	}

	@Override
	public String getScoreForTeam(int team, List<Player> players) {
		int score = 0;
		for(Player p : players)
			if(flagsCaptured.get(p)!=null &&  p.getTeam()==team){
				score+=flagsCaptured.get(p);
			}
		return ""+score;
	}

	@Override
	public void update(List<Player> players) {
		for(int i = 0; i<players.size(); i++){
			Player p = players.get(i);
			if(isFlag(p.getCurrWeapon())){
				if(flagSpawnLocs.get(p.getTeam()).equals(GameMap.getGridPoint(p.getLocation())) && modeMap[flagSpawnLocs.get(p.getTeam()).x][flagSpawnLocs.get(p.getTeam()).y]==(char)(p.getTeam()+75)) {
					flagsCaptured.put(p, (flagsCaptured.get(p)!=null ? flagsCaptured.get(p)+1 : 1));
					resetMode(players);
				}
			}
			else{
				Point gridPoint = GameMap.getGridPoint(p.getLocation());
				char c = modeMap[gridPoint.x][gridPoint.y];
				if(Arrays.binarySearch(FLAG_CHARS, c)>=0){
					//the player is standing on a flag
					Weapon w = new Weapon(c,gridPoint);
					if(canGetWeapon(p,w)) {
						p.addWeapon(w, this);
						p.switchToWeapon(p.getNumWeapons()-1);
						modeMap[gridPoint.x][gridPoint.y] = GameOptions.BLANK_CHARACTER;
					}
				}
			}
		}
		//Respawn any flags that have been sitting around for >10 seconds
		for(int i = 0; i<flagRespawnTimes.length;i++){
			if(flagRespawnTimes[i]!=0)
				if(System.currentTimeMillis()>flagRespawnTimes[i]+10000){
					for(int r = 0; r<modeMap.length; r++)
						for(int c = 0; c<modeMap[r].length; c++)
							if(modeMap[r][c]==(char)(i+75))
								modeMap[r][c]=GameOptions.BLANK_CHARACTER;
					modeMap[flagSpawnLocs.get(i).x][flagSpawnLocs.get(i).x] = (char)(i+75);
				}
					
		}
	}
	private void resetMode(List<Player> players)
	{
		flagRespawnTimes= new long[4];
		for(Player p : players) {
			p.getWeapons().clear(); p.addWeapon(new Weapon("Default"), this);
		}
		for(int i = 0; i < modeMap.length;i++)
			for(int j = 0; j < modeMap[i].length;j++)
				modeMap[i][j] = GameOptions.BLANK_CHARACTER;
		for(int i:flagSpawnLocs.keySet())
			modeMap[flagSpawnLocs.get(i).x][flagSpawnLocs.get(i).y] = (char)(i+75);
	}
	private boolean isFlag(Weapon w){
		return w.getType()==WeaponType.OBJECTIVE;
	}
	
	private boolean playerHasFlag(Player p) {
		return p.getCurrWeapon().getType().equals(WeaponType.OBJECTIVE);
	}

	@Override
	public int getWinningTeam(List<Player> players) {
		int[] teamCaptures = new int[4];
		for(Player p : players)
			if(flagsCaptured.get(p)!=null){
				teamCaptures[p.getTeam()-1]+=flagsCaptured.get(p);
				if(teamCaptures[p.getTeam()-1]>=capturesToWin) {
					flagsCaptured = new HashMap<Player,Integer>();
					return p.getTeam();
				}
			}
		return -1;
	}

	@Override
	public boolean canGetWeapon(Player p, Weapon w) {
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
		//for(int r = 0; r<modeMap.length;r++)
		//	for(int c =0; c<modeMap[r].length;c++){
		//		if(modeMap[r][c]!=0)
		//			p.addObjective(new Objective(new Point(r,c), 1, 1));
		//	}
	}

	@Override
	public void onPlayerDeath(Player p) {
		if(playerHasFlag(p)){
			Point loc = GameMap.getGridPoint(p.getLocation());
			modeMap[loc.x][loc.y]=p.getCurrWeapon().getCharacter();
			flagRespawnTimes[p.getTeam()] = System.currentTimeMillis();
		}
		threads.add(new RespawnThread(gameMap, p, p.getType()==PlayerType.HUMAN, 5000));
		threads.get(threads.size()-1).start();
	}

	@Override
	public void onPlayerRespawn(Player p) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawModeMapPre(Graphics2D g) {
		for(Integer i : flagSpawnLocs.keySet())
			g.drawOval((int)flagSpawnLocs.get(i).getX()*GameMap.GRID_PIXELS-GameMap.GRID_PIXELS/2, (int)flagSpawnLocs.get(i).getY()*GameMap.GRID_PIXELS-GameMap.GRID_PIXELS/2, GameMap.GRID_PIXELS*2, GameMap.GRID_PIXELS*2);
		for(int i = 0; i < modeMap.length;i++) {
			for(int j = 0; j < modeMap[i].length;j++) {
				if(Arrays.binarySearch(FLAG_CHARS, modeMap[i][j])>=0) {
					Image img = Weapon.getWeaponImg(new Weapon(modeMap[i][j], new Point()).getImgLoc());
					if(img!=null)
						g.drawImage(img,i*GameMap.GRID_PIXELS, j*GameMap.GRID_PIXELS+GameMap.GRID_PIXELS/4, 30, 15, null);
				}
			}
		}
	}
	
	public void drawModeMapPost(Graphics2D g, List<Player> p) {
		// TODO Auto-generated method stub
		for(Player player : p) {
			if(playerHasFlag(player))
				g.drawImage(Weapon.getWeaponImg(player.getCurrWeapon().getImgLoc()), (int)player.getLocation().getX()+6, (int)player.getLocation().getY()-15,30,15, null);
		}
	}
}
