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
		//TODO maybe remove
	    boolean loggedIn = false;

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
	        boolean valid = teller.customerLogin(customerUser.getText(), customerPass.getText());

	        if (valid) {
	            // if customer login is approved
	            JOptionPane.showMessageDialog(login, "Customer login success");
	            
	            // call showProfile
	            showProfile();
	            
	            
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
	
	public static void showProfile() {
		
	}

	
	
	// displays account or profile information
	private void viewInfo(String source) {

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
