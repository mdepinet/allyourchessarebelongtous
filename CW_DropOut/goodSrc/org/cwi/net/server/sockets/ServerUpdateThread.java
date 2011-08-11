package org.cwi.net.server.sockets;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import org.cwi.shoot.control.Shoot;
import org.cwi.shoot.model.Player;

public class ServerUpdateThread extends Thread {
	private long gameId;
	private boolean done;
	
	public ServerUpdateThread(long gameId){
		this.gameId = gameId;
	}
	
	public void run(){
		while (!done){
			try{
				Thread.sleep(33);
			} catch (InterruptedException ex){
				ex.printStackTrace();
			}
			Shoot game = Server.gameIdMap.get(gameId);
			if (game == null) continue;
			for (String user : Server.gameIdToPlayerMap.get(gameId)){
				ObjectOutputStream oos = Server.playerOutputMap.get(user);
				if (oos == null) continue;
				try {
					Server.correctForUser(game, user);
					oos.writeObject(game);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		for (Player p : Server.controllers.keySet()){
			if (p.getGAME_ID() == gameId) Server.controllers.remove(p);
		}
		Server.gameIdMap.remove(gameId);
		for (List<String> users : Server.gameIdToPlayerMap.values()){
			for (String user : users){
				Server.PCs.remove(user);
				try{Server.playerOutputMap.get(user).close();}catch(Throwable t){}
				Server.playerOutputMap.remove(user);
				Server.playerInputMap.get(user).setDone();
				Server.playerInputMap.remove(user);
			}
		}
		Server.gameIdToPlayerMap.remove(gameId);
	}
	
	public void setDone(){
		done = true;
	}
}
