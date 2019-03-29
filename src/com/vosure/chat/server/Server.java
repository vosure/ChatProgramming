package com.vosure.chat.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;

public class Server implements Runnable {

	private int port;
	private DatagramSocket socket;

	private ArrayList<ServerClient> clients = new ArrayList<>();
	private ArrayList<Integer> clientResponse = new ArrayList<>();

	private Thread run, manage, recieve, send;
	private boolean running = false;

	private static final int MAX_ATTEMPTS = 5;

	private boolean raw = false;

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
		Scanner scanner = new Scanner(System.in);
		while (running) {
			String text = scanner.nextLine();
			if (!text.startsWith("/")) {
				sendToAll("/m/Server: " + text + "/e/");
				continue;
			}
			text = text.substring(1);
			if (text.equals("raw")) {
				raw = !raw;
			} else if (text.equals("clients")) {
				System.out.println("Online Clients: ");
				ServerClient client;
				for (int i = 0; i < clients.size(); i++) {
					client = clients.get(i);
					System.out.println(client.name + " (" + client.getID() + ") " + client.address + " " + client.port);
				}
			} else if (text.equals("kick")) {
				String name = text.split(" ")[1];
				int ID = -1;
				boolean isNumber = false;
				try {
					ID = Integer.parseInt(name);
					isNumber = true;
				} catch (NumberFormatException e) {
				}
				if (isNumber) {
					boolean isExist = false;
					for (int i = 0; i < clients.size(); i++) {
						if (clients.get(i).getID() == ID) {
							isExist = true;
							break;
						}
					}
					if (isExist) {
						disconnect(ID, true);
					} else
						System.out.println("Client with ID - " + ID + " does not exist");
				} else {
					for (int i = 0; i < clients.size(); i++) {
						ServerClient client = clients.get(i);
						if (name.equals(client.name)) {
							disconnect(client.getID(), true); // NOTE(vosure) What if this name doesn't exist?
																// TODO(vosure) Check it

						}
					}
				}
			} else if (text.equals("quit")) {
				quit();
			} else {
				System.out.println("Unknown command");
			}
		}
	}

	public void manage() {
		manage = new Thread("Manage") {
			public void run() {
				while (running) {
					sendToAll("/i/server");
					sendStatus();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					for (int i = 0; i < clients.size(); i++) {
						ServerClient client1 = clients.get(i);
						if (!clientResponse.contains(client1.getID())) {
							if (client1.attempt >= MAX_ATTEMPTS) {
								disconnect(client1.getID(), false);
							} else {
								client1.attempt++;
							}
						} else {
							clientResponse.remove(new Integer(client1.getID()));
							client1.attempt = 0;
						}
					}
				}
			}
		};
		manage.start();
	}

	private void sendStatus() {
		if (clients.size() <= 0) return;
		String users = "/u/";
		for (int i = 0; i < clients.size() - 1; i++) {
			users += clients.get(i).name + "/n/";
		}
		users += clients.get(clients.size() - 1).name + "/e/";
		System.out.println(users);
		sendToAll(users);
	}

	public void receive() {
		recieve = new Thread("Recieve") {
			public void run() {
				while (running) {
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
					} catch (SocketException e) {
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
		if (message.startsWith("/m/")) {
			String text = message.substring(3);
			text = text.split("/e/")[0];
			System.out.println(text);
		}

		for (int i = 0; i < clients.size(); i++) {
			ServerClient client = clients.get(i);
			send(message.getBytes(), client.address, client.port);
		}

	}

	private void processMessage(DatagramPacket packet) {
		String string = new String(packet.getData()).trim();
		if (raw)
			System.out.println(string);
		if (string.startsWith("/c/")) {
			String name = string.split("/c/|/e/")[1];
			System.out.println(name + " connected!");
			int id = UniqueID.getIdentifier();
			clients.add(new ServerClient(name, packet.getAddress(), packet.getPort(), id));
			String ID = "/c/" + id + "/e/";
			send(ID.getBytes(), packet.getAddress(), packet.getPort());
		} else if (string.startsWith("/m/")) {
			sendToAll(string);
		} else if (string.startsWith("/d/")) {
			disconnect(Integer.parseInt(string.split("/d/|/e/")[1]), true);
		} else if (string.startsWith("/i/")) {
			clientResponse.add(Integer.parseInt(string.split("/i/|/e/")[1]));
		} else {

		}
	}

	private void disconnect(int ID, boolean status) {
		ServerClient client = null;
		boolean exists = false;
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getID() == ID) {
				client = clients.get(i);
				clients.remove(i);
				exists = true;
				break;
			}
		}
		if (!exists)
			return;
		if (status)
			System.out.println("Client " + client.name + " disconnected");
		else
			System.out.println("Client " + client.name + " timed out");
	}

	private void quit() {
		for (int i = 0; i < clients.size(); i++) {
			disconnect(clients.get(i).getID(), true);
		}
		running = false;
		socket.close();
	}

}
