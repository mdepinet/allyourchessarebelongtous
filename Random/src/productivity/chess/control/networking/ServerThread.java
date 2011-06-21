package productivity.chess.control.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import productivity.chess.control.Chess;
import productivity.chess.model.GameBoard;

public class ServerThread extends Thread {

	private Chess c;
	private Socket client;
	private boolean done = false;
	private GameBoard lastBoard = null;
	private boolean myMove = true;
	
	public ServerThread(Socket client){
		this.client = client;
		c = new Chess("Server");
		lastBoard = c.getBoardCopy();
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
				while (myMove){
					if (!c.getBoard().equals(lastBoard)){
						oos.writeObject(c.getBoard());
						lastBoard = c.getBoardCopy();
						myMove = false;
					}
					else Thread.sleep(500);
				}
				if (!myMove){
					Object obj = ois.readObject();
					if (obj instanceof GameBoard){
						GameBoard newBoard = (GameBoard)obj;
						c.setBoard(newBoard);
						lastBoard = c.getBoardCopy();
						myMove = true;
					}
					else System.err.println("Received non GameBoard object...");
				}
				
			} catch (SocketException ex){
				System.err.println(ex.getMessage());
				done = true;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}
		}
		try{
			ois.close();
			oos.close();
		} catch(Throwable t){}
	}
}
