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
		Scanner scan = new Scanner(System.in); // temporary, we will use GUI for input
	
		
	public static void main(String args[]) throws IOException{
		// get user ip
		String ip = InetAddress.getLocalHost().getHostAddress();
		int port = 777;
		
	
		// Socket that connects to the ip and port
		Socket socket = new Socket(ip, port);
		System.out.println("Connected to " + ip + ":" + port);
						
		// create the object input and output streams on socket
		ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
	
	
		
	
	
	}
	
	public void start() {
		
		// welcome
		System.out.println("Welcome Employee\n---------------- ");
		
		// enter employee login
		System.out.println("username: ");
		String username = scan.nextLine();
		System.out.println("password: ");
		String password = scan.nextLine();
		
		// after employee authenticates
		if (employeeLogin(username, password)) {
			
			// welcome 
			System.out.println("Welcome Customer\\n---------------- ");
			System.out.println("username: ");
			username = scan.nextLine();
			System.out.println("password: ");
			password = scan.nextLine();
			
			Profile profile = findProfile(username,password);
			
			// after customer authenticates
			if (profile != null) {
				
				// "Welcome " + profile.getName()
				System.out.println("Welcome " + profile.getName());
			
			}
			// customer authentication error
			else {
				
			}
		}
		// employee authentication error
		else {
			
			// increment fraud counter
			// start over
		}	
			
	
		
		
		
		
	}
	
	// checks with server and returns profile if user information
	public Boolean employeeLogin(String user, String pass) {
		boolean valid = false;
		
		// create + send request message to teller login with user and pass
		
		// listen for confirm message
		
		// if status == confirmation valid = true return valid
		
		// else 
		return valid;
		
	}
	
	
	// authenticates user login info and returns null if invalid
	public Profile findProfile(String user, String pass) {
		Profile profile = null;
		
		return profile;
		
	}
	
	
}
