package productivity.chess.control.networking;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import productivity.chess.control.Chess;
import productivity.chess.model.GameBoard;

public class Client {
	public static void main(String[] args){
		Chess c = new Chess();
		boolean done = false;
		GameBoard lastBoard = null;
		
		ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        Socket s = null;
        try{
        	s = new Socket("10.0.0.89", 3030);
        	oos = new ObjectOutputStream(s.getOutputStream());
        	ois = new ObjectInputStream(s.getInputStream());
        	while (!done){
        		Object obj = ois.readObject();
        		if (obj instanceof GameBoard){
        			c.setBoard((GameBoard)obj);
        			lastBoard = (GameBoard)obj;
        		}
        		else System.err.println("Recieved non-GameBoard object");
        		
        		GameBoard newBoard = c.getBoard();
        		if (!newBoard.equals(lastBoard)){
        			lastBoard = newBoard;
        			oos.writeObject(lastBoard);
        		}
        	}
        }
        catch(IOException ex){
        	ex.printStackTrace();
        } catch (ClassNotFoundException e) {
			e.printStackTrace();
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