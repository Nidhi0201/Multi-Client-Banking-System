package group3;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BankGUI {
	Profile profile;
	Account account;
	Scanner scan;
	
	public BankGUI() {
		scan = new Scanner(System.in);
	}

	//////////////////////
	//   Example Code 	//
	//////////////////////
	
	
	// create a frame
	   // frame is an outside border defines a boundary (window)
	   private static void startGUI() {   
		   Boolean loggedIn = false;
		   Boolean quit = false;
		   
		  // loop for entire system if quit = q GUI closes
		  while(!quit) {
			  
			// until employee is logged in
			  while (!loggedIn) {
				// created a frame, string passed is window title
			      JFrame employeeLogin = new JFrame("Welcome Employee");
			      employeeLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			      createUI(employeeLogin); 
			      
			    // close employee login
			  }
			  
			// after employee login open cusomter login
		      JFrame customerLogin = new JFrame("Welcome Customer");
		      customerLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		      createUI(customerLogin); 
			  
			  
		  }
		  
		  JFrame frame = new JFrame("Welcome Customer");
		  
	      


	      
	      createUI(frame); 
	      
	      // height by width in pixels
	      frame.setSize(560, 200);     
	      
	      // how do i want the window relatively; relative to null is center of screen
	      frame.setLocationRelativeTo(null);
	      
	      // set the frame attribute to true; makes it visible
	      // if its invisible you can make it visible when needed
	      frame.setVisible(true);	
	   }

	   
	   
	   // panel is the flat piece that lives in the frame
	   // creates panetl
	   private static void createUI(final JFrame frame){  
		   
		  // create panel
	      JPanel panel = new JPanel();
	      
	      // this affects how assets populate the panel
	      LayoutManager layout = new FlowLayout();  // comment out: To try alternate layouts
	      //LayoutManager layout = new GridLayout();  // uncomment: To try alternate layouts
	      
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
	      //cancelButton.setEnabled(true);
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
	             System.exit(0);	// End program
	          }
	       });

	      
	      // the "active" button by default what works if u press enter
	      frame.getRootPane().setDefaultButton(submitButton); //Default button focus

	      panel.add(okButton);
	      panel.add(cancelButton);
	      panel.add(submitButton);
	      panel.add(textField); // add the textfield from above
	      panel.add(exitButton); //add the exit button

	      // this is how we add a panel to the frame after all the details
	      frame.getContentPane().add(panel, BorderLayout.CENTER);    
	   }
	}

	
	
	


