package com.vosure.chat;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;

	public Login() {
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

		textField = new JTextField();
		textField.setBounds(65, 36, 188, 27);
		contentPane.add(textField);
		textField.setColumns(10);

		JLabel lblName = new JLabel("Name:");
		lblName.setFont(new Font("Constantia", Font.BOLD | Font.ITALIC, 16));
		lblName.setBounds(129, 11, 61, 14);
		contentPane.add(lblName);

		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(65, 121, 188, 27);
		contentPane.add(textField_1);

		JLabel lblIp = new JLabel("IP - Address:");
		lblIp.setFont(new Font("Constantia", Font.BOLD | Font.ITALIC, 16));
		lblIp.setBounds(111, 96, 97, 14);
		contentPane.add(lblIp);

		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(65, 210, 188, 27);
		contentPane.add(textField_2);

		JLabel lblPort = new JLabel("Port:");
		lblPort.setFont(new Font("Constantia", Font.BOLD | Font.ITALIC, 16));
		lblPort.setBounds(129, 185, 61, 14);
		contentPane.add(lblPort);

		JButton btnNewButton = new JButton("Login");
		btnNewButton.setFont(new Font("Century Schoolbook", Font.BOLD, 13));
		btnNewButton.setBounds(78, 285, 163, 27);
		contentPane.add(btnNewButton);
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
