package productivity.chess.control.networking;

import java.io.*;
import java.net.*;
import java.util.*;

import productivity.chess.model.Board;

public class Server {

   private ServerSocket server;
   private Board board;
   private boolean end;
   
   public Server() throws Exception {
	   server = new ServerSocket(3030);
     System.out.println("Server listening on port 3030.");
     this.run();
   } 

   public void run() {
       try {
        System.out.println("Waiting for connections.");
        Socket client = server.accept();
        System.out.println("Accepted a connection from: "+client.getInetAddress());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        while(!isEnd()) {
        	if(board!=null)
        	{
        		oos.writeObject(board);
        		board=null;
        	}
        }
        oos.writeObject("end");
        oos.flush();
        ois.close();
        oos.close();
        client.close(); 
       } catch(Exception e) {}
   }

public void setBoard(Board board) {
	this.board = board;
}

public Board getBoard() {
	return board;
}

public void setEnd(boolean end) {
	this.end = end;
}

public boolean isEnd() {
	return end;
}
}