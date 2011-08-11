package org.cwi.shoot.threads;

import org.cwi.shoot.map.GameMap;
import org.cwi.shoot.model.Player;


public class RespawnThread extends Thread {
	private long millis;
	private Player p;
	private GameMap gm;
	private boolean indexZero;
	private boolean dead;
	
	public RespawnThread(GameMap gm, Player p, boolean indexZero, long millis){
		this.millis = millis;
		this.p = p;
		this.gm = gm;
		this.indexZero = indexZero;
	}
	public void kill()
	{
		dead = true;
	}
	public void resetPlayer() { p.reset(); }
	public void run(){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(dead) { gm.getThreads().remove(this); return; }
		respawn();
	}
	public void respawn() {
		if (indexZero) gm.getPlayers().add(0, p);
		else gm.getPlayers().add(p);
		gm.spawn(p);
		gm.getThreads().remove(this);
	}
}
