package productivity.chess.control.networking;


import java.io.*;
import java.net.*;
import java.util.*;

import productivity.chess.model.Board;

public class Client {
   public static void main(String[] args) {
      ObjectOutputStream oos = null;
      ObjectInputStream ois = null;
      Socket socket = null;
      Object board = new Object();
      try {
        // open a socket connection
        socket = new Socket("10.0.0.95", 3030);
        // open I/O streams for objects
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
        // read an object from the server
        for(;;)
        {
        	board = ois.readObject();
        	if(board instanceof Board)
        		System.out.print((Board)board);
        	else if(board instanceof String &&((String)board).equals("end"))
        		break;
        }
        oos.close();
        ois.close();
      } catch(Exception e) {
        System.out.println("Message" + e.getMessage());
      }
   }
}
