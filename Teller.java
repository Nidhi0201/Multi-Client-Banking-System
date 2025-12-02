package group3;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.JOptionPane;

public class Teller {
	//Easy access message parameters
	static MessageType employeeLogin = MessageType.employeeLogin;
	static MessageType customerLogin = MessageType.customerLogin;
  static MessageStatus request = MessageStatus.request;
	static MessageType withdraw = MessageType.withdrawal;
	static MessageType deposit = MessageType.deposit;
	static MessageType logout = MessageType.logout;
	static MessageType updateAcc = MessageType.updateAccount;
	static MessageType updateProf = MessageType.updateProfile;
	static MessageStatus request = MessageStatus.request;
	static Application tellerApp = Application.teller;

	//Connection fields
	private Socket socket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private boolean connected = false;

	private Profile currentProfile;

	public static void main(String args[]) throws IOException {
		//prompt for an IP address; default will be 127.0.0.1
		String ip = (String) JOptionPane.showInputDialog(null, "Enter IP Address", "Connect to a Server",
				JOptionPane.QUESTION_MESSAGE, null, null, "127.0.0.1");

		//if user presses Cancel or X quit the main
		if (ip == null) {
			return;
		}

		Teller teller = new Teller(ip);

		if (teller.isConnected()) {
			BankGUI gui = new BankGUI(null, teller);
		} else {
			JOptionPane.showMessageDialog(null, 
				"Failed to connect to server at " + ip + ":7777\n\n" +
				"Please make sure:\n" +
				"1. The server is running (run Server.java)\n" +
				"2. The IP address is correct\n" +
				"3. No firewall is blocking the connection", 
				"Connection Failed", 
				JOptionPane.ERROR_MESSAGE);
		}
	}

	//constructor - establishes connection to server
	public Teller(String ip) {
		int port = 7777;
		try {
			socket = new Socket(ip, port);
			System.out.println("Connected to server at " + ip + ":" + port);

			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
			connected = true;

		} catch (IOException e) {
			System.out.println("Error connecting to server: " + e.getMessage());
			e.printStackTrace();
			connected = false;
		}
	}

	//check if connected to server
	public boolean isConnected() {
		return connected;
	}

	//employee login with username and password
	public boolean employeeLogin(String user, String pass) {
		if (!connected) {
			return false;
		}

		try {
			//create login request message with username and password
			String credentials = user + "," + pass;
			Message loginMsg = new Message(request, employeeLogin, tellerApp, 0, credentials);

			outputStream.writeObject(loginMsg);
			outputStream.flush();
			System.out.println("Employee login request sent for: " + user);

			Message response = (Message) inputStream.readObject();

			// check if login was successful
			if (response.getStatus() == MessageStatus.confirmation) {
				System.out.println("Employee login successful");
				return true;
			} else {
				System.out.println("Employee login failed: " + response.getText());
				return false;
			}

		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error during employee login: " + e.getMessage());
			return false;
		}
	}

	//customer login - returns Profile or null if invalid
	public Profile customerLogin(String user, String pass) {
		if (!connected) {
			return null;
		}

		try {
			//Create login request message with username and password
			String credentials = user + "," + pass;
			Message loginMsg = new Message(request, customerLogin, tellerApp, 0, credentials);

			outputStream.writeObject(loginMsg);
			outputStream.flush();
			System.out.println("Customer login request sent for: " + user);

			Message response = (Message) inputStream.readObject();

			//check if login was successful
			if (response.getStatus() == MessageStatus.confirmation) {
				System.out.println("Customer login successful");
				//receive profile object from server
				currentProfile = (Profile) inputStream.readObject();
				return currentProfile;
			} else {
				System.out.println("Customer login failed: " + response.getText());
				return null;
			}

		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error during customer login: " + e.getMessage());
			return null;
		}
	}

	//withdraw money from account
	public boolean withdraw(String accountNum, double amount) {
		if (!connected) {
			return false;
		}

		try {
			int accountNumInt = Integer.parseInt(accountNum);
			Message withdrawMsg = new Message(request, withdraw, tellerApp, accountNumInt, String.valueOf(amount));

			outputStream.writeObject(withdrawMsg);
			outputStream.flush();
			System.out.println("Withdrawal request sent: $" + amount);

			Message response = (Message) inputStream.readObject();

			if (response.getStatus() == MessageStatus.confirmation) {
				System.out.println("Withdrawal successful");
				return true;
			} else {
				System.out.println("Withdrawal failed: " + response.getText());
				return false;
			}

		} catch (NumberFormatException e) {
			System.out.println("Invalid account number format: " + accountNum);
			return false;
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error during withdrawal: " + e.getMessage());
			return false;
		}
	}

	//deposit money to account
	public boolean deposit(String accountNum, double amount) {
		if (!connected) {
			return false;
		}

		try {
			int accountNumInt = Integer.parseInt(accountNum);
			Message depositMsg = new Message(request, deposit, tellerApp, accountNumInt, String.valueOf(amount));

			outputStream.writeObject(depositMsg);
			outputStream.flush();
			System.out.println("Deposit request sent: $" + amount);

			Message response = (Message) inputStream.readObject();

			if (response.getStatus() == MessageStatus.confirmation) {
				System.out.println("Deposit successful");
				return true;
			} else {
				System.out.println("Deposit failed: " + response.getText());
				return false;
			}

		} catch (NumberFormatException e) {
			System.out.println("Invalid account number format: " + accountNum);
			return false;
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error during deposit: " + e.getMessage());
			return false;
		}
	}

	//update account info
	public boolean updateAccount(String accountNum, String updateData) {
		if (!connected) {
			return false;
		}

		try {
			int accountNumInt = Integer.parseInt(accountNum);
			Message updateMsg = new Message(request, updateAcc, tellerApp, accountNumInt, updateData);

			outputStream.writeObject(updateMsg);
			outputStream.flush();
			System.out.println("Update account request sent");

			Message response = (Message) inputStream.readObject();

			if (response.getStatus() == MessageStatus.confirmation) {
				System.out.println("Account update successful");
				return true;
			} else {
				System.out.println("Account update failed: " + response.getText());
				return false;
			}

		} catch (NumberFormatException e) {
			System.out.println("Invalid account number format: " + accountNum);
			return false;
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error during account update: " + e.getMessage());
			return false;
		}
	}

	//create a new customer profile
	public boolean createProfile(String name, String username, String password, long phone, String address, String email) {
		if (!connected) {
			return false;
		}

		try {
			//format profile data as comma-separated string: name,username,password,phone,address,email
			String profileData = name + "," + username + "," + password + "," + phone + "," + address + "," + email;
			Message createMsg = new Message(request, updateProf, tellerApp, 0, "CREATE:" + profileData);

			outputStream.writeObject(createMsg);
			outputStream.flush();
			System.out.println("Create profile request sent for: " + username);

			Message response = (Message) inputStream.readObject();

			if (response.getStatus() == MessageStatus.confirmation) {
				System.out.println("Profile created successfully");
				return true;
			} else {
				System.out.println("Profile creation failed: " + response.getText());
				return false;
			}

		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error during profile creation: " + e.getMessage());
			return false;
		}
	}

	//update profile info
	public boolean updateProfile(String updateData) {
		if (!connected) {
			return false;
		}

		try {
			Message updateMsg = new Message(request, updateProf, tellerApp, 0, updateData);

			outputStream.writeObject(updateMsg);
			outputStream.flush();
			System.out.println("Update profile request sent");

			Message response = (Message) inputStream.readObject();

			if (response.getStatus() == MessageStatus.confirmation) {
				System.out.println("Profile update successful");
				return true;
			} else {
				System.out.println("Profile update failed: " + response.getText());
				return false;
			}

		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error during profile update: " + e.getMessage());
			return false;
		}
	}

	//logout from teller session
	public boolean logout() {
		if (!connected) {
			return false;
		}

		try {
			Message logoutMsg = new Message(request, logout, tellerApp, 0, "logout");

			outputStream.writeObject(logoutMsg);
			outputStream.flush();
			System.out.println("Logout request sent");

			Message response = (Message) inputStream.readObject();

			if (response.getStatus() == MessageStatus.confirmation) {
				System.out.println("Logout successful");
				currentProfile = null;
				return true;
			} else {
				return false;
			}

		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error during logout: " + e.getMessage());
			return false;
		}
	}

	//close connection to server
	public void disconnect() {
		try {
			if (inputStream != null) inputStream.close();
			if (outputStream != null) outputStream.close();
			if (socket != null) socket.close();
			connected = false;
			System.out.println("Disconnected from server");
		} catch (IOException e) {
			System.out.println("Error disconnecting: " + e.getMessage());
		}
	}

	//getters
	public Profile getCurrentProfile() {
		return currentProfile;
	}

	//setters
	public void setCurrentProfile(Profile profile) {
		this.currentProfile = profile;
	}

	// checks with server and returns Profile or null if DNE
	public Profile customerLogin(String user, String pass) {
		Profile profile = null;

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
