package org.cwi.net.server.sockets;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.cwi.shoot.control.Shoot;
import org.cwi.shoot.model.Player;


public class Server {
	public static final int MAX_GAMES = 20;
	
	static Map<Long, List<String>> gameIdToPlayerMap;
	static Map<Long, Shoot> gameIdMap;
	static Map<String, List<Player>> PCs;
	static Map<Player, String> controllers;
	static Map<String, ObjectOutputStream> playerOutputMap;
	static Map<String, ServerGameThread> playerInputMap;
	private static long nextGameId = 0;

	public static void main(String[] args){
		gameIdToPlayerMap = new TreeMap<Long, List<String>>();
		gameIdMap = new TreeMap<Long, Shoot>();
		PCs = new HashMap<String, List<Player>>();
		controllers = new HashMap<Player, String>();
		playerOutputMap = new TreeMap<String, ObjectOutputStream>();
		playerInputMap = new TreeMap<String, ServerGameThread>();
		System.out.println("Waiting for connections.");
		ServerSocket servSoc = null;		
		try {
		servSoc = new ServerSocket(3030);
			while (true){
					Socket client = servSoc.accept();
					new ServerRequestThread(client).start();
				} 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try{servSoc.close();}catch(Throwable t){}
		}
	}
	
	static long getNextGameId(){
		return nextGameId++;
	}
	
	static boolean validate(Shoot prev, Shoot curr){
		//TODO
		return true;
	}
	
	static void correctForUser(Shoot game, String user){
		for (Player p : game.getMap().getPlayers()){
			if (p.getType() == Player.PlayerType.HUMAN){
				if (Server.controllers.get(p).equals(user)) continue;
				else p.setType(Player.PlayerType.REMOTE);
			}
			else if (Server.controllers.get(p).equals(user)) p.setType(Player.PlayerType.COMPUTER);
			else p.setType(Player.PlayerType.REMOTE);
		}
	}
	
}
