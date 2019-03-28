package com.vosure.chat.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class Server implements Runnable {

	private int port;
	private DatagramSocket socket;

	private ArrayList<ServerClient> clients = new ArrayList<>();

	private Thread run, manage, recieve, send;
	private boolean running = false;

	Server(int port) {
		this.port = port;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		}

		run = new Thread(this, "Server");
		run.start();

	}

	public void run() {
		running = true;
		System.out.println("Server running on port: " + port);
		manage();
		receive();
	}

	public void manage() {
		manage = new Thread("Manage") {
			public void run() {
				while (running) {
				}
			}
		};
		manage.start();
	}

	public void receive() {
		recieve = new Thread("Recieve") {
			public void run() {
				while (running) {
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
					processMessage(packet);
				}

			}
		};
		recieve.start();
	}

	private void send(byte[] data, InetAddress address, int port) {
		send = new Thread("Send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}

	private void sendToAll(String message) {
		for (int i = 0; i < clients.size(); i++) {
			ServerClient client = clients.get(i);
			send(message.getBytes(), client.address, client.port);
		}

	}

	private void processMessage(DatagramPacket packet) {
		String string = null;
		try {
			string = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (string.startsWith("/c/")) {
			clients.add(new ServerClient(string.substring(3, string.length()), packet.getAddress(), packet.getPort()));
			String ID = "/c/" + clients.get(clients.size() - 1).getID() + "/e/";
			send(ID.getBytes(), packet.getAddress(), packet.getPort());
		} else if (string.startsWith("/m/")) {
			sendToAll(string + "/e/");
		} else {
			System.out.println(string);
		}
	}
}
