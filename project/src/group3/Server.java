package group3;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;


public class Server {
	private static File logFile = new File("src/group3/log.txt");
	private static File employeeFile = new File("src/group3/employees.txt");
	private static File proFile = new File("src/group3/profiles.txt");
	// for access to one account at a time by the ATM
	private static File accountFile = new File("src/group3/accounts.txt");

	public static void main(String args[]) throws IOException {

		// make a server socket on the port number
		try (ServerSocket ss = new ServerSocket(7777)) {

			// confirmation message
			System.out.println("Server is running ");

			var pool = Executors.newFixedThreadPool(20);

			// listening loop
			while (true) {

				// accept a connection on the socket
				var socket = ss.accept();

				// when there is a connection, the pool creates a new handler that uses socket
				pool.execute(new ClientHandler(socket, employeeFile, logFile, proFile, accountFile));
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

	// Stream references for sending responses
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;

	// ATM session tracking
	private boolean atmLoggedIn = false;
	private String currentAccountNum = null;
	private double currentBalance = 0.0;

	// constructor takes the socket connection
	public ClientHandler(Socket s, File employees, File log, File profiles, File accounts) {
		socket = s;
		logFile = log;
		employeeFile = employees;
		proFile = profiles;
		accountFile = accounts;
	}

	@Override
	public void run() {

		try ( // create the object input and output streams on socket
				Socket s = socket;
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());) {

			// Store streams as instance variables for use in handler methods
			this.inputStream = in;
			this.outputStream = out;

			Message msg = null;
			// In headless / server environments, avoid GUI popups – just log to console
			System.out.println("New client connection established: " + socket.getRemoteSocketAddress());

			// loop that listens for messages
			while (true) {

				// read new message
				msg = (Message) inputStream.readObject();

				// message status must be a request
				if (msg.getStatus() != MessageStatus.request) {
					// loop again
					continue;
				}

				// check if it is from the ATM or Teller
				Application sender = msg.getSender();
				// if the ATM sent the message call ATM()
				if (sender == Application.ATM) {
					System.out.println("ATM Message: " + msg.getType());
					ATM(msg);
				}
				// if the Teller sent the message call Teller()
				else if (sender == Application.teller) {
					Teller(msg);
				}
				// if the sender isn't AtM or Teller
				else {
					continue;
				}
			}
		} catch (java.io.EOFException e) {
			// Client disconnected 
			System.out.println("Client disconnected");
		} catch (Exception e) {
			// show other errors
			System.out.println("Error: " + e.getMessage());
		}

	}

	private String[] parse(File file) {
		try {
			// scanner reads file
			Scanner scan = new Scanner(file);
			String data = "";

			// read file into the String
			while (scan.hasNextLine()) {
				data += scan.nextLine() + "\n";
			}
			// done with scanner
			scan.close();

			// if file is empty return null
			if (data.equals("")) {
				return new String[0];
			}

			// split on newline, return arrau
			return data.split("\n");

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e);
			return null;
		}

	}

	// handles ATM related messages
	private void ATM(Message msg) throws Exception {
		switch (msg.getType()) {
		case customerLogin:
			// Parse credentials
			String[] creds = msg.getText().split(",");
			java.util.Scanner scanner = new java.util.Scanner(accountFile);
			while (scanner.hasNextLine()) {
				String[] data = scanner.nextLine().split(",");
				if (data.length >= 4 && data[0].equals(msg.getNum()) && data[1].equals(creds[1])) {
					atmLoggedIn = true;
					currentAccountNum = msg.getNum();
					currentBalance = Double.parseDouble(data[3]);
					sendResponse(MessageStatus.confirmation, "Login successful");
					// log successful ATM login
					LogEntry.appendToLog(logFile,
							new LogEntry(Integer.parseInt(currentAccountNum), LogType.login,
									"ATM login successful", java.time.LocalDateTime.now().toString()));
					scanner.close();
					return;
				}
			}
			scanner.close();
			sendResponse(MessageStatus.denial, "Invalid credentials");
			// log failed ATM login
			LogEntry.appendToLog(logFile,
					new LogEntry(parseAccountNumSafe(msg.getNum()), LogType.login, "ATM login failed",
							java.time.LocalDateTime.now().toString()));
			break;

		case withdrawal:
			double withdrawAmt = Double.parseDouble(msg.getText());
			if (!atmLoggedIn || currentAccountNum == null) {
				sendResponse(MessageStatus.denial, "Not logged in");
				break;
			}
			if (withdrawAmt <= 0) {
				sendResponse(MessageStatus.denial, "Invalid amount");
				break;
			}
			if (withdrawAmt <= currentBalance) {
				double newBalance = currentBalance - withdrawAmt;
				if (atmUpdateBalance(currentAccountNum, newBalance)) {
					currentBalance = newBalance;
					sendResponse(MessageStatus.confirmation, "Withdrawn. Balance: $" + currentBalance);
					LogEntry.appendToLog(logFile,
							new LogEntry(Integer.parseInt(currentAccountNum), LogType.withdrawal,
									"ATM withdrew " + withdrawAmt + ", new balance " + currentBalance,
									java.time.LocalDateTime.now().toString()));
				} else {
					sendResponse(MessageStatus.denial, "Server error");
				}
			} else {
				sendResponse(MessageStatus.denial, "Insufficient funds");
			}
			break;

		case deposit:
			if (!atmLoggedIn || currentAccountNum == null) {
				sendResponse(MessageStatus.denial, "Not logged in");
				break;
			}
			double depositAmt = Double.parseDouble(msg.getText());
			if (depositAmt <= 0) {
				sendResponse(MessageStatus.denial, "Invalid amount");
				break;
			}
			{
				double newBalance = currentBalance + depositAmt;
				if (atmUpdateBalance(currentAccountNum, newBalance)) {
					currentBalance = newBalance;
					sendResponse(MessageStatus.confirmation, "Deposited. Balance: $" + currentBalance);
					LogEntry.appendToLog(logFile,
							new LogEntry(Integer.parseInt(currentAccountNum), LogType.deposit,
									"ATM deposited " + depositAmt + ", new balance " + currentBalance,
									java.time.LocalDateTime.now().toString()));
				} else {
					sendResponse(MessageStatus.denial, "Server error");
				}
			}
			break;

		case logout:
			atmLoggedIn = false;
			currentAccountNum = null;
			currentBalance = 0;
			sendResponse(MessageStatus.confirmation, "Logged out");
			LogEntry.appendToLog(logFile,
					new LogEntry(0, LogType.logout, "ATM logout", java.time.LocalDateTime.now().toString()));
			break;

		default:
			// Balance check
			if ("balance".equals(msg.getText())) {
				if (!atmLoggedIn || currentAccountNum == null) {
					sendResponse(MessageStatus.denial, "Not logged in");
				} else {
					sendResponse(MessageStatus.confirmation, String.valueOf(currentBalance));
				}
			}
			break;
		}
	}

	/**
	 * Helper used by ATM operations to persist a new balance for the current
	 * account back to the accounts file.
	 */
	private boolean atmUpdateBalance(String accountNum, double newBalance) {
		boolean worked = false;
		String[] lines = parse(accountFile);
		if (lines == null) {
			return false;
		}

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			String[] info = line.split(",");
			if (info.length < 4) {
				continue;
			}
			if (!info[0].equals(accountNum)) {
				continue;
			}
			// update balance field
			info[3] = String.valueOf(newBalance);
			lines[i] = String.join(",", info);
			worked = true;
			break;
		}

		if (!worked) {
			return false;
		}

		try (PrintWriter write = new PrintWriter(accountFile)) {
			for (String line : lines) {
				write.println(line);
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return false;
		}

		return true;
	}

	// safely parse an account number string, returning 0 on failure
	private int parseAccountNumSafe(String num) {
		if (num == null) {
			return 0;
		}
		try {
			return Integer.parseInt(num);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private void sendResponse(MessageStatus status, String text) throws Exception {
		outputStream.writeObject(new Message(status, null, Application.ATM, currentAccountNum, text));
		outputStream.flush();
	}

	// handles Teller related messages
	private void Teller(Message msg) {
		// store the message type
		MessageType type = msg.getType();

		// switch that calls operation depending on the type
		switch (type) {
		case employeeLogin:
			employeeLogin(msg);
			break;

		case customerLogin:
			customerLogin(msg);
			break;

		case logout:
			logout(msg);
			break;

		case withdrawal:
			withdrawal(msg);
			break;

		case deposit:
			deposit(msg);
			break;

		case updateAccount:
			updateAccount(msg);
			break;

		case updateProfile:
			updateProfile(msg);
			break;

		default:
			// ignore any other message type
			break;

		}

	}

	private void sendMessage(MessageType type, MessageStatus status, String num, String text) {
		// try to write the response to the output stream
		try {
			// create a response message to the teller
			Message response = new Message(status, type, Application.teller, num, text);
			// write to output + flush to send the data immediately
			outputStream.writeObject(response);
			outputStream.flush();

			// catch and display exceptions
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void updateProfile(Message msg) {
		// get the text from message
		String text = msg.getText();

		// CREATE path is used by Teller.createProfile(...)
		if (text != null && text.startsWith("CREATE:")) {
			String dataStr = text.substring("CREATE:".length());
			// Expected order from Teller: name,username,password,phone,address,email
			String[] parts = dataStr.split(",", 6);
			if (parts.length < 6) {
				// invalid payload
				sendMessage(MessageType.updateProfile, MessageStatus.denial, null, "invalid");
				return;
			}

			String name = parts[0];
			String username = parts[1];
			String password = parts[2];
			String phone = parts[3];
			String address = parts[4];
			String email = parts[5];

			// Default credit score and empty accounts list
			String creditScore = "0";
			String accounts = "[]";

			// Append new profile line to profiles file in format:
			// username,password,name,phone,address,email,creditScore,accounts
			try (PrintWriter write = new PrintWriter(new FileWriter(proFile, true))) {
				write.println(username + "," + password + "," + name + "," + phone + "," + address + "," + email + ","
						+ creditScore + "," + accounts);
			} catch (IOException e) {
				// send denial if we could not write the new profile
				sendMessage(MessageType.updateProfile, MessageStatus.denial, null, "error");
				return;
			}

			// send confirmation that the profile was created successfully
			sendMessage(MessageType.updateProfile, MessageStatus.confirmation, null, "valid");
			return;
		}

		// For now, non-CREATE update requests are not supported via the server.
		// (Profile updates are handled locally by Profile.saveToFile().)
		sendMessage(MessageType.updateProfile, MessageStatus.denial, null, "unsupported");

	}

	// method to change account pin
	private void updateAccount(Message msg) {
		// get the account number + pin from Message
		String accountNum = msg.getNum();
		String newPin = msg.getText();

		// make sure pin is a number
		try {
			// parse newPin as integer to validate numeric content
			Integer.parseInt(newPin);

		} catch (NumberFormatException e) {
			// send denial to Teller
			sendMessage(MessageType.updateAccount, MessageStatus.denial, accountNum, "invalid");
			return;
		}

		// call helper to actually change the PIN in accounts file
		boolean success = writePin(accountNum, newPin);
		// if helper returned true, PIN update succeeded
		if (success) {
			// send confirmation message to Teller
			sendMessage(MessageType.updateAccount, MessageStatus.confirmation, accountNum, "valid");
			// log PIN change on server
			LogEntry.appendToLog(logFile,
					new LogEntry(Integer.parseInt(accountNum), LogType.updateAccount,
							"Teller changed PIN for account " + accountNum,
							java.time.LocalDateTime.now().toString()));
			// if helper returned false, update failed (e.g., account not found)
		} else {
			// send denial message to Teller
			sendMessage(MessageType.updateAccount, MessageStatus.denial, accountNum, "invalid");
		}

	}

	private boolean writePin(String accountNum, String newPin) {
		boolean worked = false;
		// read account file lines
		String[] lines = parse(accountFile);

		// loop through lines array to find account
		for (int i = 0; i < lines.length; i++) {
			// save the current line
			String line = lines[i];
			// split line into info strings
			String[] info = line.split(",");

			// check if the account number matches the account in info
			if (!info[0].equals(accountNum)) {
				// if not move on
				continue;
			}
			// set the PIN field to the new value
			info[1] = newPin;
			// rebuild the updated line as CSV
			lines[i] = String.join(",", info);
			// mark that we updated an account
			worked = true;
			// break out of the loop now that we are done
			break;
		}

		// if no account was changed, account not found
		if (!worked) {
			return false;
		}

		// try to write data to the accounts file
		try (PrintWriter write = new PrintWriter(accountFile)) {
			// for each line in lines
			for (String line : lines) {
				// write line to the file
				write.println(line);
			}
		} catch (IOException e) {
			// print error if write fails
			System.out.println(e.getMessage());
			return false;
		}

		// return true for successful pin change + write
		return true;
	}

	private void deposit(Message msg) {
		// get the account number + amount from message
		String accountNum = msg.getNum();
		String amountText = msg.getText();

		// variable to hold the amount
		double amount = 0.0;
		// try to get the amount from text
		try {
			amount = Double.parseDouble(amountText);

		} catch (NumberFormatException e) {
			// send denial if invalid
			sendMessage(MessageType.deposit, MessageStatus.denial, accountNum, "invalid");
			return;
		}

		// call to apply deposit to accounts file
		boolean worked = fileDeposit(accountNum, amount);
		// if true, deposit succeeded
		if (worked) {
			// send confirmation back to Teller
			sendMessage(MessageType.deposit, MessageStatus.confirmation, accountNum, "valid");
			// log teller deposit on server
			LogEntry.appendToLog(logFile,
					new LogEntry(Integer.parseInt(accountNum), LogType.deposit,
							"Teller deposited " + amount + ", new balance updated in accounts file",
							java.time.LocalDateTime.now().toString()));

		} else {
			// send denial to Teller
			sendMessage(MessageType.deposit, MessageStatus.denial, accountNum, "invalid");
		}

	}

	private boolean fileDeposit(String accountNum, double amount) {
		boolean worked = false;
		// read account file lines
		String[] lines = parse(accountFile);

		// loop through lines array to find account
		for (int i = 0; i < lines.length; i++) {
			// save the current line
			String line = lines[i];
			// split line into info strings
			String[] info = line.split(",");

			// check if the account number matches the account in info
			if (!info[0].equals(accountNum)) {
				// if not move on
				continue;
			}
			// if so try to deposit to balance
			try {
				// get balance as double from info
				double balance = Double.parseDouble(info[3]);
				// if its a loc account
				if (info.length > 4 && info[2].equals(String.valueOf(AccountType.lineOfCredit))) {
					double initBalance = Double.parseDouble(info[4]);
					// balance + deposit cannot exceed initial balance
					if (amount + Double.parseDouble(info[3]) > initBalance) {
						// send denial to Teller
						sendMessage(MessageType.deposit, MessageStatus.denial, accountNum, "invalid");
						// return false
						return false;
					}
				}
				// add amount to balance
				balance += amount;
				// store updated balance as string
				info[3] = "" + balance;
				// put info back as a CSV String
				lines[i] = String.join(",", info);
				// note that a change was made
				worked = true;
				// break from for loop
				break;

				// catch invalid numbers
			} catch (NumberFormatException e) {
				return false;
			}
		}

		// if no account was changed, account not found
		if (!worked) {
			return false;
		}

		// try to write data to the accounts file
		try (PrintWriter write = new PrintWriter(accountFile)) {
			// for each line in lines
			for (String line : lines) {
				// write line to the file
				write.println(line);
			}

		} catch (IOException e) {
			// print error if write fails
			System.out.println(e.getMessage());
			return false;
		}

		// return true for successful deposit and file update
		return true;
	}

	private void withdrawal(Message msg) {

		// get the account number + amount from message
		String accountNum = msg.getNum();
		String amountText = msg.getText();

		// variable to hold the amount
		double amount = 0.0;
		// try to get the amount from text
		try {
			amount = Double.parseDouble(amountText);

		} catch (NumberFormatException e) {
			// send denial if invalid
			sendMessage(MessageType.withdrawal, MessageStatus.denial, accountNum, "invalid");
			return;
		}

		// call to apply withdrawal to accounts file
		boolean worked = fileWithdrawal(accountNum, amount);
		// if true, withdrawal succeeded
		if (worked) {
			// send confirmation back to Teller
			sendMessage(MessageType.withdrawal, MessageStatus.confirmation, accountNum, "valid");
			// log teller withdrawal on server
			LogEntry.appendToLog(logFile,
					new LogEntry(Integer.parseInt(accountNum), LogType.withdrawal,
							"Teller withdrew " + amount + ", new balance updated in accounts file",
							java.time.LocalDateTime.now().toString()));
			// if helper returned false, withdrawal failed (insufficient funds or account
			// not found)
		} else {
			// send denial to Teller
			sendMessage(MessageType.withdrawal, MessageStatus.denial, accountNum, "invalid");
		}

	}

	private boolean fileWithdrawal(String accountNum, double amount) {
		boolean worked = false;

		// read account file lines
		String[] lines = parse(accountFile);

		// loop through lines array
		for (int i = 0; i < lines.length; i++) {
			// save the current line
			String line = lines[i];
			// split line into info strings
			String[] info = line.split(",");

			// check if the account number matches the account in info
			if (!info[0].equals(accountNum)) {
				// if not move on
				continue;
			}
			// if so try to withdraw from balance
			try {
				// get balance as double from info
				double balance = Double.parseDouble(info[3]);
				// if balance is insufficient
				if (balance < amount) {
					// return false
					return false;
				}
				// subtract amount from balance
				balance -= amount;
				// store updated balance as string
				info[3] = "" + balance;
				// put info back as a CSV String
				lines[i] = String.join(",", info);

				// note that change was made
				worked = true;
				// break from for loop
				break;
				// catch invalid numbers
			} catch (NumberFormatException e) {
				return false;
			}
		}
		// if it didn't work
		if (!worked) {
			return false;
		}

		// try to write data to the accounts file
		try (PrintWriter write = new PrintWriter(accountFile)) {
			// for each line in lines
			for (String line : lines) {
				// write line to the file
				write.println(line);
			}

		} catch (IOException e) {
			// print error if write fails
			System.out.println(e.getMessage());
			return false;
		}

		// return true for successful withdrawal and file update
		return true;
	}

	private void logout(Message msg) {
		// send a logout confirmation
		sendMessage(MessageType.logout, MessageStatus.confirmation, null, "logout");
		// log teller logout (no specific account)
		LogEntry.appendToLog(logFile,
				new LogEntry(0, LogType.logout, "Teller session logout",
						java.time.LocalDateTime.now().toString()));
	}

	private void customerLogin(Message msg) {
		// get username,password from the message
		String text = msg.getText();
		// split the string into at most two pieces on ","
		String[] credentials = text.split(",", 2);
		// if there are not 2 parts credentials are invalid
		if (credentials.length != 2) {
			// send invalid response
			sendMessage(MessageType.customerLogin, MessageStatus.denial, null, "invalid");
			return;
		}
		// get the username from index 0
		String user = credentials[0];
		// get the password from index 1
		String pass = credentials[1];

		// parse the customer file for searching
		String[] lines = parse(proFile);

		// for each string in the line array
		for (String line : lines) {

			// split the line into fields: username,password,name,phone,address,email,creditScore,accounts
			String[] fields = line.split(",");
			
			// need at least username and password
			if (fields.length < 2) {
				continue;
			}

			// if username and password match
			if (user.equals(fields[0]) && pass.equals(fields[1])) {
				// confirmation to Teller
				sendMessage(MessageType.customerLogin, MessageStatus.confirmation, null, "valid");
				// log customer login via teller
				LogEntry.appendToLog(logFile,
						new LogEntry(0, LogType.login,
								"Teller logged in customer profile " + user,
								java.time.LocalDateTime.now().toString()));
				
				// parse profile data from this line
				// Format: username,password,name,phone,address,email,creditScore,accounts
				String username = fields[0];
				String password = fields[1];
				String name = fields.length > 2 ? fields[2] : "";
				long phone = fields.length > 3 ? Long.parseLong(fields[3]) : 0;
				String address = fields.length > 4 ? fields[4] : "";
				String email = fields.length > 5 ? fields[5] : "";

				// create a new Profile with these
				Profile profile = new Profile(name, username, password, phone, address, email);

				// try sending the profile to teller
				try {
					// write Profile to the output stream + flush to send immediately
					outputStream.writeObject(profile);
					outputStream.flush();
				} catch (Exception e) {
					// print error
					System.out.println(e.getMessage());
				}
				return;
			}
		}
		
		// Only send denial AFTER checking all lines and finding no match
		sendMessage(MessageType.customerLogin, MessageStatus.denial, null, "invalid");
	}

	private void employeeLogin(Message msg) {
		// get username,password from the message
		String text = msg.getText();
		// split the string into at most two pieces on ","
		String[] credentials = text.split(",", 2);
		// if there are not 2 parts credentials are invalid
		if (credentials.length != 2) {
			// send invalid response
			sendMessage(MessageType.employeeLogin, MessageStatus.denial, null, "invalid");
			return;
		}
		// get the username from index 0
		String user = credentials[0];
		// get the password from index 1
		String pass = credentials[1];

		// parse the employee file for searching
		String[] lines = parse(employeeFile);

		// for each string in the line array
		for (String line : lines) {

			// split into no more than two parts
			String[] creds = line.split(",", 2);
			// if we don't get two credentials, move on
			if (creds.length < 2) {
				// continue to next line
				continue;
			}
			// if username and password match creds from file
			if (user.equals(creds[0]) && pass.equals(creds[1])) {
				// confirmation to Teller
				sendMessage(MessageType.employeeLogin, MessageStatus.confirmation, null, "valid");
				return;
			}
		}
		
		// Only send denial AFTER checking all lines and finding no match
		sendMessage(MessageType.employeeLogin, MessageStatus.denial, null, "invalid");
	}

}
