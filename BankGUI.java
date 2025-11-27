package group3;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

import javax.swing.JButton;
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
		}
		else if (atm != null) {
			
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
		Boolean employeeloggedIn = false;
		Boolean customerloggedIn = false;
		Boolean quit = false;

		// loop for entire system if quit is true GUI closes
		while (!quit) {

			// until employee is logged in
			while (!employeeloggedIn) {
				employeeloggedIn = loginScreen("employee");
			}

			// until customer is logged in
			while (!customerloggedIn) {
				customerloggedIn = loginScreen("employee");
			}

			// once all login is complete, display customer profile screen (show name+list
			// of accounts)

			// buttons for view info, view account (drop down menu), or open new account

		}
	}

	///////////////////////////////
	// shows ATM specific screen //
	///////////////////////////////
	private static void startATM() {

	}

	// login screen for employees or customers
	private static boolean loginScreen(String role) {
		boolean loggedin = false;

		// create login screen frame
		JFrame employeeLogin = new JFrame("Login");
		employeeLogin.setVisible(true);
		// frame closes by the X button
		employeeLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// height by width in pixels
		employeeLogin.setSize(1000, 500);
		// relative to null is center of screen
		employeeLogin.setLocationRelativeTo(null);
		
		// create login screen panel
		JPanel panel = new JPanel();
	    JTextField enterUser = new JTextField(20);
	    JTextField enterPass = new JTextField(20);
	    // pass the text for the button as an argument
	    JButton loginButton = new JButton("Login");
	    
	    panel.setLayout(new FlowLayout());
	    panel.add(new JLabel("Employee Login"));
	    panel.add(enterUser);
	    panel.add(enterPass);

		// employee login sequence
		if (role.equals("employee")) {
			
			// login button calls employee login on the teller application with user + pass
			loginButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					teller.employeeLogin(enterUser.getText(), enterPass.getText());}});
			// return
			return loggedin;
		}

		// customer login sequence
		if (role.equals("customer")) {
		
			// login button calls employee login on the teller application with user + pass
			loginButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					teller.customerLogin(enterUser.getText(), enterPass.getText());}});
			//return
			return loggedin;
		}
		// return
		return loggedin;
	}

	// displays account or profile information
	private void viewInfo(String source) {

	}

	// panel is the flat piece that lives in the frame
	// creates panel
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
