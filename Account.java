package group3;

public class Account {
	private int accountNumber;
	private int pin;
	private final AccountType type;
	private double balance;
	private final double initialBalance; // for LOC tracking

	public Account(int num, int pin, AccountType type, double value) {
		this.accountNumber = num;
		this.pin = pin;
		this.type = type;
		this.balance = value;
		this.initialBalance = value;
	}

	public void deposit(double amt) {
		// make sure a LOC account is not at maxium balance
		if (type == AccountType.lineOfCredit) {
			if (balance == initialBalance) {
				System.out.println("No Balance Owed, Cannot Make Deposit.");
				// TODO log entry
				return;
			}
		} else {
			// updates account balance
			balance += amt;

			// TODO confirmation for log entry

		}
	}

	public void withdraw(double amt) {
		if (balance < amt) {
			System.out.println("Insufficient Funds");
			// TODO log entry
			return;
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
		return this.type;
	}

	// setters
	public void setAccountNumber(int accountNumber) {
		this.accountNumber = accountNumber;
	}

	public void setPin(int pin) {
		this.pin = pin;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	public String getLog() {
		String log = "";
		
		// gets account history from server log
		
		// returns account history with events separated by ","
		
		return log;
	}

}
