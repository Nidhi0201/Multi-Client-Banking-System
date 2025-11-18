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
		try (ServerSocket ss = new ServerSocket(7777)) {

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

	// constructor takes the socket connection
	public ClientHandler(Socket s, File employees, File log, File profiles, File accounts) {
		this.socket = s;
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
						
						
						
					// first message will always be a login
						

					// store type
					type = msg.getType();
					
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
				}
						
						
					}

				}

				

			}

		} catch (Exception e) {

		}

	}
	
	// handles ATM related messages
	private void ATM(){
		
	}
	
	// handles Teller related messages
	private void Teller() {
		
	}
	
	//private void

}
