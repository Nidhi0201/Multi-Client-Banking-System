package group3;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;


public class Server {
	private static File logFile = new File("log.txt");              // accountNum,type,message,date\n
	private static File employeeFile = new File("employees.txt");   // username,password\n
	private static File proFile = new File("profiles.txt");         // username,password,name,phone,address,email,creditScore,[num,num,num]\n
	// for access to one account at a time by the ATM
	private static File accountFile = new File("accounts.txt");     // number,pin,type,balance,initialBalance

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
			var s = socket;
      // IMPORTANT: Server reads first, so create InputStream before OutputStream to avoid deadlock
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
      ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());) {
          
      // Store streams as instance variables for use in handler methods
			this.inputStream = in;
			this.outputStream = out;

			// create variables outside of loop
			Message msg = null;
			MessageType type = MessageType.undefined;
			Application sender = Application.undefined;
			boolean loggedIn = false;

			JOptionPane.showMessageDialog(null, "New Connection");

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
		} catch (Exception e) {
			// show the error
			JOptionPane.showMessageDialog(null, e);
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
				if (data[0].equals(msg.getNum()) && data[1].equals(creds[1])) {
					atmLoggedIn = true;
					currentAccountNum = msg.getNum();
					currentBalance = Double.parseDouble(data[3]);
					sendResponse(MessageStatus.confirmation, "Login successful");
					scanner.close();
					return;
				}
			}
			scanner.close();
			sendResponse(MessageStatus.denial, "Invalid credentials");
			break;

		case withdrawal:
			double withdrawAmt = Double.parseDouble(msg.getText());
			if (withdrawAmt <= currentBalance) {
				currentBalance -= withdrawAmt;
				sendResponse(MessageStatus.confirmation, "Withdrawn. Balance: $" + currentBalance);
			} else {
				sendResponse(MessageStatus.denial, "Insufficient funds");
			}
			break;

		case deposit:
			currentBalance += Double.parseDouble(msg.getText());
			sendResponse(MessageStatus.confirmation, "Deposited. Balance: $" + currentBalance);
			break;

		case logout:
			atmLoggedIn = false;
			currentBalance = 0;
			sendResponse(MessageStatus.confirmation, "Logged out");
			break;

		default:
			// Balance check
			if (msg.getText().equals("balance")) {
				sendResponse(MessageStatus.confirmation, String.valueOf(currentBalance));
			}
			break;
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
		// split the attribute from the new value
		String[] arr = text.split(",");
		// parse the profile file to get the values
		String[] lines = parse(proFile);
		// data should not hold more than 8 attributes
		String[] data = new String[8];

		// split each line into data strings
		for (String line : lines) {
			data = line.split(",");
		}

		// username,password,name,phone,address,email,creditScore

		switch (arr[0]) {
		// if attribute is username
		case "username":
			// update username element
			data[0] = arr[1];
			break;

		// if attribute is password
		case "password":
			// update the password element
			data[1] = arr[1];

			break;

		case "name":
			// update the name element
			data[2] = arr[1];
			break;

		case "phone":
			// update the phone element
			data[3] = arr[1];
			break;

		case "address":
			// update the address element
			data[4] = arr[1];
			break;

		case "email":
			// update the email element
			data[5] = arr[1];
			break;

		default:
			break;
		}

		// save changes to file

		// try block to append a new profile line to the profiles file
		try (PrintWriter write = new PrintWriter(proFile)) {
			// for each line in lines
			for (String line : lines) {
				// write line to the file
				write.println(line);
			}
		} catch (IOException e) {
			// send denial if we could not write the new profile
			sendMessage(MessageType.updateProfile, MessageStatus.denial, null, "error");
			// return because we failed to create the profile
			return;
		}

		// send confirmation that the profile was created successfully
		sendMessage(MessageType.updateProfile, MessageStatus.confirmation, null, "valid");

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
			// rebuild the updated line
			lines[i] = "" + info;
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
			// if so try to withdraw from balance
			try {
				// get balance as double from info
				double balance = Double.parseDouble(info[3]);
				// if its a loc account
				if (info[2] == "" + AccountType.lineOfCredit) {
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
				// put info back as a String
				lines[i] = "" + info;
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
				// put info back as a String
				lines[i] = "" + info;

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

			// split into two parts
			String[] creds = line.split(",", 2);
			// split the second string to break off the password
			String[] temp = creds[1].split(",", 2);
			// put the password back into creds
			creds[1] = temp[0];

			// if username and password match creds from file
			if (user.equals(creds[0]) && pass.equals(creds[1])) {
				// confirmation to Teller
				sendMessage(MessageType.customerLogin, MessageStatus.confirmation, null, "valid");
			} else {
				// send denial if it doesn't match
				sendMessage(MessageType.customerLogin, MessageStatus.denial, null, "invalid");
			}
		}

		// attributes from parsing
		String name = lines[2];
		String username = lines[0];
		String password = lines[1];
		long phone = Long.parseLong(lines[3]);
		String address = lines[4];
		String email = lines[5];

		// create a new Profile with these
		Profile proFile = new Profile(name, username, password, phone, address, email);

		// try sending the profile to teller
		try {
			// write Profile to the output stream + flush to send immediately
			outputStream.writeObject(proFile);
			outputStream.flush();
			return;

		} catch (Exception e) {
			// print error
			System.out.println(e.getMessage());
		}

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
			} else {
				// send denial if it doesn't match
				sendMessage(MessageType.employeeLogin, MessageStatus.denial, null, "invalid");
			}

		}

	}

}
