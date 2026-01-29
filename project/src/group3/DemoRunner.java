package group3;

/**
 * Headless demo runner to show server and clients (Teller + ATM)
 * talking to each other without any Swing UI.
 *
 * This is meant for environments like Cursor where GUI windows
 * cannot be displayed.
 */
public class DemoRunner {

	public static void main(String[] args) throws Exception {
		// Start the server in a background daemon thread
		Thread serverThread = new Thread(() -> {
			try {
				Server.main(new String[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, "BankServer-Thread");
		serverThread.setDaemon(true);
		serverThread.start();

		// Give the server a moment to start listening
		Thread.sleep(1500);

		System.out.println("=== DEMO: Teller connecting to server ===");
		Teller teller = new Teller("127.0.0.1");
		System.out.println("Teller connected: " + teller.isConnected());

		// Employee login
		boolean empLogin = teller.employeeLogin("employee1", "employee1");
		System.out.println("Employee login (employee1): " + empLogin);

		// Customer login for demo profile user1/pass1
		Profile profile = teller.customerLogin("user1", "pass1");
		System.out.println("Customer login (user1): " + (profile != null));

		// Perform a teller-side deposit on account 2223 if connected
		if (teller.isConnected()) {
			System.out.println("Teller depositing 50.0 to account 2223...");
			boolean tellerDeposit = teller.deposit("2223", 50.0);
			System.out.println("Teller deposit result: " + tellerDeposit);
		}

		// Now demonstrate ATM against same server/account
		System.out.println("\n=== DEMO: ATM connecting to server ===");
		ATM atm = new ATM("127.0.0.1");
		System.out.println("ATM connected: " + atm.isConnected());

		// Account 2223 with PIN 1163 (from accounts.txt)
		boolean atmLogin = atm.customerLogin("2223", "1163");
		System.out.println("ATM login (account 2223): " + atmLogin);

		if (atmLogin) {
			double balanceBefore = atm.checkBalance();
			System.out.println("ATM balance before deposit: " + balanceBefore);

			System.out.println("ATM depositing 25.0 to account 2223...");
			boolean atmDeposit = atm.deposit(25.0);
			System.out.println("ATM deposit result: " + atmDeposit);

			double balanceAfter = atm.checkBalance();
			System.out.println("ATM balance after deposit: " + balanceAfter);

			atm.logout();
		}

		// Clean up client connections
		teller.logout();
		teller.disconnect();
		atm.disconnect();

		System.out.println("\nDemo complete. Check the server's src/group3/log.txt for all logged activity.");
	}
}

