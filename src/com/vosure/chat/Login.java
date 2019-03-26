package com.vosure.chat;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField textName;
	private JTextField textIpAddress;
	private JTextField textPort;

	public Login() {
		setTitle("Login");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		setBackground(Color.WHITE);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(325, 383);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		textName = new JTextField();
		textName.setBounds(65, 36, 188, 27);
		contentPane.add(textName);
		textName.setColumns(10);

		JLabel labelName = new JLabel("Name:");
		labelName.setBounds(129, 21, 61, 14);
		labelName.setFont(new Font("Constantia", Font.BOLD | Font.ITALIC, 16));
		contentPane.add(labelName);

		textIpAddress = new JTextField();
		textIpAddress.setBounds(65, 121, 188, 27);
		textIpAddress.setColumns(10);
		contentPane.add(textIpAddress);

		JLabel labelIpAddress = new JLabel("IP Address:");
		labelIpAddress.setBounds(117, 104, 97, 14);
		labelIpAddress.setFont(new Font("Constantia", Font.BOLD | Font.ITALIC, 16));
		contentPane.add(labelIpAddress);

		textPort = new JTextField();
		textPort.setBounds(65, 203, 188, 27);
		textPort.setColumns(10);
		contentPane.add(textPort);

		JLabel labelPort = new JLabel("Port:");
		labelPort.setBounds(129, 186, 61, 14);
		labelPort.setFont(new Font("Constantia", Font.BOLD | Font.ITALIC, 16));
		contentPane.add(labelPort);

		JButton buttonLogin = new JButton("Login");
		buttonLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				login(textName.getText(), textIpAddress.getText(), Integer.parseInt(textPort.getText()));
			}

		});
		buttonLogin.setBounds(10, 316, 299, 27);
		buttonLogin.setFont(new Font("Century Schoolbook", Font.BOLD, 13));
		contentPane.add(buttonLogin);
	}

	private void login(String name, String address, int port) {
		dispose();
		new Client(name, address, port);
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
