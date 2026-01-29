package group3;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LogEntry {

	private final int accountNum;
	private final LogType type;
	private final String message;
	private final String dateTime;

	public LogEntry(int accountNum, LogType type, String message, String dateTime) {
		this.accountNum = accountNum;
		this.type = type;
		this.message = message;
		this.dateTime = dateTime;
	}

	// format used by Server: accountNum,type,message,date
	@Override
	public String toString() {
		return accountNum + "," + type + "," + message + "," + dateTime;
	}

	// append a single log entry to the given log file
	public static void appendToLog(File logFile, LogEntry entry) {
		try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
			writer.println(entry.toString());
			// also echo logs to the server console for easy monitoring
			System.out.println("[LOG] " + entry.toString());
		} catch (IOException e) {
			System.out.println("Error writing log entry: " + e.getMessage());
		}
	}
}
