package productivity.chess.control.networking;

import java.io.*;
import java.net.*;
import java.util.*;

import productivity.chess.model.Board;

public class Server extends Thread {

   private ServerSocket server;

   public Server() throws Exception {
	   server = new ServerSocket(3000);
     System.out.println("Server listening on port 3000.");
     this.start();
   } 

   public void run() {
     while(true) {
       try {
        System.out.println("Waiting for connections.");
        Socket client = server.accept();
        System.out.println("Accepted a connection from: "+
client.getInetAddress());
        Connect c = new Connect(client);
       } catch(Exception e) {}
     }
   }
}

class Connect extends Thread {
   private Socket client = null;
   private ObjectInputStream ois = null;
   private ObjectOutputStream oos = null;
    
   public Connect() {}

   public Connect(Socket clientSocket) {
     client = clientSocket;
     try {
      ois = new ObjectInputStream(client.getInputStream());
      oos = new ObjectOutputStream(client.getOutputStream());
     } catch(Exception e1) {
         try {
            client.close();
         }catch(Exception e) {
           System.out.println(e.getMessage());
         }
         return;
     }
     this.start();
   }

  
   public void run() {
      try {
         oos.writeObject(new Board());
         
         //oos.flush();
         //ois.close();
         //oos.close();
         //client.close(); 
      } catch(Exception e) {}       
   }
}
