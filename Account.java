package group3;

import java.io.Serializable;

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
		this.initialBalance = value;
	}
	
	public void deposit(double amt) {
		// Line of Credit accounts behave differently: you are paying down debt.
		if (type == AccountType.lineOfCredit) {
			// If there is no outstanding balance, do not accept a payment.
			if (balance == initialBalance) {
				System.out.println("No Balance Owed, Cannot Make Deposit.");
				// TODO log entry somehow
				return;
			} else {
				// reduce debt (balance moves closer to initialBalance)
				balance += amt;
				// don't allow balance to exceed initialBalance
				if (balance > initialBalance) {
					balance = initialBalance;
				}
				// TODO log entry
			}
		} else {
			// updates account balance
			balance += amt;
			
			// TODO confirmation for log entry
		}
	}
	public void withdraw(double amt) {
		// Line of Credit accounts can withdraw up to the credit limit (balance can go to 0)
		if (type == AccountType.lineOfCredit) {
			// For LOC, balance starts at initialBalance (credit limit) and decreases with withdrawals
			// Can withdraw as long as balance doesn't go below 0
			if (balance - amt < 0) {
				System.out.println("Insufficient Credit Available");
				// TODO log entry somehow
				return;
			}
			balance -= amt;
			// TODO log entry
		} else {
			// Regular accounts (checking, saving) need sufficient balance
			if (balance < amt) {
				System.out.println("Insufficient Funds");
				// TODO log entry somehow
				return;
			}
			// subtract amount from balance
			balance -= amt;
			// TODO log entry
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
	
}
