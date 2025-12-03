package group3;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;

public class Profile implements Serializable {
	private String name;
	private String username;
	private String password;
	private long phone;
	private String address;
	private String email;
	private ArrayList<Account> accounts;
	private int creditScore;
	// use explicit path relative to project root so it works when run from IDE
	private static File accountFile = new File("src/group3/accounts.txt");
	private static File profileFile = new File("src/group3/profiles.txt");

	public Profile(String name, String user, String pass,long phone, String addy, String email) {
		this.name = name;
		this.username = user;
		this.password = pass;
		this.phone = phone;
		this.address = addy;
		this.email = email;
		this.accounts = new ArrayList<>();
		this.creditScore = 0;
	}

	// load accounts from file into the array
	public void loadAccounts(int[] accountNumbers) {
		try (java.util.Scanner scanner = new java.util.Scanner(accountFile)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] data = line.split(",");
				if (data.length >= 4) {
					int accNum = Integer.parseInt(data[0]);
					// check if this account belongs to this profile
					for (int num : accountNumbers) {
						if (accNum == num) {
							int pin = Integer.parseInt(data[1]);
							AccountType type = AccountType.valueOf(data[2]);
							double balance = Double.parseDouble(data[3]);
							accounts.add(new Account(accNum, pin, type, balance));
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Error loading accounts: " + e.getMessage());
		}
	}

	// add a new account to the profile
	public void addAccount(AccountType type, double value) {
		// generate new account number
		int newAccountNum = generateAccountNumber();
		// generate random 4-digit PIN
		int newPin = 1000 + (int)(Math.random() * 9000);

		// create new account
		Account newAccount = new Account(newAccountNum, newPin, type, value);

		// add to array
		accounts.add(newAccount);

		// write to file
		saveAccountToFile(newAccount);

		System.out.println("Account created: " + newAccountNum + " PIN: " + newPin);
	}

	// generate unique account number
	private int generateAccountNumber() {
		int maxNum = 1000;
		try (java.util.Scanner scanner = new java.util.Scanner(accountFile)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] data = line.split(",");
				if (data.length >= 1) {
					int num = Integer.parseInt(data[0]);
					if (num >= maxNum) {
						maxNum = num + 1;
					}
				}
			}
		} catch (Exception e) {
			// file might not exist, start with 1001
		}
		return maxNum;
	}

	// save a single account to file
	private void saveAccountToFile(Account acc) {
		try (PrintWriter writer = new PrintWriter(new FileWriter(accountFile, true))) {
			writer.println(acc.getNum() + "," + acc.getPin() + "," + acc.getType() + "," + acc.getBalance());
		} catch (Exception e) {
			System.out.println("Error saving account: " + e.getMessage());
		}
	}

	// remove account from profile
	public void removeAccount(Account account) {
		int removedNum = account.getNum();
		accounts.remove(account);
		rewriteAccountsFile(removedNum);
	}

	// rewrite accounts file excluding the removed account
	private void rewriteAccountsFile(int removedAccountNum) {
		ArrayList<String> linesToKeep = new ArrayList<>();
		try (java.util.Scanner scanner = new java.util.Scanner(accountFile)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] data = line.split(",");
				if (data.length >= 1 && Integer.parseInt(data[0]) != removedAccountNum) {
					linesToKeep.add(line);
				}
			}
		} catch (Exception e) {
			System.out.println("Error reading accounts: " + e.getMessage());
		}

		try (PrintWriter writer = new PrintWriter(accountFile)) {
			for (String line : linesToKeep) {
				writer.println(line);
			}
		} catch (Exception e) {
			System.out.println("Error rewriting accounts: " + e.getMessage());
		}
	}

	// find account by account number
	public Account findAccount(int accountNumber) {
		for (Account acc : accounts) {
			if (acc.getNum() == accountNumber) {
				return acc;
			}
		}
		return null;
	}

	// find account by Account object
	public Account findAccount(Account account) {
		for (Account acc : accounts) {
			if (acc.getNum() == account.getNum()) {
				return acc;
			}
		}
		return null;
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
		return accounts.toArray(new Account[0]);
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
	public void setCreditScore(int creditScore) {
	    this.creditScore = creditScore;
	}
	public void setAccounts(ArrayList<Account> accounts) {
	    this.accounts = accounts;
	}

	// save profile changes to file
	public void saveToFile() {
		ArrayList<String> allLines = new ArrayList<>();
		boolean found = false;
		
		// read all profiles from file
		try (java.util.Scanner scanner = new java.util.Scanner(profileFile)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] data = line.split(",");
				// check if this line is for the current user (by matching username at index 0)
				if (data.length >= 1 && data[0].equals(this.username)) {
					// replace with updated profile data
					// format: username,password,name,phone,address,email,creditScore,accounts
					String accountList = "[]"; // keeping accounts format simple for now
					String updatedLine = username + "," + password + "," + name + "," + phone + "," + address + "," + email + "," + creditScore + "," + accountList;
					allLines.add(updatedLine);
					found = true;
				} else {
					allLines.add(line);
				}
			}
		} catch (Exception e) {
			System.out.println("Error reading profiles: " + e.getMessage());
		}
		
		// write all lines back to file
		try (PrintWriter writer = new PrintWriter(profileFile)) {
			for (String line : allLines) {
				writer.println(line);
			}
			System.out.println("Profile saved successfully");
		} catch (Exception e) {
			System.out.println("Error saving profile: " + e.getMessage());
		}
	}
}