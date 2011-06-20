package productivity.chess.control.networking;


import java.io.*;
import java.net.*;
import java.util.*;

import productivity.chess.model.Board;

public class Client {
   public static void main(String argv[]) {
      ObjectOutputStream oos = null;
      ObjectInputStream ois = null;
      Socket socket = null;
      Board board = null;
      try {
        // open a socket connection
        socket = new Socket("yourMachineNameORipAddress", 3000);
        // open I/O streams for objects
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
        // read an object from the server
        board = (Board) ois.readObject();
        
        oos.close();
        ois.close();
      } catch(Exception e) {
        System.out.println(e.getMessage());
      }
   }
}
