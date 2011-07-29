package org.cwi.shoot.config;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cwi.shoot.ai.ZombieBrain;
import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;
import org.cwi.shoot.model.Player.PlayerType;
import org.cwi.shoot.model.Weapon;
import org.cwi.shoot.util.NameGenerator;

public class ClassicZombiesMode extends ZombiesWGuns {

	@Override
	public String getModeName() {
		// TODO Auto-generated method stub
		return "Classic Zombies";
	}

//	@Override
//	public String getScoreForPlayer(Player player) {
//		// TODO Auto-generated method stub
//		if(player.getBrain() instanceof ZombieBrain)
//			return "";
//		return player.getName() + ": " + player.getStats().getNumKills();
//	}
//
//	@Override
//	public String getScoreForTeam(int team, List<Player> players) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void update(List<Player> players) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public int getWinningTeam(List<Player> players) {
//		// TODO Auto-generated method stub
//		for(Player p : players){
//			if(p.getHealth()>0 && p.getType()==PlayerType.HUMAN)
//				return -1;
//		}
//		return 1;
//	}

	@Override
	public boolean canGetWeapon(Player p, Weapon w) {
		// TODO Auto-generated method stub
		if(p.getBrain() instanceof ZombieBrain)
			return false;
		return true;
	}
	public List<Player> addZombies(int num) {
		ArrayList<Player> zombiesToAdd = new ArrayList<Player>();
		NameGenerator gen = null;
		try {
			gen = new NameGenerator(GameOptions.NAME_RESOURCE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int i = 1; i < num; i++) {
			Player foe = new Player(gen.compose((int)(Math.random()*3)+2));
			foe.setTeam(ZOMBIES_TEAM_NUM);
			foe.setType(PlayerType.COMPUTER);
			foe.setLocation(GameMap.fromGridPoint(spawnLocs.get((int)(spawnLocs.size()*Math.random()))));
			foe.addWeapon(new Weapon('Z', new Point()), this);
			foe.setBrain(new ZombieBrain());
			zombiesToAdd.add(foe);
			
		}
		return zombiesToAdd;
	}
	@Override
	public List<Character> getAdditionalMapChars() {
		List<Character> specChars = new ArrayList<Character>();
		specChars.add('0');
		specChars.add('Z');
		return specChars;
	}

//	@Override
//	public int getMaxNumTeams() {
//		return 1;
//	}
//
//	@Override
//	public List<Objective> getObjectives(GameMap map, Player p) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void onPlayerDeath(Player p) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onPlayerRespawn(Player p) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void drawModeMapPre(Graphics2D g) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void drawModeMapPost(Graphics2D g, List<Player> players) {
//		// TODO Auto-generated method stub
//
//	}
//	public boolean handlesRespawn(){
//		return true;
//	}
}
