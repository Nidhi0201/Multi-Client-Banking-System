package group3;

import java.io.*;
import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Account implements Serializable {
	private int accountNumber;
	private int pin;
	private final AccountType type;
	private double balance;
	private double initialBalance; // for LOC tracking

	public Account(int num, int pin, AccountType type, double value) {
		this.accountNumber = num;
		this.pin = pin;
		this.type = type;
		this.balance = value;
		this.initialBalance = value;
	}

	public void deposit(double amt) {
		// Line of Credit accounts behave differently: you are paying down debt.
		if (type == AccountType.lineOfCredit) {
			// If there is no outstanding balance, do not accept a payment.
			if (balance == initialBalance) {
				System.out.println("No Balance Owed, Cannot Make Deposit.");
				writeLog(LogType.deposit, "Failed deposit of " + amt + " - no balance owed");
				return;
			} else {
				// reduce debt (balance moves closer to initialBalance)
				balance += amt;
				// don't allow balance to exceed initialBalance
				if (balance > initialBalance) {
					balance = initialBalance;
				}
				writeLog(LogType.deposit, "Deposited " + amt + " to Line of Credit. New balance: " + balance);
			}
		} else {
			// updates account balance
			balance += amt;
			writeLog(LogType.deposit, "Deposited " + amt + ". New balance: " + balance);
		}
	}

	public void withdraw(double amt) {
		// Line of Credit accounts can withdraw up to the credit limit (balance can go to 0)
		if (type == AccountType.lineOfCredit) {
			// For LOC, balance starts at initialBalance (credit limit) and decreases with withdrawals
			// Can withdraw as long as balance doesn't go below 0
			if (balance - amt < 0) {
				System.out.println("Insufficient Credit Available");
				writeLog(LogType.withdrawal, "Failed withdrawal of " + amt + " - insufficient credit");
				return;
			}
			balance -= amt;
			writeLog(LogType.withdrawal, "Withdrew " + amt + " from Line of Credit. New balance: " + balance);
		} else {
			// Regular accounts (checking, saving) need sufficient balance
			if (balance < amt) {
				System.out.println("Insufficient Funds");
				writeLog(LogType.withdrawal, "Failed withdrawal of " + amt + " - insufficient funds");
				return;
			}
			// subtract amount from balance
			balance -= amt;
			writeLog(LogType.withdrawal, "Withdrew " + amt + ". New balance: " + balance);
		}
	}

	// getters
	public double getBalance() {
		return balance;
	}

	public int getPin() {
		return pin;
	}

	public int getNum() {
		return accountNumber;
	}

	public AccountType getType() {
		return type;
	}

	// setters used by GUI / business logic
	public void setAccountNumber(int accountNumber) {
		this.accountNumber = accountNumber;
	}

	public void setPin(int pin) {
		this.pin = pin;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	// returns account history for this account as a comma-separated String of events
	public String getLog() {
		File logFile = new File("src/group3/log.txt");
		if (!logFile.exists()) {
			return "";
		}

		StringBuilder builder = new StringBuilder();
		try (Scanner scanner = new Scanner(logFile)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] parts = line.split(",", 4);
				if (parts.length < 4) {
					continue;
				}
				// parts[0] is account number
				try {
					int num = Integer.parseInt(parts[0]);
					if (num != accountNumber) {
						continue;
					}
				} catch (NumberFormatException e) {
					continue;
				}

				String type = parts[1];
				String message = parts[2];
				String date = parts[3];

				// build a readable event line
				String event = date + " [" + type + "] " + message;
				if (builder.length() > 0) {
					builder.append(",");
				}
				builder.append(event);
			}
		} catch (Exception e) {
			System.out.println("Error reading account log: " + e.getMessage());
		}

		return builder.toString();
	}

	// helper to append a log entry for this account
	private void writeLog(LogType type, String message) {
		LogEntry entry = new LogEntry(accountNumber, type, message, LocalDateTime.now().toString());
		LogEntry.appendToLog(new File("src/group3/log.txt"), entry);
	}
}
