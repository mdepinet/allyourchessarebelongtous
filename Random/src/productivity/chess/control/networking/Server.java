package productivity.chess.control.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import productivity.chess.view.PlayerPicker;


public class Server {

	public static void main(String[] args){
		System.out.println("Waiting for connections.");
		ServerSocket servSoc = null;
		
		PlayerPicker picker = new PlayerPicker();
		try {
		servSoc = new ServerSocket(8090);
			while (true){
					Socket client = servSoc.accept();
					picker.addClient(new ServerThread(client));
				} 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try{servSoc.close();}catch(Throwable t){}
		}
	}
	
	
	
}
//   private ServerSocket server;
//   private Board board;
//   private boolean end;
//   
//   public Server() throws Exception {
//	   server = new ServerSocket(3030);
//     System.out.println("Server listening on port 3030.");
//     this.run();
//   } 
//
//   public void run() {
//       try {
//        System.out.println("Waiting for connections.");
//        Socket client = server.accept();
//        System.out.println("Accepted a connection from: "+client.getInetAddress());
//        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
//        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
//        while(!isEnd()) {
//        	if(board!=null)
//        	{
//        		oos.writeObject(board);
//        		board=null;
//        	}
//        }
//        oos.writeObject("end");
//        oos.flush();
//        ois.close();
//        oos.close();
//        client.close(); 
//       } catch(Exception e) {}
//   }
//
//public void setBoard(Board board) {
//	this.board = board;
//}
//
//public Board getBoard() {
//	return board;
//}
//
//public void setEnd(boolean end) {
//	this.end = end;
//}
//
//public boolean isEnd() {
//	return end;
//}
//}