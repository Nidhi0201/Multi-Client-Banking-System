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
		// if no client is recieved
		else {
			// TODO not sure what do to
		}
	}

	//////////////////////////////////
	// shows teller specific screen //
	//////////////////////////////////
	private static void startTeller() {
		// must first login employee and customer at teller
		loginScreen();

		// once all login is complete, display customer profile screen (show name+list
		// of accounts)

		// buttons for view info, view account (drop down menu), or open new account

	}

	///////////////////////////////
	// shows ATM specific screen //
	///////////////////////////////
	private static void startATM() {

	}

	// login screen for employees then customers
	private static void loginScreen() {

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

		// add employee panel to frame
		login.add(employeePanel, BorderLayout.CENTER);

		// employee login sequence
		ELB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// calls employee login on the teller application with user + pass
				boolean valid = teller.employeeLogin(employeeUser.getText(), employeePass.getText());

				// if employee login is approved
				if (valid) {
					// move on to customer login panel
					login.remove(employeePanel);
					login.add(customerPanel, BorderLayout.CENTER);
					// refresh the UI for the new panel
					login.revalidate();
					login.repaint();
				}
				// if employee login is not approved TODO start over?
				else {
					JOptionPane.showMessageDialog(login, "Employee login failed");
				}
			}
		});

		// customer login sequence
		CLB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// calls customer login on the teller application with user + pass
				Profile profile = teller.customerLogin(customerUser.getText(), customerPass.getText());
				if (profile != null) { // TODO change to == for testing
					// if customer login is approved close the panel and frame
					JOptionPane.showMessageDialog(login, "Customer login success");
					login.remove(customerPanel);
					login.dispose();

					// call showProfile
					showProfile(profile);

					login.dispose();
				} else {
					// if customer login is not approved TODO start over?
					JOptionPane.showMessageDialog(login, "Customer login failed");
				}
			}
		});

		// set the frame attribute to true; makes it visible
		login.setVisible(true);
	}

	public static void showProfile(Profile profile) {
		// store accounts associated with the profile
		Account[] accounts = new Account[0];//profile.getAccounts();
		Object[] accountMenu = new Object[0]; // account array with prompt at index 0 for dropdown menu


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
		JComboBox<Object> selector = new JComboBox<>(accountMenu);
		//selector.setSelectedIndex(0); // TODO show default first
		// add header at the north spot
		main.add(welcome, BorderLayout.NORTH);

		// panel with gridlayout for buttons, with specified rows and columns
		JPanel buttons = new JPanel(new GridLayout(4, 1));
		buttons.add(info);
		buttons.add(newAccount);
		buttons.add(openAccount);
		buttons.add(selector);
		// add button panel to main panel
		main.add(buttons, BorderLayout.CENTER);
		// add main panel to the frame
		profileFrame.add(main, BorderLayout.CENTER);



		// Panel for profile info
		JPanel infoPanel = new JPanel();
		main.setLayout(new BorderLayout());
		JLabel name = new JLabel("Name: " + profile.getName());
		JLabel address = new JLabel("Address: " + profile.getAddress());
		JLabel phone = new JLabel("Phone: " + profile.getPhone());
		JLabel email = new JLabel("Email: " + profile.getEmail());
		JLabel score = new JLabel("Credit Score: " + profile.getCreditScore());
		JLabel username = new JLabel("Username: " + profile.getUsername());
		JLabel password = new JLabel("Password: " + profile.getPassword());
		JButton update = new JButton("update personal info");
		// String options for dropdown menu
		String[] infoMenu = {"make a selection","Name","Address","Phone","Email","Username","Password"};
		JComboBox<String> options = new JComboBox<>(infoMenu);
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





		// Panel for creating a new account
		JPanel createPanel = new JPanel();
		createPanel.setLayout(new BorderLayout());
		JLabel message = new JLabel("Enter initial amount and select an account type");
		JTextField initBalance = new JTextField(11);
		JButton checking = new JButton("Checking");
		JButton saving = new JButton("Saving");
		JButton loc = new JButton("Line of Credit");
		// add components to panel
		createPanel.add(message);
		createPanel.add(initBalance);
		createPanel.add(checking);
		createPanel.add(saving);
		createPanel.add(loc);




		// Panel for viewing existing account
		JPanel accountPanel = new JPanel();
		JLabel number = new JLabel("Account "+": "); // + account number TODO
		JLabel balance = new JLabel(""); // + balance TODO
		JButton pin = new JButton("Change Pin"); // TODO may remove
		JButton history = new JButton("View Account History");
		JTextField amount = new JTextField(11);
		// set options equal to new menu // TODO be sure its closed before its used
		String[] transactionMenu = {"select a transaction type","Deposit","Withdraw","Transfer"};
		options = new JComboBox<>(transactionMenu);
		options.setSelectedIndex(0); // shows prompt as default
		JButton transaction = new JButton("Make Transaction");
		accountPanel.add(number);
		accountPanel.add(balance);
		accountPanel.add(pin);
		accountPanel.add(history);
		accountPanel.add(amount);
		accountPanel.add(options); // TODO be sure this is not in use
		accountPanel.add(transaction);



		info.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// display information panel
				profileFrame.remove(main);
				profileFrame.add(infoPanel, BorderLayout.CENTER);
				// refresh the UI for the new panel
				profileFrame.revalidate();
				profileFrame.repaint();

			}});

		newAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// display new account form panel
				profileFrame.remove(main);
				profileFrame.add(createPanel, BorderLayout.CENTER);
				// refresh the UI for the new panel
				profileFrame.revalidate();
				profileFrame.repaint();
			}});

		openAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// display account panel with number + balance + buttons
				profileFrame.remove(main);
				profileFrame.add(accountPanel, BorderLayout.CENTER);
				// refresh the UI for the new panel
				profileFrame.revalidate();
				profileFrame.repaint();
			}});


		profileFrame.setVisible(true);
	}











	// createUI for reference
	private static void createUI(final JFrame frame) {

		// create panel
		JPanel panel = new JPanel();

		// this affects how assets populate the panel
		LayoutManager layout = new FlowLayout(); // comment out: To try alternate layouts
		// LayoutManager layout = new GridLayout(); // uncomment: To try alternate
		// layouts

		// apply layout to panel
		panel.setLayout(layout);

		// the assets, buttons and

		// default of 20 characters
		JTextField textField = new JTextField(20); // create a textfield

		// pass the text for the button as an argument
		JButton okButton = new JButton("Ok");
		JButton exitButton = new JButton("Exit");
		JButton cancelButton = new JButton("Cancel");

		// not enabled is grayed out, not accesible
		cancelButton.setEnabled(false);
		JButton submitButton = new JButton("Submit");

		// put desired outputs in action listeners?
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// when they click ok, it makes the cancel button available
				JOptionPane.showMessageDialog(frame, "Ok Button clicked. Cancel Enabled");
				cancelButton.setEnabled(true);
			}
		});

		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String str = "Submit Button clicked. Text: " + textField.getText();
				JOptionPane.showMessageDialog(frame, str);
				textField.setText("");
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, "Cancel Button clicked.");
				cancelButton.setEnabled(false);
			}
		});

		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, "Goodbye");
				System.exit(0); // End program
			}
		});

		// the "active" button by default what works if u press enter
		frame.getRootPane().setDefaultButton(submitButton); // Default button focus

		panel.add(okButton);
		panel.add(cancelButton);
		panel.add(submitButton);
		panel.add(textField); // add the textfield from above
		panel.add(exitButton); // add the exit button

		// this is how we add a panel to the frame after all the details
		frame.getContentPane().add(panel, BorderLayout.CENTER);
	}
}
