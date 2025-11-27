package group3;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class Teller {
	// easy access message parameters
	static MessageType employeelogin = MessageType.employeeLogin;
	static MessageStatus request = MessageStatus.request;
	static MessageType withdraw = MessageType.withdrawal;
	static MessageType deposit = MessageType.deposit;
	static MessageType logout = MessageType.logout;
	static MessageType updateAcc = MessageType.updateAccount;
	static MessageType updateProf = MessageType.updateProfile;
	static MessageType customerlogin = MessageType.customerLogin;

	public static void main(String args[]) throws IOException {
		String ip = (String) JOptionPane.showInputDialog(null, "Enter IP Address", "Connect to a Server",
				JOptionPane.QUESTION_MESSAGE, null, null, "127.0.0.1");

		// if user presses Cancel or X quit the main
		if (ip == null) {
			return;
		}

		Teller teller = new Teller(ip);
		BankGUI gui = new BankGUI(null, teller);
	}

	public Teller(String ip) throws IOException {
		int port = 777;
		try (
				// Socket that connects to the ip and port
				Socket socket = new Socket(ip, port);

				// create the object input and output streams on socket
				ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());) {
		}
	}

	public void start() {

		
	}

	// checks with server and returns bool
	public Boolean employeeLogin(String user, String pass) {
		boolean valid = true; // TODO revert to false

		// create + send request message to teller login with user and pass

		// listen for confirm message

		// if status == confirmation valid = true return valid

		// else
		return valid;

	}

	// checks with server and returns Profile or null if DNE
	public Profile customerLogin(String user, String pass) {
		Profile profile = null; // TODO revert to false

		// create + send request message to teller login with user and pass

		// listen for confirm message

		// if status == confirmation 
		// {valid = true; listen for profile object; return valid}
		

		// else
		return profile;

	}

	// checks file for profile info returns null if invalid
	public Profile findProfile(String user, String pass) {
		Profile profile = null;

		return profile;

	}

}
