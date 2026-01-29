package group3;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

/**
 * REST API Server that wraps existing banking logic for web frontend.
 * Runs on port 8080 and exposes JSON endpoints.
 */
public class RestApiServer {
	// Data directory - uses DATA_DIR env var or defaults to src/group3
	private static String dataDir = System.getenv("DATA_DIR") != null 
		? System.getenv("DATA_DIR") 
		: "src/group3";
	
	private static File logFile = new File(dataDir + "/log.txt");
	private static File employeeFile = new File(dataDir + "/employees.txt");
	private static File proFile = new File(dataDir + "/profiles.txt");
	private static File accountFile = new File(dataDir + "/accounts.txt");
	
	private static Gson gson = new Gson();
	// Store active sessions (in production, use proper session management)
	private static Map<String, SessionData> sessions = new ConcurrentHashMap<>();
	
	public static void main(String[] args) throws IOException {
		// Use PORT env var (for Railway/Render) or default to 8080
		int port = 8080;
		String envPort = System.getenv("PORT");
		if (envPort != null && !envPort.isEmpty()) {
			try {
				port = Integer.parseInt(envPort);
			} catch (NumberFormatException e) {
				System.out.println("Invalid PORT, using default 8080");
			}
		}
		
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		
		// Enable CORS for all routes
		server.createContext("/api", new CorsHandler(new ApiHandler()));
		
		server.setExecutor(null);
		server.start();
		System.out.println("REST API Server running on port " + port);
		System.out.println("CORS enabled for web frontend");
		System.out.println("Data directory: " + dataDir);
	}
	
	// CORS wrapper to allow web frontend to call API
	static class CorsHandler implements HttpHandler {
		private HttpHandler handler;
		
		public CorsHandler(HttpHandler handler) {
			this.handler = handler;
		}
		
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
			exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
			exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
			
			if ("OPTIONS".equals(exchange.getRequestMethod())) {
				exchange.sendResponseHeaders(200, -1);
				exchange.close();
				return;
			}
			
			handler.handle(exchange);
		}
	}
	
	static class ApiHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			String path = exchange.getRequestURI().getPath();
			String method = exchange.getRequestMethod();
			
			try {
				if (path.equals("/api/auth/employee-login") && method.equals("POST")) {
					handleEmployeeLogin(exchange);
				} else if (path.equals("/api/auth/customer-login") && method.equals("POST")) {
					handleCustomerLogin(exchange);
				} else if (path.equals("/api/auth/atm-login") && method.equals("POST")) {
					handleAtmLogin(exchange);
				} else if (path.equals("/api/auth/logout") && method.equals("POST")) {
					handleLogout(exchange);
				} else if (path.equals("/api/accounts") && method.equals("GET")) {
					handleGetAccounts(exchange);
				} else if (path.equals("/api/accounts/deposit") && method.equals("POST")) {
					handleDeposit(exchange);
				} else if (path.equals("/api/accounts/withdraw") && method.equals("POST")) {
					handleWithdraw(exchange);
				} else if (path.equals("/api/accounts/balance") && method.equals("GET")) {
					handleGetBalance(exchange);
				} else if (path.equals("/api/accounts/update-pin") && method.equals("POST")) {
					handleUpdatePin(exchange);
				} else if (path.equals("/api/profiles/create") && method.equals("POST")) {
					handleCreateProfile(exchange);
				} else if (path.equals("/api/accounts/create") && method.equals("POST")) {
					handleCreateAccount(exchange);
				} else if (path.equals("/api/logs") && method.equals("GET")) {
					handleGetLogs(exchange);
				} else if (path.equals("/api/accounts/search") && method.equals("GET")) {
					handleSearchAccount(exchange);
				} else if (path.equals("/api/accounts/link") && method.equals("POST")) {
					handleLinkAccountToProfile(exchange);
				} else if (path.equals("/api/profiles/search") && method.equals("GET")) {
					handleSearchProfile(exchange);
				} else if (path.equals("/api/profiles/update") && method.equals("POST")) {
					handleUpdateProfile(exchange);
				} else {
					sendJsonResponse(exchange, 404, Map.of("error", "Not found"));
				}
			} catch (Exception e) {
				e.printStackTrace();
				sendJsonResponse(exchange, 500, Map.of("error", e.getMessage()));
			}
		}
		
		private void handleEmployeeLogin(HttpExchange exchange) throws IOException {
			JsonObject body = readJsonBody(exchange);
			String username = body.get("username").getAsString();
			String password = body.get("password").getAsString();
			
			String[] lines = parse(employeeFile);
			boolean valid = false;
			for (String line : lines) {
				String[] creds = line.split(",", 2);
				if (creds.length >= 2 && username.equals(creds[0]) && password.equals(creds[1])) {
					valid = true;
					break;
				}
			}
			
			if (valid) {
				String sessionId = generateSessionId();
				sessions.put(sessionId, new SessionData("employee", username));
				LogEntry.appendToLog(logFile, new LogEntry(0, LogType.login,
						"Employee login via REST API: " + username,
						java.time.LocalDateTime.now().toString()));
				sendJsonResponse(exchange, 200, Map.of("success", true, "sessionId", sessionId, "role", "employee"));
			} else {
				sendJsonResponse(exchange, 401, Map.of("success", false, "error", "Invalid credentials"));
			}
		}
		
		private void handleCustomerLogin(HttpExchange exchange) throws IOException {
			JsonObject body = readJsonBody(exchange);
			String username = body.get("username").getAsString();
			String password = body.get("password").getAsString();
			
			String[] lines = parse(proFile);
			Profile profile = null;
			for (String line : lines) {
				String[] fields = line.split(",");
				if (fields.length >= 2 && username.equals(fields[0]) && password.equals(fields[1])) {
					String name = fields.length > 2 ? fields[2] : "";
					long phone = fields.length > 3 ? Long.parseLong(fields[3]) : 0;
					String address = fields.length > 4 ? fields[4] : "";
					String email = fields.length > 5 ? fields[5] : "";
					profile = new Profile(name, username, password, phone, address, email);
					// Load accounts for this profile
					profile.loadAccounts(extractAccountNumbers(line));
					break;
				}
			}
			
			if (profile != null) {
				String sessionId = generateSessionId();
				sessions.put(sessionId, new SessionData("customer", username, profile));
				LogEntry.appendToLog(logFile, new LogEntry(0, LogType.login,
						"Customer login via REST API: " + username,
						java.time.LocalDateTime.now().toString()));
				Map<String, Object> response = new HashMap<>();
				response.put("success", true);
				response.put("sessionId", sessionId);
				response.put("role", "customer");
				response.put("profile", profileToMap(profile));
				sendJsonResponse(exchange, 200, response);
			} else {
				sendJsonResponse(exchange, 401, Map.of("success", false, "error", "Invalid credentials"));
			}
		}
		
		private void handleAtmLogin(HttpExchange exchange) throws IOException {
			JsonObject body = readJsonBody(exchange);
			String accountNumber = body.get("accountNumber").getAsString();
			String pin = body.get("pin").getAsString();
			
			String[] lines = parse(accountFile);
			Map<String, Object> accountData = null;
			for (String line : lines) {
				String[] data = line.split(",");
				if (data.length >= 4 && data[0].equals(accountNumber) && data[1].equals(pin)) {
					accountData = new HashMap<>();
					accountData.put("accountNumber", Integer.parseInt(data[0]));
					accountData.put("pin", data[1]);
					accountData.put("type", data[2]);
					accountData.put("balance", Double.parseDouble(data[3]));
					break;
				}
			}
			
			if (accountData != null) {
				String sessionId = generateSessionId();
				// Store ATM session with account number
				SessionData atmSession = new SessionData("atm", accountNumber);
				atmSession.atmAccountNumber = accountNumber;
				sessions.put(sessionId, atmSession);
				LogEntry.appendToLog(logFile, new LogEntry(Integer.parseInt(accountNumber), LogType.login,
						"ATM login via REST API",
						java.time.LocalDateTime.now().toString()));
				Map<String, Object> response = new HashMap<>();
				response.put("success", true);
				response.put("sessionId", sessionId);
				response.put("role", "atm");
				response.put("account", accountData);
				sendJsonResponse(exchange, 200, response);
			} else {
				sendJsonResponse(exchange, 401, Map.of("success", false, "error", "Invalid account number or PIN"));
			}
		}
		
		private void handleLogout(HttpExchange exchange) throws IOException {
			String sessionId = getSessionId(exchange);
			if (sessionId != null && sessions.containsKey(sessionId)) {
				sessions.remove(sessionId);
				LogEntry.appendToLog(logFile, new LogEntry(0, LogType.logout,
						"Logout via REST API",
						java.time.LocalDateTime.now().toString()));
				sendJsonResponse(exchange, 200, Map.of("success", true));
			} else {
				sendJsonResponse(exchange, 401, Map.of("success", false, "error", "Not logged in"));
			}
		}
		
		private void handleGetAccounts(HttpExchange exchange) throws IOException {
			String sessionId = getSessionId(exchange);
			SessionData session = sessions.get(sessionId);
			if (session == null) {
				sendJsonResponse(exchange, 401, Map.of("error", "Not authenticated"));
				return;
			}
			
			// For customers, return only their linked accounts
			if ("customer".equals(session.role)) {
				// Get customer's linked accounts from profile
				String[] profileLines = parse(proFile);
				java.util.Set<String> linkedAccounts = new java.util.HashSet<>();
				
				for (String line : profileLines) {
					String[] fields = line.split(",");
					if (fields.length >= 2 && fields[0].equals(session.username)) {
						// Parse accounts array [acc1,acc2,...]
						String accountsField = fields.length > 7 ? fields[7] : "[]";
						if (!accountsField.equals("[]") && accountsField.length() > 2) {
							String[] accNums = accountsField.substring(1, accountsField.length() - 1).split(",");
							for (String acc : accNums) {
								linkedAccounts.add(acc.trim());
							}
						}
						break;
					}
				}
				
				// Load only the linked accounts
				String[] accountLines = parse(accountFile);
				java.util.List<Map<String, Object>> accountList = new java.util.ArrayList<>();
				
				for (String line : accountLines) {
					String[] data = line.split(",");
					if (data.length >= 4 && linkedAccounts.contains(data[0])) {
						try {
							Map<String, Object> accountMap = new HashMap<>();
							accountMap.put("accountNumber", Integer.parseInt(data[0]));
							accountMap.put("pin", data[1]);
							accountMap.put("type", data[2]);
							accountMap.put("balance", Double.parseDouble(data[3]));
							accountList.add(accountMap);
						} catch (Exception e) {
							// Skip invalid lines
						}
					}
				}
				
				sendJsonResponse(exchange, 200, Map.of("accounts", accountList.toArray()));
				return;
			}
			
			// For ATM sessions, return only the logged-in account
			if ("atm".equals(session.role) && session.atmAccountNumber != null) {
				String[] accountLines = parse(accountFile);
				java.util.List<Map<String, Object>> accountList = new java.util.ArrayList<>();
				
				for (String line : accountLines) {
					String[] data = line.split(",");
					if (data.length >= 4 && data[0].equals(session.atmAccountNumber)) {
						try {
							Map<String, Object> accountMap = new HashMap<>();
							accountMap.put("accountNumber", Integer.parseInt(data[0]));
							accountMap.put("pin", data[1]);
							accountMap.put("type", data[2]);
							accountMap.put("balance", Double.parseDouble(data[3]));
							accountList.add(accountMap);
						} catch (Exception e) {
							// Skip invalid lines
						}
						break;
					}
				}
				
				sendJsonResponse(exchange, 200, Map.of("accounts", accountList.toArray()));
				return;
			}
			
			// For employees, return empty by default (they should search instead)
			sendJsonResponse(exchange, 200, Map.of("accounts", new Object[0]));
		}
		
		private void handleDeposit(HttpExchange exchange) throws IOException {
			JsonObject body = readJsonBody(exchange);
			String accountNum = body.get("accountNumber").getAsString();
			double amount = body.get("amount").getAsDouble();
			
			boolean success = fileDeposit(accountNum, amount);
			if (success) {
				LogEntry.appendToLog(logFile, new LogEntry(Integer.parseInt(accountNum), LogType.deposit,
						"REST API deposit: " + amount,
						java.time.LocalDateTime.now().toString()));
				sendJsonResponse(exchange, 200, Map.of("success", true, "message", "Deposit successful"));
			} else {
				sendJsonResponse(exchange, 400, Map.of("success", false, "error", "Deposit failed"));
			}
		}
		
		private void handleWithdraw(HttpExchange exchange) throws IOException {
			JsonObject body = readJsonBody(exchange);
			String accountNum = body.get("accountNumber").getAsString();
			double amount = body.get("amount").getAsDouble();
			
			boolean success = fileWithdrawal(accountNum, amount);
			if (success) {
				LogEntry.appendToLog(logFile, new LogEntry(Integer.parseInt(accountNum), LogType.withdrawal,
						"REST API withdrawal: " + amount,
						java.time.LocalDateTime.now().toString()));
				sendJsonResponse(exchange, 200, Map.of("success", true, "message", "Withdrawal successful"));
			} else {
				sendJsonResponse(exchange, 400, Map.of("success", false, "error", "Withdrawal failed"));
			}
		}
		
		private void handleGetBalance(HttpExchange exchange) throws IOException {
			String accountNum = exchange.getRequestURI().getQuery().split("=")[1];
			String[] lines = parse(accountFile);
			for (String line : lines) {
				String[] data = line.split(",");
				if (data.length >= 4 && accountNum.equals(data[0])) {
					double balance = Double.parseDouble(data[3]);
					sendJsonResponse(exchange, 200, Map.of("balance", balance));
					return;
				}
			}
			sendJsonResponse(exchange, 404, Map.of("error", "Account not found"));
		}
		
		private void handleUpdatePin(HttpExchange exchange) throws IOException {
			JsonObject body = readJsonBody(exchange);
			String accountNum = body.get("accountNumber").getAsString();
			String newPin = body.get("pin").getAsString();
			
			boolean success = writePin(accountNum, newPin);
			if (success) {
				LogEntry.appendToLog(logFile, new LogEntry(Integer.parseInt(accountNum), LogType.updateAccount,
						"REST API PIN update",
						java.time.LocalDateTime.now().toString()));
				sendJsonResponse(exchange, 200, Map.of("success", true));
			} else {
				sendJsonResponse(exchange, 400, Map.of("success", false, "error", "PIN update failed"));
			}
		}
		
		private void handleCreateProfile(HttpExchange exchange) throws IOException {
			JsonObject body = readJsonBody(exchange);
			String name = body.get("name").getAsString();
			String username = body.get("username").getAsString();
			String password = body.get("password").getAsString();
			long phone = body.get("phone").getAsLong();
			String address = body.get("address").getAsString();
			String email = body.get("email").getAsString();
			
			String profileLine = username + "," + password + "," + name + "," + phone + "," + address + "," + email + ",0,[]";
			try (PrintWriter writer = new PrintWriter(new FileWriter(proFile, true))) {
				writer.println(profileLine);
				sendJsonResponse(exchange, 200, Map.of("success", true));
			} catch (Exception e) {
				sendJsonResponse(exchange, 500, Map.of("success", false, "error", e.getMessage()));
			}
		}
		
		private void handleCreateAccount(HttpExchange exchange) throws IOException {
			// Check if employee
			String sessionId = getSessionId(exchange);
			SessionData session = sessions.get(sessionId);
			if (session == null || !"employee".equals(session.role)) {
				sendJsonResponse(exchange, 403, Map.of("success", false, "error", "Only employees can create accounts"));
				return;
			}
			
			JsonObject body = readJsonBody(exchange);
			String accountNum = body.get("accountNumber").getAsString();
			String pin = body.get("pin").getAsString();
			String type = body.get("type").getAsString();
			double balance = body.get("initialBalance").getAsDouble();
			
			// Check if account already exists
			String[] lines = parse(accountFile);
			for (String line : lines) {
				String[] data = line.split(",");
				if (data.length >= 1 && accountNum.equals(data[0])) {
					sendJsonResponse(exchange, 400, Map.of("success", false, "error", "Account number already exists"));
					return;
				}
			}
			
			// Create account line: accountNum,pin,type,balance[,initialBalance for LOC]
			String accountLine = accountNum + "," + pin + "," + type + "," + balance;
			if ("lineOfCredit".equals(type)) {
				accountLine += "," + balance; // initialBalance for LOC
			}
			
			try (PrintWriter writer = new PrintWriter(new FileWriter(accountFile, true))) {
				writer.println(accountLine);
				LogEntry.appendToLog(logFile, new LogEntry(Integer.parseInt(accountNum), LogType.updateAccount,
						"REST API created account: " + type + " with balance " + balance,
						java.time.LocalDateTime.now().toString()));
				sendJsonResponse(exchange, 200, Map.of("success", true, "message", "Account created successfully"));
			} catch (Exception e) {
				sendJsonResponse(exchange, 500, Map.of("success", false, "error", e.getMessage()));
			}
		}
		
		private void handleGetLogs(HttpExchange exchange) throws IOException {
			String[] lines = parse(logFile);
			sendJsonResponse(exchange, 200, Map.of("logs", lines));
		}
		
		private void handleSearchAccount(HttpExchange exchange) throws IOException {
			// Employee-only endpoint to search for an account by number
			String sessionId = getSessionId(exchange);
			SessionData session = sessions.get(sessionId);
			if (session == null || !"employee".equals(session.role)) {
				sendJsonResponse(exchange, 403, Map.of("error", "Only employees can search accounts"));
				return;
			}
			
			String query = exchange.getRequestURI().getQuery();
			if (query == null || !query.contains("accountNumber=")) {
				sendJsonResponse(exchange, 400, Map.of("error", "Missing accountNumber parameter"));
				return;
			}
			String accountNum = query.split("accountNumber=")[1].split("&")[0];
			
			// Search for the account
			String[] accountLines = parse(accountFile);
			Map<String, Object> accountData = null;
			for (String line : accountLines) {
				String[] data = line.split(",");
				if (data.length >= 4 && data[0].equals(accountNum)) {
					accountData = new HashMap<>();
					accountData.put("accountNumber", Integer.parseInt(data[0]));
					accountData.put("pin", data[1]);
					accountData.put("type", data[2]);
					accountData.put("balance", Double.parseDouble(data[3]));
					break;
				}
			}
			
			if (accountData == null) {
				sendJsonResponse(exchange, 404, Map.of("found", false, "error", "Account not found"));
				return;
			}
			
			// Find associated profile (if any)
			String[] profileLines = parse(proFile);
			Map<String, Object> profileData = null;
			for (String line : profileLines) {
				String[] fields = line.split(",");
				if (fields.length >= 8) {
					// Check if this profile has this account linked
					String accountsField = fields.length > 7 ? fields[7] : "[]";
					if (accountsField.contains(accountNum)) {
						profileData = new HashMap<>();
						profileData.put("username", fields[0]);
						profileData.put("name", fields.length > 2 ? fields[2] : "");
						profileData.put("phone", fields.length > 3 ? fields[3] : "");
						profileData.put("address", fields.length > 4 ? fields[4] : "");
						profileData.put("email", fields.length > 5 ? fields[5] : "");
						profileData.put("creditScore", fields.length > 6 ? fields[6] : "0");
						break;
					}
				}
			}
			
			Map<String, Object> response = new HashMap<>();
			response.put("found", true);
			response.put("account", accountData);
			response.put("profile", profileData);
			sendJsonResponse(exchange, 200, response);
		}
		
		private void handleSearchProfile(HttpExchange exchange) throws IOException {
			// Employee-only endpoint to search for a profile by username
			String sessionId = getSessionId(exchange);
			SessionData session = sessions.get(sessionId);
			if (session == null || !"employee".equals(session.role)) {
				sendJsonResponse(exchange, 403, Map.of("error", "Only employees can search profiles"));
				return;
			}
			
			String query = exchange.getRequestURI().getQuery();
			if (query == null || !query.contains("username=")) {
				sendJsonResponse(exchange, 400, Map.of("error", "Missing username parameter"));
				return;
			}
			String username = query.split("username=")[1].split("&")[0];
			
			String[] profileLines = parse(proFile);
			for (String line : profileLines) {
				String[] fields = line.split(",");
				if (fields.length >= 2 && fields[0].equals(username)) {
					Map<String, Object> profileData = new HashMap<>();
					profileData.put("username", fields[0]);
					profileData.put("name", fields.length > 2 ? fields[2] : "");
					profileData.put("phone", fields.length > 3 ? fields[3] : "");
					profileData.put("address", fields.length > 4 ? fields[4] : "");
					profileData.put("email", fields.length > 5 ? fields[5] : "");
					profileData.put("creditScore", fields.length > 6 ? fields[6] : "0");
					profileData.put("linkedAccounts", fields.length > 7 ? fields[7] : "[]");
					sendJsonResponse(exchange, 200, Map.of("found", true, "profile", profileData));
					return;
				}
			}
			
			sendJsonResponse(exchange, 404, Map.of("found", false, "error", "Profile not found"));
		}
		
		private void handleUpdateProfile(HttpExchange exchange) throws IOException {
			// Employee-only endpoint to update profile information
			String sessionId = getSessionId(exchange);
			SessionData session = sessions.get(sessionId);
			if (session == null || !"employee".equals(session.role)) {
				sendJsonResponse(exchange, 403, Map.of("error", "Only employees can update profiles"));
				return;
			}
			
			JsonObject body = readJsonBody(exchange);
			String username = body.get("username").getAsString();
			String name = body.has("name") ? body.get("name").getAsString() : null;
			String phone = body.has("phone") ? body.get("phone").getAsString() : null;
			String address = body.has("address") ? body.get("address").getAsString() : null;
			String email = body.has("email") ? body.get("email").getAsString() : null;
			String creditScore = body.has("creditScore") ? body.get("creditScore").getAsString() : null;
			String newPassword = body.has("password") && !body.get("password").isJsonNull() 
				? body.get("password").getAsString() : null;
			
			String[] profileLines = parse(proFile);
			boolean found = false;
			
			for (int i = 0; i < profileLines.length; i++) {
				String[] fields = profileLines[i].split(",");
				if (fields.length >= 2 && fields[0].equals(username)) {
					found = true;
					
					// Update fields - profile format: username,password,name,phone,address,email,creditScore,accounts
					String updatedPassword = (newPassword != null && !newPassword.isEmpty()) ? newPassword : fields[1];
					String updatedName = (name != null) ? name : (fields.length > 2 ? fields[2] : "");
					String updatedPhone = (phone != null) ? phone : (fields.length > 3 ? fields[3] : "");
					String updatedAddress = (address != null) ? address : (fields.length > 4 ? fields[4] : "");
					String updatedEmail = (email != null) ? email : (fields.length > 5 ? fields[5] : "");
					String updatedCreditScore = (creditScore != null) ? creditScore : (fields.length > 6 ? fields[6] : "0");
					String linkedAccounts = fields.length > 7 ? fields[7] : "[]";
					
					profileLines[i] = username + "," + updatedPassword + "," + updatedName + "," + 
						updatedPhone + "," + updatedAddress + "," + updatedEmail + "," + 
						updatedCreditScore + "," + linkedAccounts;
					break;
				}
			}
			
			if (!found) {
				sendJsonResponse(exchange, 404, Map.of("success", false, "error", "Profile not found"));
				return;
			}
			
			// Write updated profiles back
			try (PrintWriter writer = new PrintWriter(proFile)) {
				for (String line : profileLines) {
					writer.println(line);
				}
				LogEntry.appendToLog(logFile, new LogEntry(0, LogType.updateAccount,
						"REST API updated profile: " + username,
						java.time.LocalDateTime.now().toString()));
				sendJsonResponse(exchange, 200, Map.of("success", true, "message", "Profile updated successfully"));
			} catch (Exception e) {
				sendJsonResponse(exchange, 500, Map.of("success", false, "error", e.getMessage()));
			}
		}
		
		private void handleLinkAccountToProfile(HttpExchange exchange) throws IOException {
			// Employee-only endpoint to link an account to a profile
			String sessionId = getSessionId(exchange);
			SessionData session = sessions.get(sessionId);
			if (session == null || !"employee".equals(session.role)) {
				sendJsonResponse(exchange, 403, Map.of("error", "Only employees can link accounts"));
				return;
			}
			
			JsonObject body = readJsonBody(exchange);
			String accountNum = body.get("accountNumber").getAsString();
			String username = body.get("username").getAsString();
			
			// Verify account exists
			String[] accountLines = parse(accountFile);
			boolean accountExists = false;
			for (String line : accountLines) {
				String[] data = line.split(",");
				if (data.length >= 1 && data[0].equals(accountNum)) {
					accountExists = true;
					break;
				}
			}
			
			if (!accountExists) {
				sendJsonResponse(exchange, 404, Map.of("success", false, "error", "Account not found"));
				return;
			}
			
			// Update profile to include this account
			String[] profileLines = parse(proFile);
			boolean profileFound = false;
			for (int i = 0; i < profileLines.length; i++) {
				String[] fields = profileLines[i].split(",");
				if (fields.length >= 2 && fields[0].equals(username)) {
					profileFound = true;
					// Parse existing accounts array
					String accountsField = fields.length > 7 ? fields[7] : "[]";
					if (accountsField.contains(accountNum)) {
						sendJsonResponse(exchange, 400, Map.of("success", false, "error", "Account already linked to this profile"));
						return;
					}
					
					// Add account to the list
					if (accountsField.equals("[]")) {
						accountsField = "[" + accountNum + "]";
					} else {
						accountsField = accountsField.substring(0, accountsField.length() - 1) + "," + accountNum + "]";
					}
					
					// Rebuild the profile line
					StringBuilder newLine = new StringBuilder();
					for (int j = 0; j < 7 && j < fields.length; j++) {
						if (j > 0) newLine.append(",");
						newLine.append(fields[j]);
					}
					// Ensure we have 7 fields
					for (int j = fields.length; j < 7; j++) {
						newLine.append(",");
						if (j == 6) newLine.append("0"); // creditScore
					}
					newLine.append(",").append(accountsField);
					profileLines[i] = newLine.toString();
					break;
				}
			}
			
			if (!profileFound) {
				sendJsonResponse(exchange, 404, Map.of("success", false, "error", "Profile not found"));
				return;
			}
			
			// Write updated profiles back
			try (PrintWriter writer = new PrintWriter(proFile)) {
				for (String line : profileLines) {
					writer.println(line);
				}
				LogEntry.appendToLog(logFile, new LogEntry(Integer.parseInt(accountNum), LogType.updateAccount,
						"REST API linked account to profile: " + username,
						java.time.LocalDateTime.now().toString()));
				sendJsonResponse(exchange, 200, Map.of("success", true, "message", "Account linked successfully"));
			} catch (Exception e) {
				sendJsonResponse(exchange, 500, Map.of("success", false, "error", e.getMessage()));
			}
		}
		
		// Helper methods
		private JsonObject readJsonBody(HttpExchange exchange) throws IOException {
			InputStream is = exchange.getRequestBody();
			Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name()).useDelimiter("\\A");
			String body = scanner.hasNext() ? scanner.next() : "";
			return JsonParser.parseString(body).getAsJsonObject();
		}
		
		private void sendJsonResponse(HttpExchange exchange, int statusCode, Object data) throws IOException {
			String response = gson.toJson(data);
			exchange.getResponseHeaders().set("Content-Type", "application/json");
			exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes(StandardCharsets.UTF_8));
			os.close();
		}
		
		private String getSessionId(HttpExchange exchange) {
			String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				return authHeader.substring(7);
			}
			return null;
		}
		
		private String generateSessionId() {
			return java.util.UUID.randomUUID().toString();
		}
		
		private String[] parse(File file) {
			try {
				Scanner scan = new Scanner(file);
				String data = "";
				while (scan.hasNextLine()) {
					data += scan.nextLine() + "\n";
				}
				scan.close();
				if (data.equals("")) {
					return new String[0];
				}
				return data.split("\n");
			} catch (Exception e) {
				return new String[0];
			}
		}
		
		private boolean fileDeposit(String accountNum, double amount) {
			String[] lines = parse(accountFile);
			for (int i = 0; i < lines.length; i++) {
				String[] info = lines[i].split(",");
				if (info.length >= 4 && info[0].equals(accountNum)) {
					double balance = Double.parseDouble(info[3]);
					balance += amount;
					info[3] = String.valueOf(balance);
					lines[i] = String.join(",", info);
					try (PrintWriter write = new PrintWriter(accountFile)) {
						for (String line : lines) {
							write.println(line);
						}
					} catch (Exception e) {
						return false;
					}
					return true;
				}
			}
			return false;
		}
		
		private boolean fileWithdrawal(String accountNum, double amount) {
			String[] lines = parse(accountFile);
			for (int i = 0; i < lines.length; i++) {
				String[] info = lines[i].split(",");
				if (info.length >= 4 && info[0].equals(accountNum)) {
					double balance = Double.parseDouble(info[3]);
					if (balance < amount) {
						return false;
					}
					balance -= amount;
					info[3] = String.valueOf(balance);
					lines[i] = String.join(",", info);
					try (PrintWriter write = new PrintWriter(accountFile)) {
						for (String line : lines) {
							write.println(line);
						}
					} catch (Exception e) {
						return false;
					}
					return true;
				}
			}
			return false;
		}
		
		private boolean writePin(String accountNum, String newPin) {
			String[] lines = parse(accountFile);
			for (int i = 0; i < lines.length; i++) {
				String[] info = lines[i].split(",");
				if (info.length >= 2 && info[0].equals(accountNum)) {
					info[1] = newPin;
					lines[i] = String.join(",", info);
					try (PrintWriter write = new PrintWriter(accountFile)) {
						for (String line : lines) {
							write.println(line);
						}
					} catch (Exception e) {
						return false;
					}
					return true;
				}
			}
			return false;
		}
		
		private int[] extractAccountNumbers(String profileLine) {
			// Parse accounts from profile line (format: username,password,name,...,accounts)
			// For now, return empty - accounts are loaded separately
			return new int[0];
		}
		
		private Map<String, Object> profileToMap(Profile p) {
			Map<String, Object> map = new HashMap<>();
			map.put("name", p.getName());
			map.put("username", p.getUsername());
			map.put("email", p.getEmail());
			map.put("phone", p.getPhone());
			map.put("address", p.getAddress());
			map.put("creditScore", p.getCreditScore());
			return map;
		}
		
		private Map<String, Object> accountToMap(Account a) {
			Map<String, Object> map = new HashMap<>();
			map.put("accountNumber", a.getNum());
			map.put("balance", a.getBalance());
			map.put("type", a.getType().toString());
			return map;
		}
	}
	
	static class SessionData {
		String role;
		String username;
		Profile profile;
		String atmAccountNumber; // For ATM sessions
		
		SessionData(String role, String username) {
			this.role = role;
			this.username = username;
		}
		
		SessionData(String role, String username, Profile profile) {
			this.role = role;
			this.username = username;
			this.profile = profile;
		}
	}
}
