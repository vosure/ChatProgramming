package com.vosure.chat.server;

import java.net.InetAddress;

public class ServerClient {
	public String name;
	public InetAddress address;
	public int port;
	static private int ID = 0; // TODO(vosure): Create a Unique RNG;
	public int attempt = 0;
	
	ServerClient(String name, InetAddress address, int port){
		this.name = name;
		this.address = address;
		this.port = port;
		this.ID++;
	}
	
	public int getID() {
		return ID;
	}
}
