package com.vosure.chat;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class ClientWindow extends JFrame implements Runnable {

	private JPanel contentPane;
	private JTextField messageField;
	private JTextArea history;
	private DefaultCaret caret;
	
	private Client client;
	
	private boolean running = false;
	
	Thread listen, run;
	private JMenuBar menuBar;
	private JMenu mnNewMenu;
	private JMenuItem mntmOnlineUsers;
	private JMenuItem mntmExit;
	
	private OnlineUsers users;

	public ClientWindow(String name, String address, int port) {

		client = new Client(name, address, port);

		if (!client.openConnection(address)) {
			System.err.println("Connection Failed");
			send("Connection Failed", true);
			return;
		}
		
		createWindow();
		String connectionInfo = "/c/" + name + "/e/";
		client.send(connectionInfo.getBytes());
		
		users = new OnlineUsers();

		running = true;
		run = new Thread(this, "Running");
		run.start();
	}

	private void createWindow() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		setTitle("HandmadeChat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(880, 550);
		setLocationRelativeTo(null);
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		mntmOnlineUsers = new JMenuItem("Online Users");
		mntmOnlineUsers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				users.setVisible(true);
			}
		});
		mnNewMenu.add(mntmOnlineUsers);
		
		mntmExit = new JMenuItem("Exit");
		mnNewMenu.add(mntmExit);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 28, 815, 30, 7 };
		gbl_contentPane.rowHeights = new int[] { 25, 485, 40 };
		contentPane.setLayout(gbl_contentPane);

		history = new JTextArea();
		history.setEditable(false);
		history.setFont(new Font("Times New Roman", Font.BOLD, 14));
		caret = (DefaultCaret) history.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scroll = new JScrollPane(history);
		GridBagConstraints scrollConstraints = new GridBagConstraints();
		scrollConstraints.insets = new Insets(0, 0, 5, 5);
		scrollConstraints.fill = GridBagConstraints.BOTH;
		scrollConstraints.gridx = 0;
		scrollConstraints.gridy = 0;
		scrollConstraints.gridwidth = 3;
		scrollConstraints.gridheight = 2;
		scrollConstraints.weightx = 1;
		scrollConstraints.weighty = 1;
		scrollConstraints.insets = new Insets(0,5,0,0);
		contentPane.add(scroll, scrollConstraints);

		messageField = new JTextField();
		messageField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_ENTER)
					send(messageField.getText(), true);
			}
		});
		GridBagConstraints gbc_textFieldMessage = new GridBagConstraints();
		gbc_textFieldMessage.insets = new Insets(0, 0, 0, 5);
		gbc_textFieldMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldMessage.gridx = 0;
		gbc_textFieldMessage.gridy = 2;
		gbc_textFieldMessage.gridwidth = 2;
		gbc_textFieldMessage.weightx = 1;
		gbc_textFieldMessage.weighty = 0;
		contentPane.add(messageField, gbc_textFieldMessage);
		messageField.setColumns(10);

		JButton buttonSend = new JButton("Send");
		buttonSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String message = messageField.getText();
				send(message, true);

			}
		});
		GridBagConstraints gbc_buttonSend = new GridBagConstraints();
		gbc_buttonSend.gridx = 2;
		gbc_buttonSend.gridy = 2;
		gbc_buttonSend.weightx = 0;
		gbc_buttonSend.weighty = 0;
		contentPane.add(buttonSend, gbc_buttonSend);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				String disconnect = "/d/" + client.getID() + "/e/";
				send(disconnect, false);
				running = false;
				client.close();
			}
		});
		
		setVisible(true);

		messageField.requestFocusInWindow();
	}

	private void printIntoChat(String message) {
		history.append(message + "\n\r");
	}
	
	private void send(String message, boolean broadcast) {
		if (message.equals(""))
			return;
		if (broadcast) {
			message = "/m/" + client.getName() + ": " + message;
			messageField.setText("");
		}
		client.send(message.getBytes());
	}
	
	private void listen() {
		listen = new Thread("Listen") {
			public void run() {
				while(running) {
					String message = client.receive();
					if (message.startsWith("/c/")) {
						client.setID(Integer.parseInt(message.split("/c/|/e/")[1]));
						printIntoChat("Successfully connected to server. ID: " + client.getID());
					}
					else if (message.startsWith("/m/")) {
						String text = message.substring(3).split("/e/")[0];
						printIntoChat(text);
					}
					else if (message.startsWith("/i/")) {
						String text = "/i/" + client.getID() + "/e/";
						send(text, false);
					}
					else if (message.startsWith("/u/")) {
						String[] u = message.split("/u/|/n/|/e/");
						//users.update(Arrays.copyOfRange(u, 1, u.length - 1));
						users.update(u);
					}
					else{
						
					}
				}
			}
		};
		listen.start();
	}
	
	public void run() {
		listen();
	}

}
