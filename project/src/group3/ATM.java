package group3;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

public class ATM {
	// easy access message parameters
	static MessageType login = MessageType.customerLogin;
	static MessageType withdraw = MessageType.withdrawal;
	static MessageType deposit = MessageType.deposit;
	static MessageType logout = MessageType.logout;
	static MessageStatus request = MessageStatus.request;
	static Application atmApp = Application.ATM;

	// connection fields
	private Socket socket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private boolean connected = false;

	// current session state
	private String currentAccountNum;
	private Profile currentProfile;
	private Account currentAccount;

	public static void main(String args[]) throws IOException {
		// prompt for an IP address; default will be 127.0.0.1
		String ip = (String) JOptionPane.showInputDialog(null, "Enter IP Address", "Connect to a Server",
				JOptionPane.QUESTION_MESSAGE, null, null, "127.0.0.1");

		// if user presses Cancel or X quit the main
		if (ip == null) {
			return;
		}

		ATM atm = new ATM(ip);

		if (atm.isConnected()) {
			BankGUI gui = new BankGUI(atm, null);
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

	// constructor - establishes connection to server
	public ATM(String ip) {
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

	// check if connected to server
	public boolean isConnected() {
		return connected;
	}

	// customer login with account number and PIN
	public boolean customerLogin(String accountNum, String pin) {
		if (!connected) {
			return false;
		}

		try {
			// create login request message with account number and PIN
			String credentials = accountNum + "," + pin;
			// Message.number is stored as String; server can parseInt if needed
			Message loginMsg = new Message(request, login, atmApp, accountNum, credentials);

			outputStream.writeObject(loginMsg);
			outputStream.flush();
			System.out.println("ATM login request sent for account: " + accountNum);

			Message response = (Message) inputStream.readObject();

			// check if login was successful
			if (response.getStatus() == MessageStatus.confirmation) {
				System.out.println("ATM login successful");
				currentAccountNum = accountNum;
				return true;
			} else {
				System.out.println("ATM login failed: " + response.getText());
				return false;
			}

		} catch (NumberFormatException e) {
			System.out.println("Invalid account number format: " + accountNum);
			return false;
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error during ATM login: " + e.getMessage());
			return false;
		}
	}

	// check balance
	public double checkBalance() {
		if (!connected || currentAccountNum == null) {
			return -1.0;
		}

		try {
			Message balanceMsg = new Message(request, MessageType.undefined, atmApp, currentAccountNum, "balance");

			outputStream.writeObject(balanceMsg);
			outputStream.flush();
			System.out.println("Balance request sent");

			Message response = (Message) inputStream.readObject();

			if (response.getStatus() == MessageStatus.confirmation) {
				// balance should be in response text
				try {
					double balance = Double.parseDouble(response.getText());
					return balance;
				} catch (NumberFormatException e) {
					return -1.0;
				}
			} else {
				System.out.println("Balance check failed: " + response.getText());
				return -1.0;
			}

		} catch (NumberFormatException e) {
			System.out.println("Invalid account number format");
			return -1.0;
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error during balance check: " + e.getMessage());
			return -1.0;
		}
	}

	// withdraw money from account
	public boolean withdraw(double amount) {
		if (!connected || currentAccountNum == null) {
			return false;
		}

		try {
			Message withdrawMsg = new Message(request, withdraw, atmApp, currentAccountNum, String.valueOf(amount));

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
			System.out.println("Invalid account number format");
			return false;
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error during withdrawal: " + e.getMessage());
			return false;
		}
	}

	// deposit money to account
	public boolean deposit(double amount) {
		if (!connected || currentAccountNum == null) {
			return false;
		}

		try {
			Message depositMsg = new Message(request, deposit, atmApp, currentAccountNum, String.valueOf(amount));

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
			System.out.println("Invalid account number format");
			return false;
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error during deposit: " + e.getMessage());
			return false;
		}
	}

	// logout from ATM session
	public boolean logout() {
		if (!connected) {
			return false;
		}

		try {
			// use current account number if available, otherwise "0" (logout doesn't require account number)
			String num = (currentAccountNum != null) ? currentAccountNum : "0";
			Message logoutMsg = new Message(request, logout, atmApp, num, "logout");

			outputStream.writeObject(logoutMsg);
			outputStream.flush();
			System.out.println("Logout request sent");

			Message response = (Message) inputStream.readObject();

			if (response.getStatus() == MessageStatus.confirmation) {
				System.out.println("Logout successful");
				currentAccountNum = null;
				return true;
			} else {
				return false;
			}

		} catch (NumberFormatException e) {
			System.out.println("Invalid account number format");
			return false;
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error during logout: " + e.getMessage());
			return false;
		}
	}

	// close connection to server
	public void disconnect() {
		try {
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
			if (socket != null) {
				socket.close();
			}
			connected = false;
			currentAccountNum = null;
			currentProfile = null;
			currentAccount = null;
			System.out.println("Disconnected from server");
		} catch (IOException e) {
			System.out.println("Error disconnecting: " + e.getMessage());
		}
	}

	// additional getters/setters used in tests
	public Profile getCurrentProfile() {
		return currentProfile;
	}

	public void setCurrentProfile(Profile profile) {
		this.currentProfile = profile;
	}

	public Account getCurrentAccount() {
		return currentAccount;
	}

	public void setCurrentAccount(Account account) {
		this.currentAccount = account;
	}
}
