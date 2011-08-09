package org.cwi.net.server.sockets;

import java.net.Socket;

public class ServerGameThread extends Thread {
	private Socket s;
	
	public ServerGameThread(Socket s){
		this.s = s;
	}
}
