package group3;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

public class Server {
	private static File employeeFile = new File("employees.txt"); 	// username,password\n
	private static File logFile = new File("log.txt"); 				// accountNum,type,message,date\n
	private static File proFile = new File("profiles.txt"); 		// username,password,name,phone,address,email,creditScore,[num,num,num]\n
	// for access to one account at a time by the ATM
	private static File accountFile = new File("accounts.txt"); 	// number,pin,type,balance,initialBalance

	public static void main(String args[]) throws IOException {

		// make a server socket on the port number
		try (ServerSocket ss = new ServerSocket(777)) {

			// confirmation message
			JOptionPane.showMessageDialog(null, "Server is running");

			// variable pool is a thread pool of 20 TODO: need more?
			var pool = Executors.newFixedThreadPool(20);

			// listening loop
			while (true) {

				// accept a connection on the socket
				var socket = ss.accept();

				// when there is a connection, the pool creates a new handler that uses socket
				pool.execute(new ClientHandler(socket, employeeFile, logFile, proFile, accountFile));

			}
		}

	}
}

class ClientHandler implements Runnable {

	// has a socket attribute for the current connection + file references
	private Socket socket;
	private static File logFile;
	private static File employeeFile;
	private static File proFile;
	private static File accountFile;

	// constructor takes the socket connection
	public ClientHandler(Socket s, File employees, File log, File profiles, File accounts) {
		socket = s;
		logFile = log;
		employeeFile = employees;
		proFile = profiles;
		accountFile = accounts;
	}

	@Override
	public void run() {

		try ( // create the object input and output streams on socket
				var s = socket;
				ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());) {

			// create variables outside of loop
			Message msg = null;
			MessageType type = MessageType.undefined;
			Application sender = Application.undefined;
			Boolean loggedIn = false;

			JOptionPane.showMessageDialog(null,"New Connection"); // TODO remove

			// loop that listens for messages
			while (true) {

				// read new message
				msg = (Message) inputStream.readObject();

				// if the message comes from an ATM
				if (msg.getSender() == Application.ATM) {
					ATM(msg);
				}
				// if message comes from a Teller
				else if (msg.getSender() == Application.teller) {
					continue;
				}
				// if undefined listen for another message
				else {
					continue;
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e);
		}

	}

	private String[] parse(File file) {
		try {
			Scanner scan = new Scanner(file);
			String data = "";

			// read file into the String
			while (scan.hasNextLine()) {
				data += scan.nextLine() + "\n";
			}
			// done with scanner
			scan.close();

			// if file is empty return null
			if (data.equals("")) {
				return null;
			}

			// split on newline
			return data.split("\n");

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e);
			return null;
		}

	}

	// handles ATM related messages
	private void ATM(Message msg) {
		// parse files needed
		String[] accounts = parse(accountFile);
		
		
		// store type
		MessageType type = msg.getType();
		System.out.println("Client said: " + msg.getText()); // TODO remove

		// if the message isn't login get a new one
		if (msg.getType() != MessageType.customerLogin) {
			return;
		}

		/*
		 * switch (type) { case MessageType.login: // code break; case
		 * MessageType.logout: // code break; case MessageType.updateAccount:
		 * 
		 * case MessageType.updateProfile:
		 * 
		 * case MessageType.withdrawal:
		 * 
		 * case MessageType.deposit:
		 * 
		 * default: // code
		 * 
		 */

	}

	// handles Teller related messages
	private void Teller(Message msg) {
		// parse files needed
		String[] employees = parse(employeeFile);
		String[] profiles = parse(proFile);
		String[] log = parse(logFile);
		String[] accounts = parse(accountFile);
		
		
		
		// store type
		MessageType type = msg.getType();

		// employee login happens first
		// if type is not login ignore
		// check employees.txt for credentials

		/*
		 * switch (type) { case MessageType.login: // code break; case
		 * MessageType.logout: // code break; case MessageType.updateAccount:
		 * 
		 * case MessageType.updateProfile:
		 * 
		 * case MessageType.withdrawal:
		 * 
		 * case MessageType.deposit:
		 * 
		 * default: // code
		 */
		
		// create profile object when customer logs in
	}

}
