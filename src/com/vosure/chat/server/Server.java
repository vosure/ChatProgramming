package com.vosure.chat.server;

import java.net.DatagramSocket;
import java.net.SocketException;

public class Server implements Runnable {

	private int port;
	private DatagramSocket socket;

	private Thread run, manage, recieve;
	private boolean running = false;

	Server(int port) {
		this.port = port;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}

		run = new Thread(this, "Server");

	}

	public void run() {
		running = true;
		manage();
		recieve();
	}

	public void manage() {
		manage = new Thread("Manage") {
			public void run() {
				while (running) {
					System.out.println("...");
				}
			}
		};
		manage.start();
	}

	public void recieve() {
		recieve = new Thread("Recieve") {
			public void run() {
				while (running) {
					System.out.println("qwe");
				}
			}
		};
		recieve.start();
	}

}
