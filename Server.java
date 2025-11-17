package group3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;


public class Server {
	public static void main(String args[]) throws IOException{
		
		// make a server socket on the port number
		try (var ss = new ServerSocket(7777)) {
			
			// confirmation message
			System.out.println("Server is running ");

			// variable pool is a thread pool of 20 TODO: need more?
			var pool = Executors.newFixedThreadPool(20);

			// listening loop
			while (true) {

				// accept a connection on the socket
				var socket = ss.accept();

				// when there is a connection, the pool creates a new handler that uses socket (current connection)
				pool.execute(new ClientHandler(socket));
	


			}
		}

	}
}


class ClientHandler implements Runnable {

	// has a socket attribute for the current connection
	private Socket socket;

	// constructor takes the socket connection
	public ClientHandler(Socket s) {
		this.socket = s;
	}

	@Override
	public void run() {
		
		try (   // create the object input and output streams on socket
				var s = socket;
				ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());) {
			
			// server side functionality
			
			
		}
		catch(Exception e){
			
		}
		
		
	}
	
}
