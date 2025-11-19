package group3;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

public class Server {
	private static File logFile = new File("log.txt");
	private static File employeeFile = new File("employees.txt");
	private static File proFile = new File("profiles.txt"); 
	private static File accountFile = new File("accounts.txt");
	
	public static void main(String args[]) throws IOException {

		// make a server socket on the port number
		try (ServerSocket ss = new ServerSocket(777)) {

			// confirmation message
			System.out.println("Server is running ");

			// variable pool is a thread pool of 20 TODO: need more?
			var pool = Executors.newFixedThreadPool(20);

			// listening loop
			while (true) {

				// accept a connection on the socket
				var socket = ss.accept();

				// when there is a connection, the pool creates a new handler that uses socket
				// (current connection)
				pool.execute(new ClientHandler(socket, employeeFile, logFile, proFile, accountFile ));
				

			}
		}

	}
}

class ClientHandler implements Runnable {

	// has a socket attribute for the current connection
	private Socket socket;
	private static File logFile; 
	private static File employeeFile;
	private static File proFile;
	private static File accountFile;

	// constructor takes the socket connection
	public ClientHandler(Socket s, File employees, File log, File profiles, File accounts) {
		this.socket = s;
		this.logFile = log;
		//!
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

			// loop that listens for messages
			while (true) {

				// read new message
				msg = (Message) inputStream.readObject();

				// message status must be a request to get attention
				if (msg.getStatus() != MessageStatus.request) {
					// loop again
					continue;
				}
				// if message status is request
				else {
					// if type is undefined restart loop
					if(msg.getType() == MessageType.undefined) {
						continue;
					}
					
					
					
					
					
					
					// if message status is request and type is !undefined
					else {
						
						// check if it is from the ATM or Teller
						sender = msg.getSender();
						
						// if the ATM sent the message call ATM()
						if (sender == Application.ATM ) {
							ATM(msg);
							continue;
						}
						
						// if the Teller sent the message call Teller()
						else if (sender == Application.teller) {
							Teller(msg);
							continue;
						}
						
						// if sender is undefined continue
						else {
							continue;
						}						
					}		
				}
			}
		}

		catch(Exception e) {}

		}

	
	
	// handles ATM related messages
	private void ATM(Message msg){
		// store type
		MessageType type = msg.getType();
		
		// first message has to be login
		
		/*
		switch (type) {
	    case MessageType.login:
	        // code
	        break;
	    case MessageType.logout:
	        // code
	        break;
	    case MessageType.updateAccount:
	    	
	    case MessageType.updateProfile:
	    	
	    case MessageType.withdrawal:
	    	
	    case MessageType.deposit:
	    	
	    default:
	        // code
	    	
	    */
		
	}
	
	// handles Teller related messages
	private void Teller(Message msg) {
		
		// store type
		MessageType type = msg.getType();
		
		// employee login happens first
		// if type is not login ignore
		// check employees.txt for credentials
		
		
		
		/*
		switch (type) {
	    case MessageType.login:
	        // code
	        break;
	    case MessageType.logout:
	        // code
	        break;
	    case MessageType.updateAccount:
	    	
	    case MessageType.updateProfile:
	    	
	    case MessageType.withdrawal:
	    	
	    case MessageType.deposit:
	    	
	    default:
	        // code
		*/
	}
	

}
