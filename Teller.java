package group3;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Teller {
	// easy access message parameters
		static MessageStatus request = MessageStatus.request;
		static MessageType withdraw = MessageType.withdrawal;
		static MessageType deposit = MessageType.deposit;
		static MessageType login = MessageType.login;
		static MessageType logout = MessageType.logout;
		static MessageType updateAcc = MessageType.updateAccount;
		static MessageType updateProf = MessageType.updateProfile;
	
	public static void main(String args[]) throws IOException{
		// get user ip
		String ip = InetAddress.getLocalHost().getHostAddress();
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
