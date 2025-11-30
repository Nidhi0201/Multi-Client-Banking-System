package group3;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

// GUI object that can be used for an ATM or Teller Application
public class BankGUI {
	Scanner scan;
	Application application;
	static Teller teller;
	static ATM atm;

	// GUI constructor takes the application in use
	public BankGUI(ATM atm, Teller teller) {
		scan = new Scanner(System.in);
		this.teller = teller;
		this.atm = atm;

		// start up the GUI based on the application
		if (teller != null) {
			// if teller object, startTeller GUI
			startTeller();
		} else if (atm != null) {

			// if ATM object startATM GUI
			startATM();
		}
	}

	//////////////////////////////////
	// shows teller specific screen //
	//////////////////////////////////
	private static void startTeller() {
		// must first login employee and customer at teller
		employeeLogin();
	}

	///////////////////////////////
	// shows ATM specific screen //
	///////////////////////////////
	private static void startATM() {
		// entry point for ATM GUI - shows login first
		atmLoginScreen();
	}

	// ATM login screen - user enters account number and PIN
	private static void atmLoginScreen() {
		// create login frame
		JFrame loginFrame = new JFrame("ATM Login");
		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loginFrame.setSize(400, 300);
		loginFrame.setLocationRelativeTo(null);
		loginFrame.setLayout(new BorderLayout());

		// main panel with grid layout for components
		JPanel mainPanel = new JPanel(new GridLayout(5, 1, 10, 10));
		JLabel title = new JLabel("Welcome to ATM", JLabel.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 24));

		// account number input
		JPanel accountPanel = new JPanel(new FlowLayout());
		accountPanel.add(new JLabel("Account Number:"));
		JTextField accountField = new JTextField(15);
		accountPanel.add(accountField);

		// PIN input
		JPanel pinPanel = new JPanel(new FlowLayout());
		pinPanel.add(new JLabel("PIN:                      "));
		JTextField pinField = new JTextField(15);
		pinPanel.add(pinField);

		// login and cancel buttons
		JPanel buttonPanel = new JPanel(new FlowLayout());
		JButton loginBtn = new JButton("Login");
		JButton cancelBtn = new JButton("Cancel");
		buttonPanel.add(loginBtn);
		buttonPanel.add(cancelBtn);

		// add all panels to main panel
		mainPanel.add(title);
		mainPanel.add(accountPanel);
		mainPanel.add(pinPanel);
		mainPanel.add(buttonPanel);
		loginFrame.add(mainPanel, BorderLayout.CENTER);

		// login button - validates credentials with server via ATM.java
		loginBtn.addActionListener(e -> {
			String accountNum = accountField.getText();
			String pin = pinField.getText();
			if (accountNum.isEmpty() || pin.isEmpty()) {
				JOptionPane.showMessageDialog(loginFrame, "Please enter account number and PIN");
				return;
			}
			// call ATM.customerLogin() which sends message to server
			if (atm.customerLogin(accountNum, pin)) {
				JOptionPane.showMessageDialog(loginFrame, "Login Successful!");
				loginFrame.dispose();
				atmMainMenu(); // go to main menu on success
			} else {
				JOptionPane.showMessageDialog(loginFrame, "Invalid account number or PIN");
				accountField.setText("");
				pinField.setText("");
			}
		});

		// cancel button - disconnect and exit
		cancelBtn.addActionListener(e -> {
			atm.disconnect();
			loginFrame.dispose();
			System.exit(0);
		});

		loginFrame.setVisible(true);
	}

	// ATM main menu - shows options after successful login
	private static void atmMainMenu() {
		// create menu frame
		JFrame menuFrame = new JFrame("ATM Menu");
		menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		menuFrame.setSize(400, 400);
		menuFrame.setLocationRelativeTo(null);
		menuFrame.setLayout(new BorderLayout());

		// title at top
		JLabel title = new JLabel("Select an Option", JLabel.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 20));
		menuFrame.add(title, BorderLayout.NORTH);

		// four main option buttons
		JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
		JButton balanceBtn = new JButton("Check Balance");
		JButton withdrawBtn = new JButton("Withdraw");
		JButton depositBtn = new JButton("Deposit");
		JButton logoutBtn = new JButton("Logout");

		buttonPanel.add(balanceBtn);
		buttonPanel.add(withdrawBtn);
		buttonPanel.add(depositBtn);
		buttonPanel.add(logoutBtn);
		menuFrame.add(buttonPanel, BorderLayout.CENTER);

		// check balance - calls ATM.checkBalance() which talks to server
		balanceBtn.addActionListener(e -> {
			double balance = atm.checkBalance();
			JOptionPane.showMessageDialog(menuFrame, "Your balance is: $" + String.format("%.2f", balance));
		});

		// withdraw - opens withdraw screen
		withdrawBtn.addActionListener(e -> {
			menuFrame.setVisible(false);
			atmWithdrawScreen(menuFrame);
		});

		// deposit - opens deposit screen
		depositBtn.addActionListener(e -> {
			menuFrame.setVisible(false);
			atmDepositScreen(menuFrame);
		});

		// logout - calls ATM.logout() and returns to login screen
		logoutBtn.addActionListener(e -> {
			atm.logout();
			JOptionPane.showMessageDialog(menuFrame, "Thank you for using ATM. Goodbye!");
			menuFrame.dispose();
			atmLoginScreen();
		});

		menuFrame.setVisible(true);
	}

	// ATM withdraw screen - allows user to withdraw money
	private static void atmWithdrawScreen(JFrame menuFrame) {
		// create withdraw frame
		JFrame withdrawFrame = new JFrame("Withdraw");
		withdrawFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		withdrawFrame.setSize(400, 300);
		withdrawFrame.setLocationRelativeTo(null);
		withdrawFrame.setLayout(new BorderLayout());

		JPanel mainPanel = new JPanel(new GridLayout(4, 1, 10, 10));
		JLabel title = new JLabel("Enter Withdrawal Amount", JLabel.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 18));

		// quick amount buttons for common withdrawals
		JPanel quickPanel = new JPanel(new FlowLayout());
		JButton btn20 = new JButton("$20");
		JButton btn40 = new JButton("$40");
		JButton btn60 = new JButton("$60");
		JButton btn100 = new JButton("$100");
		quickPanel.add(btn20);
		quickPanel.add(btn40);
		quickPanel.add(btn60);
		quickPanel.add(btn100);

		// custom amount input
		JPanel amountPanel = new JPanel(new FlowLayout());
		amountPanel.add(new JLabel("Amount: $"));
		JTextField amountField = new JTextField(10);
		amountPanel.add(amountField);

		// confirm and cancel buttons
		JPanel buttonPanel = new JPanel(new FlowLayout());
		JButton confirmBtn = new JButton("Confirm");
		JButton cancelBtn = new JButton("Cancel");
		buttonPanel.add(confirmBtn);
		buttonPanel.add(cancelBtn);

		mainPanel.add(title);
		mainPanel.add(quickPanel);
		mainPanel.add(amountPanel);
		mainPanel.add(buttonPanel);
		withdrawFrame.add(mainPanel, BorderLayout.CENTER);

		// quick amount buttons fill in the amount field
		btn20.addActionListener(e -> amountField.setText("20"));
		btn40.addActionListener(e -> amountField.setText("40"));
		btn60.addActionListener(e -> amountField.setText("60"));
		btn100.addActionListener(e -> amountField.setText("100"));

		// confirm button - calls ATM.withdraw() which sends request to server
		confirmBtn.addActionListener(e -> {
			try {
				double amount = Double.parseDouble(amountField.getText());
				if (atm.withdraw(amount)) {
					JOptionPane.showMessageDialog(withdrawFrame, "Please take your cash: $" + amount);
					withdrawFrame.dispose();
					menuFrame.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(withdrawFrame, "Withdrawal failed. Insufficient funds.");
				}
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(withdrawFrame, "Please enter a valid number");
			}
		});

		// cancel - return to main menu
		cancelBtn.addActionListener(e -> {
			withdrawFrame.dispose();
			menuFrame.setVisible(true);
		});

		withdrawFrame.setVisible(true);
	}

	// ATM deposit screen - allows user to deposit money
	private static void atmDepositScreen(JFrame menuFrame) {
		// create deposit frame
		JFrame depositFrame = new JFrame("Deposit");
		depositFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		depositFrame.setSize(400, 250);
		depositFrame.setLocationRelativeTo(null);
		depositFrame.setLayout(new BorderLayout());

		JPanel mainPanel = new JPanel(new GridLayout(3, 1, 10, 10));
		JLabel title = new JLabel("Enter Deposit Amount", JLabel.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 18));

		// amount input
		JPanel amountPanel = new JPanel(new FlowLayout());
		amountPanel.add(new JLabel("Amount: $"));
		JTextField amountField = new JTextField(10);
		amountPanel.add(amountField);

		// confirm and cancel buttons
		JPanel buttonPanel = new JPanel(new FlowLayout());
		JButton confirmBtn = new JButton("Confirm");
		JButton cancelBtn = new JButton("Cancel");
		buttonPanel.add(confirmBtn);
		buttonPanel.add(cancelBtn);

		mainPanel.add(title);
		mainPanel.add(amountPanel);
		mainPanel.add(buttonPanel);
		depositFrame.add(mainPanel, BorderLayout.CENTER);

		// confirm button - calls ATM.deposit() which sends request to server
		confirmBtn.addActionListener(e -> {
			try {
				double amount = Double.parseDouble(amountField.getText());
				if (atm.deposit(amount)) {
					JOptionPane.showMessageDialog(depositFrame, "Deposited: $" + amount);
					depositFrame.dispose();
					menuFrame.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(depositFrame, "Deposit failed.");
				}
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(depositFrame, "Please enter a valid number");
			}
		});

		// cancel - return to main menu
		cancelBtn.addActionListener(e -> {
			depositFrame.dispose();
			menuFrame.setVisible(true);
		});

		depositFrame.setVisible(true);
	}

	// login screen for employees then customers
	private static void employeeLogin() {

		// create login screen frame
		JFrame login = new JFrame("Login");
		// frame closes by the X button
		login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// height by width in pixels
		login.setSize(1000, 500);
		// relative to null is center of screen
		login.setLocationRelativeTo(null);
		login.setLayout(new BorderLayout());

		// create employee login screen panel + text fields
		JPanel employeePanel = new JPanel();
		employeePanel.setLayout(new FlowLayout());
		JTextField employeeUser = new JTextField(20);
		JTextField employeePass = new JTextField(20);
		// ELB = employee login button
		JButton ELB = new JButton("Employee Login");
		employeePanel.add(new JLabel("Employee Login"));
		employeePanel.add(employeeUser);
		employeePanel.add(employeePass);
		employeePanel.add(ELB);

		// add employee panel to frame
		login.add(employeePanel, BorderLayout.CENTER);

		// employee login sequence
		ELB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// calls employee login on the teller application with user + pass
				boolean valid = teller.employeeLogin(employeeUser.getText(), employeePass.getText());

				// if employee login is approved
				if (valid) {
					// close employee login frame
					login.dispose();
					// move on to customer login
					customerLogin();
				}
				// if employee login is not approved do nothing
				else {
					JOptionPane.showMessageDialog(login, "Employee login failed");
				}
			}
		});

		// set the frame attribute to true; makes it visible
		login.setVisible(true);
	}

	public static void customerLogin() {
		// create login screen frame
		JFrame login = new JFrame("Login");
		// frame closes by the X button
		login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// height by width in pixels
		login.setSize(1000, 500);
		// relative to null is center of screen
		login.setLocationRelativeTo(null);
		login.setLayout(new BorderLayout());

		// panel for customer login that starts invisible
		JPanel customerPanel = new JPanel();
		customerPanel.setLayout(new FlowLayout());
		JTextField customerUser = new JTextField(20);
		JTextField customerPass = new JTextField(20);
		// CLB = customer login button
		JButton CLB = new JButton("Customer Login");
		customerPanel.add(new JLabel("Customer Login"));
		customerPanel.add(customerUser);
		customerPanel.add(customerPass);
		customerPanel.add(CLB);

		login.add(customerPanel, BorderLayout.CENTER);

		// customer login sequence
		CLB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// calls customer login on the teller application with user + pass
				Profile profile = teller.customerLogin(customerUser.getText(), customerPass.getText());
				if (profile != null) { // change to == for testing w/o a profile
					// if customer login is approved close the panel and frame
					login.remove(customerPanel);
					login.dispose();

					// call showProfile
					showProfile(profile);

					login.dispose();
				} else {
					// if customer login is not approved do nothing
					JOptionPane.showMessageDialog(login, "Customer login failed");
				}
			}
		});

		// set the frame attribute to true; makes it visible
		login.setVisible(true);
	}

	public static void showProfile(Profile profile) {
		// store accounts associated with the profile
		Account[] accounts = profile.getAccounts();
		Object[] accountMenu = new Object[accounts.length + 1]; // account array with prompt at index 0 for dropdown
																// menu

		// fill the array with prompt and account numbers
		accountMenu[0] = "Select An Account";
		for (int i = 0; i < accounts.length; i++) {
			accountMenu[i + 1] = "Account " + accounts[i].getNum();
		}

		// create profile view frame
		JFrame profileFrame = new JFrame("Customer Profile");
		// frame closes by the X button
		profileFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// height by width in pixels
		profileFrame.setSize(1000, 500);
		// relative to null is center of screen
		profileFrame.setLocationRelativeTo(null);
		profileFrame.setLayout(new BorderLayout());

		// main panel with inital options
		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		// welcome header
		JLabel welcome = new JLabel("Welcome " + profile.getName());
		welcome.setFont(new Font("Arial", Font.BOLD, 30));
		// buttons and dropdown menu for viewing an account
		JButton info = new JButton("View Profile Information");
		JButton newAccount = new JButton("Add New Account");
		JButton openAccount = new JButton("Open Existing Account");
		final JComboBox<Object> selector = new JComboBox<>(accountMenu);
		JButton logout = new JButton("logout");
		selector.setSelectedIndex(0); // show default first
		// add header at the north spot
		main.add(welcome, BorderLayout.NORTH);

		// panel with gridlayout for buttons, with specified rows and columns
		JPanel buttons = new JPanel(new GridLayout(5, 1));
		buttons.add(info);
		buttons.add(newAccount);
		buttons.add(openAccount);
		buttons.add(selector);
		buttons.add(logout);
		// add button panel to main panel
		main.add(buttons, BorderLayout.CENTER);
		// add main panel to the frame
		profileFrame.add(main, BorderLayout.CENTER);

		// Panel for profile info
		JPanel infoPanel = new JPanel(new GridLayout(7, 2));
		JLabel name = new JLabel("Name: " + profile.getName());
		JLabel address = new JLabel("Address: " + profile.getAddress());
		JLabel phone = new JLabel("Phone: " + profile.getPhone());
		JLabel email = new JLabel("Email: " + profile.getEmail());
		JLabel score = new JLabel("Credit Score: " + profile.getCreditScore());
		JLabel username = new JLabel("Username: " + profile.getUsername());
		JLabel password = new JLabel("Password: " + profile.getPassword());
		JButton update = new JButton("update personal info");
		// back button for info screen
		JButton infoBack = new JButton("back");
		// String options for dropdown menu
		String[] infoMenu = { "make a selection", "Name", "Address", "Phone", "Email", "Username", "Password" };
		final JComboBox<String> options = new JComboBox<>(infoMenu);
		options.setSelectedIndex(0); // shows prompt as default
		// add components to panel
		infoPanel.add(name);
		infoPanel.add(address);
		infoPanel.add(phone);
		infoPanel.add(email);
		infoPanel.add(score);
		infoPanel.add(username);
		infoPanel.add(password);
		infoPanel.add(update);
		infoPanel.add(options);
		infoPanel.add(infoBack);

		// Panel for creating a new account
		JPanel createPanel = new JPanel();
		createPanel.setLayout(new BorderLayout());
		JLabel message = new JLabel("Enter initial amount and select an account type");
		JTextField initBalance = new JTextField(11);
		JButton checking = new JButton("Checking");
		JButton saving = new JButton("Saving");
		JButton loc = new JButton("Line of Credit");
		// back button for create screen
		JButton createBack = new JButton("back");

		// panel with gridlayout for buttons, with specified rows and columns
		JPanel grid = new JPanel(new GridLayout(6, 1));
		grid.add(message);
		grid.add(initBalance);
		grid.add(checking);
		grid.add(saving);
		grid.add(loc);
		grid.add(createBack);
		// add button panel to main panel
		createPanel.add(grid, BorderLayout.CENTER);

		// Panel for viewing existing account
		JPanel accountPanel = new JPanel();
		JLabel number = new JLabel();
		JLabel balance = new JLabel();
		JLabel instruction = new JLabel("to make a transaction, enter the amount and then select a transaction type");
		JButton pin = new JButton("Change Pin");
		JButton history = new JButton("View Account History");
		JTextField amount = new JTextField(11);
		// set transactionMenu equal to new menu
		String[] transactionMenu = { "select a transaction type", "Deposit", "Withdraw", "Transfer" };
		JComboBox accOptions = new JComboBox<>(transactionMenu);
		accOptions.setSelectedIndex(0); // shows prompt as default
		JButton transaction = new JButton("Make Transaction");
		JButton transactionBack = new JButton("back");
		accountPanel.add(number);
		accountPanel.add(balance);
		accountPanel.add(pin);
		accountPanel.add(history);
		accountPanel.add(amount);
		accountPanel.add(accOptions);
		accountPanel.add(instruction);
		accountPanel.add(transaction);
		accountPanel.add(transactionBack);

		// user selects to show info
		info.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// display information panel
				profileFrame.remove(main);
				profileFrame.add(infoPanel, BorderLayout.CENTER);
				// refresh the UI for the new panel
				profileFrame.revalidate();
				profileFrame.repaint();

			}
		});

		// user selects to create new account
		newAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// display new account form panel
				profileFrame.remove(main);
				profileFrame.add(createPanel, BorderLayout.CENTER);
				// refresh the UI for the new panel
				profileFrame.revalidate();
				profileFrame.repaint();

			}
		});

		// user selects to open existing account
		openAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// index of option selected must be > 0
				int idx = selector.getSelectedIndex();
				if (idx > 0) {
					Account acct = accounts[idx - 1];

					// update labels with this account's info
					number.setText("Account: " + acct.getNum());
					balance.setText("Balance: " + acct.getBalance());

					// display account panel with number + balance + buttons
					profileFrame.remove(main);
					profileFrame.add(accountPanel, BorderLayout.CENTER);
					// refresh the UI for the new panel
					profileFrame.revalidate();
					profileFrame.repaint();
				} else {
					JOptionPane.showConfirmDialog(null, "select an account from the dropdown menu");
					return;
				}

			}
		});

		transaction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int idx = accOptions.getSelectedIndex();
				if (idx > 0) {

					// get the choice
					String choice = transactionMenu[idx];

					// try catch to be sure amount is valid
					double amt;
					try {
						amt = Double.parseDouble(amount.getText());
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(profileFrame, "enter a valid amount");
						return;
					}

					// get the selection from the account dropdown, make sure it is an account
					int acctIdx = selector.getSelectedIndex();
					if (acctIdx <= 0) {
						JOptionPane.showMessageDialog(profileFrame, "select an account from the dropdown menu");
						return;
					}
					Account acc = accounts[acctIdx - 1];

					// switch to call the methods on the account
					switch (choice) {
					case "Deposit":
						acc.deposit(amt);
						balance.setText("Balance: " + acc.getBalance());
						break;
					case "Withdraw":
						acc.withdraw(amt);
						balance.setText("Balance: " + acc.getBalance());
						break;
					case "Transfer":
						// create an array for account number selection
						String[] accountNums = new String[accounts.length];
						for (int i = 0; i < accounts.length; i++) {
							accountNums[i] = "Account " + accounts[i].getNum();
						}

						String num = (String) JOptionPane.showInputDialog(profileFrame,
								"choose an account to transfer to:", "transfer to", JOptionPane.PLAIN_MESSAGE, null,
								accountNums, accountNums[0] // default selection
						);

						// if user clicks cancel then quit
						if (num == null) {
							return;
						}

						// find the account sending to in the accounts array
						Account To = null;
						// for each account in accounts
						for (Account a : accounts) {
							// if account numbers match save account in To
							if (num.equals("Account " + a.getNum())) {
								To = a;
								break;
							}
						}
						Account From = acc;
						if (To == From) {
							JOptionPane.showMessageDialog(profileFrame, "cannot transfer to the same account");
							return;
						}

						// withdraw + deposit to make the transfer
						From.withdraw(amt);
						To.deposit(amt);

						// update displayed balance of profile already open
						balance.setText("Balance: " + From.getBalance());

						break;

					default:
						JOptionPane.showMessageDialog(profileFrame, "invalid transaction type");
						return;
					}

					// remove the current panel and reopen the main panel
					profileFrame.getContentPane().removeAll();
					profileFrame.add(main, BorderLayout.CENTER);
					// refresh the UI to go back
					profileFrame.revalidate();
					profileFrame.repaint();
				} else {
					JOptionPane.showMessageDialog(null, "select an account from the dropdown menu");
					return;
				}
			}

		});

		// back buttons
		infoBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// remove the current panel and reopen the main panel
				profileFrame.getContentPane().removeAll();
				profileFrame.add(main, BorderLayout.CENTER);
				// refresh the UI to go back
				profileFrame.revalidate();
				profileFrame.repaint();
			}
		});
		createBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// remove the current panel and reopen the main panel
				profileFrame.getContentPane().removeAll();
				profileFrame.add(main, BorderLayout.CENTER);
				// refresh the UI to go back
				profileFrame.revalidate();
				profileFrame.repaint();
			}
		});
		transactionBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// remove the current panel and reopen the main panel
				profileFrame.getContentPane().removeAll();
				profileFrame.add(main, BorderLayout.CENTER);
				// refresh the UI to go back
				profileFrame.revalidate();
				profileFrame.repaint();
			}
		});

		// user chooses to logout
		logout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// close the profile frame and return to customer login
				profileFrame.dispose();
				customerLogin();
			}
		});

		// new account action listeners
		checking.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// add a checking account to the profile
				String value = initBalance.getText();
				try {
				Double amt = Double.parseDouble(value);
				profile.addAccount(AccountType.checking, amt);
				}
				catch(NumberFormatException e2) {
				JOptionPane.showMessageDialog(null, "initial value must be a number");
				return;
				}
				
			}
		});
		saving.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// add a savings account to the profile
				String value = initBalance.getText();
				try {
				Double amt = Double.parseDouble(value);
				profile.addAccount(AccountType.saving, amt);
				}
				catch(NumberFormatException e2) {
				JOptionPane.showMessageDialog(null, "initial value must be a number");
				return;
				}
			}
		});
		loc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// add a line of credit to the profile
				String value = initBalance.getText();
				try {
				Double amt = Double.parseDouble(value);
				profile.addAccount(AccountType.lineOfCredit, amt);
				}
				catch(NumberFormatException e2) {
				JOptionPane.showMessageDialog(null, "initial value must be a number");
				return;
				}
			}
		});

		// update personal info buttons
		update.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// int to check if option 0 is selected
				int check = options.getSelectedIndex();
				// get selected item from options
				String choice = (String) options.getSelectedItem();
				// make sure its not 0
				if (check <= 0) {
					JOptionPane.showMessageDialog(null, "select an option from the dropdown menu");
					return;
				} else {

					//switch to update all options
					switch (choice) {

					case "Name":
						// prompt for new value
						String n = JOptionPane.showInputDialog("Enter name: ");
						// check if user presses cancel
						if (n == null){ 
							return;}
						// change the attribute
						profile.setName(n);
						// update the value for the JLabel
						name.setText("Name: " + profile.getName());
						break;

					case "Address":
						String a = JOptionPane.showInputDialog("Enter address: ");
						if (a == null){ 
							return;}
						profile.setAddress(a);
						address.setText("Address: " + profile.getAddress());
						break;
						
					case "Phone":
						String input = "";
						try {
							input = JOptionPane.showInputDialog("Enter phone number: ");
							// if user presses cancel
							if (input == null) {
								return;
							}
							long p = Long.parseLong(input);
							profile.setPhone(p);
							phone.setText(("Phone: "+profile.getPhone()));
						}
						catch(NumberFormatException e2){
								JOptionPane.showMessageDialog(profileFrame, "phone number may only be numbers");
						}
						break;

					case "Email":
						String eMail = JOptionPane.showInputDialog("Enter email: ");
						if (eMail == null){ 
							return;}
						profile.setEmail(eMail);
						email.setText("Email: " + profile.getEmail());
						break;
						
					case "Username":
						String u = JOptionPane.showInputDialog("Enter username: ");
						if (u == null){ 
							return;}
						profile.setUsername(u);
						username.setText("Username: " + profile.getUsername());
						break;
						
					case "Password":
						String pass = JOptionPane.showInputDialog("Enter password: ");
						if (pass == null){ 
							return;}
						profile.setPassword(pass);
						password.setText("Password: " + profile.getPassword());
						break;
						
					default:
						return;

					}

				}
			}
		});

		// button to change pin
		pin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// create an array for account number selection
				String[] accountNums = new String[accounts.length];
				for (int i = 0; i < accounts.length; i++) {
					accountNums[i] = "Account " + accounts[i].getNum();
				}
				
				// have the user select an account
				String num = (String) JOptionPane.showInputDialog(profileFrame,
						"choose an account to change the pin:", "change pin", JOptionPane.PLAIN_MESSAGE, null,
						accountNums, accountNums[0] // default selection
				);

				// if user clicks cancel then quit
				if (num == null) {
					return;
				}

				// find the account in the accounts array
				Account acc = null;
				// for each account in accounts
				for (Account a : accounts) {
					// if account numbers match save account in acc
					if (num.equals("Account " + a.getNum())) {
						acc = a;
						break;
					}
				}
				// get new pin as input
				int p = 0;
				String input = JOptionPane.showInputDialog("Enter new PIN: ");
				// user presses cancel
				if (input == null) {
					return;
				}
				
				try {
				p = Integer.parseInt(input);
				}
				catch(NumberFormatException e2){
					JOptionPane.showMessageDialog(profileFrame, "pin may only be numbers");
					return;
				}
				acc.setPin(p);
				
				
			}
		});
		// button to show recent history
		history.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// create an array for account number selection
				String[] accountNums = new String[accounts.length];
				for (int i = 0; i < accounts.length; i++) {
					accountNums[i] = "Account " + accounts[i].getNum();
				}
				
				// have the user select an account
				String num = (String) JOptionPane.showInputDialog(profileFrame,
						"choose an account to view the history", "account history", JOptionPane.PLAIN_MESSAGE, null,
						accountNums, accountNums[0] // default selection
				);

				// if user clicks cancel then quit
				if (num == null) {
					return;
				}

				// find the account in the accounts array
				Account acc = null;
				// for each account in accounts
				for (Account a : accounts) {
					// if account numbers match save account in acc
					if (num.equals("Account " + a.getNum())) {
						acc = a;
						break;
					}
				}
				// save log toString in a variable
				String log = acc.getLog();
				// parse the log on ',', store substrings in events
				String[] events = log.split(",");
				// create StringBuilder to append the parts together with newlines
				StringBuilder builder = new StringBuilder();
				// for each event in array, append with a \n
				for (String event : events) {
					builder.append(event).append("\n");
				}
				
				// display log events to user
				JOptionPane.showMessageDialog(null, builder.toString());

			}
		});

		profileFrame.setVisible(true);
	}

}