package org.cwi.net.server.sockets;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.cwi.shoot.control.Shoot;

public class ServerGameThread extends Thread {
	private ObjectInputStream ois;
	boolean done = false;
	
	public ServerGameThread(ObjectInputStream ois, String user){
		this.ois = ois;
		Server.playerInputMap.put(user, this);
	}
	
	public void run(){
		while (!done){
			try{
				Shoot game = (Shoot) ois.readObject();
				Shoot oldGame = Server.gameIdMap.get(game.getID());
				if (Server.validate(oldGame, game)) Server.gameIdMap.put(game.getID(),game);
			} catch (ClassNotFoundException ex){
				ex.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try{ois.close();}catch(Throwable t){}
	}
	
	public void setDone(){
		done = true;
	}
	
}
