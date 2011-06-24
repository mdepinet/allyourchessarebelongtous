package productivity.chess.control.networking;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Properties;

import productivity.chess.control.Chess;
import productivity.chess.encryption.BoardCrypter;
import productivity.chess.model.GameBoard;

public class Client {
	public static void main(String[] args){
		Chess c = new Chess("Client");
		//c.setWhiteTurn(false);
		boolean done = false;
		GameBoard lastBoard = null;
		boolean myMove = false;
		
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		byte[] boardish = new byte[4096];
		ByteArrayInputStream bais = null;
		ByteArrayOutputStream baos = null;
        Socket s = null;
        try{
        	Properties p = new Properties();
        	p.load(new FileInputStream("resources/props.properties"));
        	String[] servers = ((String)p.get("servers")).split(",");
        	for (int i = 0; i<servers.length && s == null; i++){
        		try{
        			s = new Socket(servers[i], 3030);
        		} catch (ConnectException ex){
        			s = null;
        		}
        	}
        	if (s == null){
        		System.err.println("FAILED TO FIND SERVER! DYing...... S..l..o..w..l..yyyyy");
        		Thread.sleep(5000);
        		System.exit(69);
        	}
        	else{
        		System.out.println("Connected to server at "+s.getInetAddress().toString());
        	}
        	
        	while (!done){
        		while (myMove){
					if (!c.getBoard().equals(lastBoard)){
						baos = new ByteArrayOutputStream();
						oos = new ObjectOutputStream(baos);
						oos.writeObject(c.getBoard());
						boardish = baos.toByteArray();
						boardish = BoardCrypter.encrypt(boardish);
						s.getOutputStream().write(boardish);
						lastBoard = c.getBoardCopy();
						myMove = false;
						c.setWhiteTurn(true, "Black");
					}
					else Thread.sleep(500);
				}
				if (!myMove){
					s.getInputStream().read(boardish);
					boardish = BoardCrypter.decrypt(boardish);
					bais = new ByteArrayInputStream(boardish);
					ois = new ObjectInputStream(bais);
					Object obj = ois.readObject();
					if (obj instanceof GameBoard){
						GameBoard newBoard = (GameBoard)obj;
						c.setBoard(newBoard);
						lastBoard = c.getBoardCopy();
						myMove = true;
						c.setWhiteTurn(false, "Black");
					}
					else System.err.println("Received non GameBoard object...");
				}
        	}
        } catch(IOException ex){
        	ex.printStackTrace();
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException ex){
			System.err.println(ex.getMessage());
		}
	}
}
//   public static void main(String[] args) {
//      ObjectOutputStream oos = null;
//      ObjectInputStream ois = null;
//      Socket socket = null;
//      Object board = new Object();
//      try {
//        // open a socket connection
//        socket = new Socket("10.0.0.95", 3030);
//        // open I/O streams for objects
//        oos = new ObjectOutputStream(socket.getOutputStream());
//        ois = new ObjectInputStream(socket.getInputStream());
//        // read an object from the server
//        for(;;)
//        {
//        	board = ois.readObject();
//        	if(board instanceof Board)
//        		System.out.print((Board)board);
//        	else if(board instanceof String &&((String)board).equals("end"))
//        		break;
//        }
//        oos.close();
//        ois.close();
//      } catch(Exception e) {
//        System.out.println("Message " + e.getMessage());
//      }
//   }
//}
//}