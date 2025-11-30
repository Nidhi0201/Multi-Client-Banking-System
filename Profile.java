package group3;

public class Profile {
	private String name;
	private String username;
	private String password;
	private long phone;
	private String address;
	private String email;
	private Account[] accounts;
	private int creditScore;
	
	
	public Profile(String name, String user, String pass, long phone, String addy, String email) {
		this.name = name;
		this.username = user;
		this.password = pass;
		this.phone = phone;
		this.address = addy;
		this.email = email;
	}
	
	public void addAccount(AccountType type, double value){
		// gets permission for a new account
		// creates new account
		// adds an account to the array
		// writes it to the file
	}
	
	public void removeAccount(Account account){
		// gets permission to remove account
		// removes account from the array
		// moves the index of the other accounts
		// removes it from the file
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
	public long getPhone() {
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
	public int getCreditScore() {
		return creditScore;
	}
	public String getPassword() {
		return password;
	}
	

	// setters
	public void setName(String name) {
	    this.name = name;
	}

	public void setUsername(String username) {
	    this.username = username;
	}

	public void setPassword(String password) {
	    this.password = password;
	}

	public void setPhone(long phone) {
	    this.phone = phone;
	}

	public void setAddress(String address) {
	    this.address = address;
	}

	public void setEmail(String email) {
	    this.email = email;
	}

}
