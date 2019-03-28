package com.vosure.chat;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class ClientWindow extends JFrame implements Runnable {

	private JPanel contentPane;
	private JTextField messageField;
	private JTextArea history;
	private DefaultCaret caret;
	
	private Client client;
	
	private boolean running = false;
	
	Thread listen, run;

	public ClientWindow(String name, String address, int port) {

		client = new Client(name, address, port);

		if (!client.openConnection(address)) {
			System.err.println("Connection Failed");
			send("Connection Failed");
			return;
		}
		
		createWindow();
		String connectionInfo = "/c/" + name;
		client.send(connectionInfo.getBytes());
		
		printIntoChat("Привет как дела");
		
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
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 28, 815, 30, 7 };
		gbl_contentPane.rowHeights = new int[] { 35, 475, 40 };
		gbl_contentPane.columnWeights = new double[] { 1.0, 1.0 };
		gbl_contentPane.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
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
		contentPane.add(scroll, scrollConstraints);

		messageField = new JTextField();
		messageField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_ENTER)
					send(messageField.getText());
			}
		});
		GridBagConstraints gbc_textFieldMessage = new GridBagConstraints();
		gbc_textFieldMessage.insets = new Insets(0, 0, 0, 5);
		gbc_textFieldMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldMessage.gridx = 0;
		gbc_textFieldMessage.gridy = 2;
		gbc_textFieldMessage.gridwidth = 2;
		contentPane.add(messageField, gbc_textFieldMessage);
		messageField.setColumns(10);

		JButton buttonSend = new JButton("Send");
		buttonSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String message = messageField.getText();
				send(message);

			}
		});
		GridBagConstraints gbc_buttonSend = new GridBagConstraints();
		gbc_buttonSend.gridx = 2;
		gbc_buttonSend.gridy = 2;
		contentPane.add(buttonSend, gbc_buttonSend);

		setVisible(true);

		messageField.requestFocusInWindow();
	}

	private void printIntoChat(String message) {
		history.append(message + "\n\r");
	}
	
	private void send(String message) {
		if (message.equals(""))
			return;
		message = "/m/" + client.getName() + ": " + message;
		client.send(message.getBytes());
		messageField.setText("");
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
						String text = message.split("/m/|/e/")[1];
						printIntoChat(text);
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
