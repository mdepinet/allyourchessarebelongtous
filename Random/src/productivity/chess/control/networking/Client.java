package productivity.chess.control.networking;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import productivity.chess.control.Chess;
import productivity.chess.model.GameBoard;

public class Client {
	public static void main(String[] args){
		Chess c = new Chess("Client");
		boolean done = false;
		GameBoard lastBoard = null;
		boolean myMove = false;
		
		ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        Socket s = null;
        try{
        	s = new Socket("10.0.0.89", 3030);
        	oos = new ObjectOutputStream(s.getOutputStream());
        	ois = new ObjectInputStream(s.getInputStream());
        	while (!done){
        		while (myMove){
					if (!c.getBoard().equals(lastBoard)){
						oos.writeObject(c.getBoard());
						lastBoard = c.getBoard();
						myMove = false;
					}
					else Thread.sleep(500);
				}
				if (!myMove){
					Object obj = ois.readObject();
					if (obj instanceof GameBoard){
						GameBoard newBoard = (GameBoard)obj;
						c.setBoard(newBoard);
						lastBoard = newBoard;
						myMove = true;
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