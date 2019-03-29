package com.vosure.chat.server;

import java.net.InetAddress;
import java.util.Random;

public class ServerClient {
	public String name;
	public InetAddress address;
	public int port;
	private int ID; // TODO(vosure): Create a Unique RNG;
	public int attempt = 0;
	
	
	ServerClient(String name, InetAddress address, int port, final int ID){
		this.name = name;
		this.address = address;
		this.port = port;
		this.ID = ID;
	}
	public int getID() {
		return ID;
	}
}
