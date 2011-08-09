package org.cwi.net.server.sockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.cwi.net.server.GameDescription;
import org.cwi.shoot.control.Shoot;
import org.cwi.shoot.model.Player;

public class ServerRequestThread extends Thread {

	private Socket s;
	
	public ServerRequestThread(Socket client){
		this.s = client;
	}

	public void run(){
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		try{
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			Request req = (Request) ois.readObject();
			switch (req){
			case GAME_LIST:
				handleGameListRequest(ois, oos); break;
			case JOIN_PLAYER:
				handleJoinPlayerRequest(s); break;
			case JOIN_TEAM:
				handleJoinTeamRequest(s); break;
			case NEW_GAME:
				handleNewGameRequest(s); break;
			}
		} catch (IOException ex){
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try{ois.close();oos.close();}catch(Throwable t){}
		}
	}

	public Socket getSocket() {
		return s;
	}
	public void setSocket(Socket s) {
		this.s = s;
	}
	
	private void handleGameListRequest(ObjectInputStream ois, ObjectOutputStream oos){
		List<GameDescription> games = new LinkedList<GameDescription>();
		for (Shoot game : Server.gameIdMap.values()){
			int numHumans = Server.gameIdToPlayerMap.get(game.getID()).size();
			Map<Integer, String> scoreMap = new TreeMap<Integer, String>();
			for (int i = 1; i<=game.getNumTeams(); i++){
				scoreMap.put(i,game.getMode().getScoreForTeam(i, game.getMap().getPlayers()));
			}
			games.add(new GameDescription(game.getID(),game.getMode().getClass(),game.getMapName(),game.getNumTeams(),game.getNumPlayersPerTeam(),numHumans,scoreMap,game.getWeaponSet(),game.allowsTeamTakeover(),game.allowsPlayerTakeover()));
		}
		try {
			oos.writeObject(Response.APPROVED);
			oos.writeObject(games);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try{ois.close();oos.close();} catch(Throwable t){}
		}
		
	}
	private void handleJoinPlayerRequest(Socket s){
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		String ip = s.getInetAddress().toString();
		try {
			ois = new ObjectInputStream(s.getInputStream());
			oos = new ObjectOutputStream(s.getOutputStream());
			GameDescription desc = (GameDescription) ois.readObject();
			Shoot game = Server.gameIdMap.get(desc.getID());
			if (!game.allowsPlayerTakeover()){
				oos.writeObject(Response.DENIED);
			}
			else{
				boolean foundPlayer = false;
				for (Player p : game.getMap().getPlayers()){
					if (p.getType() != Player.PlayerType.HUMAN){
						p.setType(Player.PlayerType.HUMAN);
						String prevController = Server.controllers.get(p);
						Server.PCs.get(prevController).remove(p);
						Server.controllers.put(p, ip);
						oos.writeObject(Response.APPROVED);
						oos.writeObject(game);
						foundPlayer = true;
						break;
					}
				}
				if (!foundPlayer){
					oos.writeObject(Response.DENIED_FULL);
				}
				else {
					new ServerGameThread(s).start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try{ois.close();oos.close();}catch(Throwable t){}
		}
	}
	private void handleJoinTeamRequest(Socket s){
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		String ip = s.getInetAddress().toString();
		try {
			ois = new ObjectInputStream(s.getInputStream());
			oos = new ObjectOutputStream(s.getOutputStream());
			GameDescription desc = (GameDescription) ois.readObject();
			Shoot game = Server.gameIdMap.get(desc.getID());
			if (!game.allowsTeamTakeover()){
				oos.writeObject(Response.DENIED);
			}
			else{
				boolean foundTeam = false;
				Map<Integer, List<Player>> teamPlayers = new TreeMap<Integer, List<Player>>();
				for (Player p : game.getMap().getPlayers()){
					int team = p.getTeam();
					if (teamPlayers.get(team) == null) teamPlayers.put(team, new LinkedList<Player>());
					teamPlayers.get(team).add(p);
				}
				for (List<Player> players : teamPlayers.values()){
					boolean allNPCs = true;
					for (Player p : players){
						if (p.getType() == Player.PlayerType.HUMAN){
							allNPCs = false;
							break;
						}
					}
					if (allNPCs){
						foundTeam = true;
						for (Player p : players){
							p.setType(Player.PlayerType.TRANSITION);
							String prevController = Server.controllers.get(p);
							Server.PCs.get(prevController).remove(p);
							Server.controllers.put(p, ip);
						}
						oos.writeObject(Response.APPROVED);
						oos.writeObject(game);
						break;
					}
				}
				if (!foundTeam){
					oos.writeObject(Response.DENIED_FULL);
				}
				else {
					new ServerGameThread(s).start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try{ois.close();oos.close();}catch(Throwable t){}
		}
	}
	private void handleNewGameRequest(Socket s){
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		String ip = s.getInetAddress().toString();
		try {
			ois = new ObjectInputStream(s.getInputStream());
			oos = new ObjectOutputStream(s.getOutputStream());
			if (Server.gameIdMap.keySet().size() > Server.MAX_GAMES) oos.writeObject(Response.DENIED_SERVER_CAPACITY);
			else{
				Shoot game = (Shoot)ois.readObject();
				long gameId = Server.getNextGameId();
				game.setID(gameId);
				Server.gameIdMap.put(gameId, game);
				Server.gameIdToPlayerMap.put(gameId, new ArrayList<String>());
				Server.gameIdToPlayerMap.get(gameId).add(ip);
				Server.PCs.put(ip, game.getMap().getPlayers());
				for (Player p : game.getMap().getPlayers()){
					Server.controllers.put(p, ip);
				}
				oos.writeObject(Response.APPROVED);
				new ServerGameThread(s).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try{ois.close();oos.close();}catch(Throwable t){}
		}
	}
}
