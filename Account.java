package group3;

public class Account {
	private int accountNumber;
	private int pin;
	private AccountType type;
	private double balance;
	private double initialBalance; // for LOC tracking
	
	public Account(int num, int pin, AccountType type, double value) {
		this.accountNumber = num;
		this.pin = pin;
		this.type = type;
		this.balance = value;
	}
	
	public void deposit(double amt) {
		// make sure a LOC account is not at maxium balance
		if (type == AccountType.lineOfCredit) {
			if (balance == initialBalance) {
				System.out.println("No Balance Owed, Cannot Make Deposit.");
				// TODO log entry somehow
				return;
			}
		}
		else {
		// updates account balance
		balance += amt;
		
		// TODO confirmation for log entry
	
		}
	}
	public void withdraw(double amt) {
		if (balance < amt) {
			System.out.println("Insufficient Funds");
			// TODO log entry somehow
			return;
		}
	}
	
	
	public double getBalance() {
		return balance;
	}
	public int getPin() {
		return pin;
	}
	public int getNum() {
		return accountNumber;
	}
	
	
}
