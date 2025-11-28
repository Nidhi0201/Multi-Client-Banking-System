package group3;

import java.io.*;
import java.net.Socket;
import javax.swing.JOptionPane;

public class ATM {
	// Message parameters for easy access
	static MessageStatus request = MessageStatus.request;
	static MessageType withdraw = MessageType.withdrawal;
	static MessageType deposit = MessageType.deposit;
	static MessageType logout = MessageType.logout;
	static MessageType customerLogin = MessageType.customerLogin;
	static Application ATMApp = Application.ATM;

	// Connection fields
	private Socket socket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private boolean connected = false;

	private Profile currentProfile;
	private Account currentAccount;

	public static void main(String args[]) throws IOException {
		// Prompt user for server IP
		String ip = (String) JOptionPane.showInputDialog(null, 
				"Enter Server IP Address", 
				"Connect to ATM Server",
				JOptionPane.QUESTION_MESSAGE, null, null, "127.0.0.1");


		if (ip == null) {
			return;
		}

		ATM atm = new ATM(ip);
	
		if (atm.isConnected()) {
			BankGUI gui = new BankGUI(atm, null);
		} else {
			JOptionPane.showMessageDialog(null, "Failed to connect to server.");
		}
	}

	// Constructor - establishes connection to server
	public ATM(String ip) {
		int port = 777;
		try {
			// Create socket connection
			socket = new Socket(ip, port);
			System.out.println("Connected to server at " + ip + ":" + port);

			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
			connected = true;

		} catch (IOException e) {
			System.out.println("Error connecting to server: " + e.getMessage());
			connected = false;
		}
	}

	// Check if connected to server
	public boolean isConnected() {
		return connected;
	}

	// Customer login with account number and PIN
	public boolean customerLogin(String accountNum, String pin) {
		if (!connected) {
			return false;
		}

		try {
			// Create login request message with account number and PIN
			String credentials = accountNum + "," + pin;
			Message loginMsg = new Message(request, customerLogin, ATMApp, accountNum, credentials);

			outputStream.writeObject(loginMsg);
			outputStream.flush();
			System.out.println("Login request sent for account: " + accountNum);

			Message response = (Message) inputStream.readObject();

			// Check if login was successful
			if (response.getStatus() == MessageStatus.confirmation) {
				System.out.println("Login successful");
				// TODO: Server should send back profile/account info
				return true;
			} else {
				System.out.println("Login failed: " + response.getText());
				return false;
			}

		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error during login: " + e.getMessage());
			return false;
		}
	}

	// Withdraw money from account
	public boolean withdraw(double amount) {
		if (!connected) {
			return false;
		}

		try {
			// Create withdrawal request message
			Message withdrawMsg = new Message(request, withdraw, ATMApp, null, String.valueOf(amount));
// Send withdrawal request
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

		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error during withdrawal: " + e.getMessage());
			return false;
		}
	}

	// Deposit money to account
	public boolean deposit(double amount) {
		if (!connected) {
			return false;
		}

		try {
			// Create deposit request message
			Message depositMsg = new Message(request, deposit, ATMApp, null, String.valueOf(amount));

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

		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error during deposit: " + e.getMessage());
			return false;
		}
	}

	// Check account balance
	public double checkBalance() {
		if (!connected) {
			return -1;
		}

		try {
			// Create balance inquiry message (using undefined type or could add a new MessageType)
			Message balanceMsg = new Message(request, MessageType.undefined, ATMApp, null, "balance");

			outputStream.writeObject(balanceMsg);
			outputStream.flush();

			Message response = (Message) inputStream.readObject();

			if (response.getStatus() == MessageStatus.confirmation) {
				return Double.parseDouble(response.getText());
			} else {
				System.out.println("Balance check failed: " + response.getText());
				return -1;
			}

		} catch (IOException | ClassNotFoundException | NumberFormatException e) {
			System.out.println("Error checking balance: " + e.getMessage());
			return -1;
		}
	}

	// Logout from ATM session
	public boolean logout() {
		if (!connected) {
			return false;
		}

		try {
			Message logoutMsg = new Message(request, logout, ATMApp, null, "logout");

			outputStream.writeObject(logoutMsg);
			outputStream.flush();
			System.out.println("Logout request sent");

			Message response = (Message) inputStream.readObject();

			if (response.getStatus() == MessageStatus.confirmation) {
				System.out.println("Logout successful");
				currentProfile = null;
				currentAccount = null;
				return true;
			} else {
				return false;
			}

		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error during logout: " + e.getMessage());
			return false;
		}
	}

	// Close connection to server
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

	// Getters
	public Profile getCurrentProfile() {
		return currentProfile;
	}

	public Account getCurrentAccount() {
		return currentAccount;
	}

	// Setters
	public void setCurrentProfile(Profile profile) {
		this.currentProfile = profile;
	}

	public void setCurrentAccount(Account account) {
		this.currentAccount = account;
	}
}
