package productivity.chess.control.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import productivity.chess.control.Chess;
import productivity.chess.model.GameBoard;

public class ServerThread extends Thread {

	private Chess c;
	private Socket client;
	private boolean done = false;
	private GameBoard lastBoard = null;
	
	public ServerThread(Socket client){
		this.client = client;
		c = new Chess();
		lastBoard = c.getBoard();
	}
	
	public void run(){
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		try {
			ois = new ObjectInputStream(client.getInputStream());
			oos = new ObjectOutputStream(client.getOutputStream());
		} catch (IOException e) {
			done = true;
			e.printStackTrace();
		}
		while (!done){
			try {
				Object obj = ois.readObject();
				if (obj instanceof GameBoard){
					GameBoard newBoard = (GameBoard)obj;
					if (!newBoard.equals(lastBoard)){
						c.setBoard(newBoard);
						lastBoard = newBoard;
						continue;
					}
				}
				else System.err.println("Received non GameBoard object...");
				
				if (!c.getBoard().equals(lastBoard)){
					oos.writeObject(c.getBoard());
					lastBoard = c.getBoard();
					continue;
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		try{
			ois.close();
			oos.close();
		} catch(Throwable t){}
	}
}
