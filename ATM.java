package group3;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ATM {
	public static void main(String args[]) throws IOException{
		String ip = "";
		int port = 777;
		Scanner scan = new Scanner(System.in); // temporary, we will use GUI for input
	
		// Socket that connects to the ip and port
		Socket socket = new Socket(ip, port);
		System.out.println("Connected to " + ip + ":" + port);
				
		// create the object input and output streams on socket
		ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream()); 
	}

}
