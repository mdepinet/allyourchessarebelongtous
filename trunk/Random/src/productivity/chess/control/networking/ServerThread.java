package productivity.chess.control.networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import productivity.chess.control.Chess;
import productivity.chess.encryption.BoardCrypter;
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
		byte[] boardish = new byte[4096];
		ByteArrayInputStream bais = null;
		ByteArrayOutputStream baos = null;
		while (!done){
			try {
				while (myMove){
					if (!c.getBoard().equals(lastBoard)){
						baos = new ByteArrayOutputStream();
						oos = new ObjectOutputStream(baos);
						oos.writeObject(c.getBoard());
						boardish = baos.toByteArray();
						boardish = BoardCrypter.encrypt(boardish);
						client.getOutputStream().write(boardish);
						lastBoard = c.getBoardCopy();
						myMove = false;
						c.setWhiteTurn(false);
					}
					else Thread.sleep(500);
				}
				if (!myMove){
					client.getInputStream().read(boardish);
					boardish = BoardCrypter.decrypt(boardish);
					bais = new ByteArrayInputStream(boardish);
					ois = new ObjectInputStream(bais);
					Object obj = ois.readObject();
					if (obj instanceof GameBoard){
						GameBoard newBoard = (GameBoard)obj;
						c.setBoard(newBoard);
						lastBoard = c.getBoardCopy();
						myMove = true;
						c.setWhiteTurn(true);
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
