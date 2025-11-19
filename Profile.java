package group3;

public class Profile {
	private String name;
	private String username;
	private String password;
	private int phone;
	private String address;
	private String email;
	private Account[] accounts;
	
	
	public Profile(String name, String user, String pass, int phone, String addy, String email) {
		this.name = name;
		this.username = user;
		this.password = pass;
		this.phone = phone;
		this.address = addy;
		this.email = email;
	}
	
	public void addAccount(Account account){
		// gets permission for a new account
		// creates new account
		// adds an account to the array
	}
	
	public void removeAccount(Account account){
		// gets permission to remove account
		// removes account from the array
		// moves the index of the other accounts
	}
	
	public Account findAccount(Account account) {
		Account acc = null;
		
		// searches array for account
		
		return acc;
		
	}
	
	// getters
	public String getName() {
		return name;
	}
	public String getUsername() {
		return username;
	}
	public int getPhone() {
		return phone;
	}
	public String getAddress() {
		return address;
	}
	public String getEmail() {
		return email;
	}
	public Account[] getAccounts() {
		return accounts;
	}

}
