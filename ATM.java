package group3;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ATM {
	// easy access message parameters
	static MessageStatus request = MessageStatus.request;
	static MessageType withdraw = MessageType.withdrawal;
	static MessageType deposit = MessageType.deposit;
	static MessageType logout = MessageType.logout;
	static Application ATM = Application.ATM;
	
	public static void main(String args[]) throws IOException{
		// get user ip
		String ip = InetAddress.getLocalHost().getHostAddress(); // TODO get a real ip
		int port = 777;
		Scanner scan = new Scanner(System.in); // temporary, we will use GUI for input
		
		// Socket that connects to the ip and port
		Socket socket = new Socket(ip, port);
		System.out.println("Connected to " + ip + ":" + port);
				
		// create the object input and output streams on socket
		ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream()); 
	
		System.out.println("Type something: "); 
		String something = scan.nextLine();
		Message msg = new Message(request, null, ATM, null, something); 
		
		outputStream.writeObject(msg);
		System.out.println("Message sent"); 
		
	}

}
