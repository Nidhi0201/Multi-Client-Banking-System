# Multi-Client Banking System

A robust, multi-threaded client-server banking application built with Java, featuring TCP/IP socket communication, concurrent user access, and thread-safe data persistence.

## 🏦 Overview

This banking system supports multiple client types (ATM and Teller) connecting simultaneously to a centralized server. The application implements advanced concurrency control, ensuring thread-safe operations while handling multiple concurrent transactions.

## ✨ Key Features

- **Multi-Client Architecture**: Supports both ATM and Teller client interfaces
- **Concurrent Access**: Handles multiple simultaneous connections using thread pooling
- **Thread-Safe Operations**: Synchronized file operations prevent data corruption
- **TCP/IP Socket Communication**: Real-time client-server communication
- **Data Persistence**: File-based storage for accounts, profiles, employees, and transaction logs
- **Transaction Logging**: Comprehensive logging system for all banking operations
- **GUI Interface**: Java Swing-based user interface for both client types

## 🛠️ Technologies Used

- **Java** - Core programming language
- **Java Swing** - GUI framework
- **TCP/IP** - Network communication protocol
- **Socket Programming** - Client-server communication
- **Multithreading** - ExecutorService thread pool for concurrent connections
- **Concurrency** - Synchronized operations for thread safety
- **File I/O** - Data persistence and logging

## 📋 Project Structure

```
Multi-Client-Banking-System/
├── project/
│   └── src/
│       └── group3/
│           ├── Server.java          # Main server implementation
│           ├── ATM.java             # ATM client interface
│           ├── Teller.java           # Teller client interface
│           ├── BankGUI.java          # GUI components
│           ├── Account.java          # Account management
│           ├── Profile.java          # User profile handling
│           ├── Message.java         # Message protocol
│           ├── LogEntry.java        # Transaction logging
│           └── testing/             # Unit tests
├── bin/                              # Compiled classes
└── Group3_SRS.docx                   # Software Requirements Specification
```

## 🚀 Getting Started

### Prerequisites

- Java Development Kit (JDK) 11 or higher
- Java Runtime Environment (JRE)

### Installation

1. Clone the repository:
```bash
git clone https://github.com/Nidhi0201/Multi-Client-Banking-System.git
cd Multi-Client-Banking-System
```

2. Navigate to the project directory:
```bash
cd project
```

3. Compile the Java files:
```bash
javac -d bin src/group3/*.java
```

### Running the Application

1. **Start the Server**:
```bash
java -cp bin group3.Server
```
The server will start on port `7777` and display: "Server is running"

2. **Start Client Applications**:
   - **ATM Client**: Run `ATM.java`
   - **Teller Client**: Run `Teller.java`

## 🏗️ Architecture

### Server-Side
- **Thread Pool**: Uses `Executors.newFixedThreadPool(20)` to handle up to 20 concurrent connections
- **Client Handler**: Each client connection is handled by a separate thread
- **File Synchronization**: Synchronized file operations ensure data integrity
- **Transaction Logging**: All operations are logged to `log.txt`

### Client-Side
- **ATM Interface**: Simplified interface for basic banking operations
- **Teller Interface**: Full-featured interface for bank employees
- **Real-time Communication**: Socket-based communication with the server

## 🔒 Thread Safety Features

- Synchronized file access prevents race conditions
- Thread pool management for efficient resource utilization
- Atomic operations for critical banking transactions
- File locking mechanisms for account access

## 📝 Data Files

- `accounts.txt` - Account information and balances
- `profiles.txt` - Customer profile data
- `employees.txt` - Employee credentials and access
- `log.txt` - Transaction and operation logs

## 🧪 Testing

The project includes comprehensive unit tests in the `testing/` directory:
- AccountTesting.java
- ATMTesting.java
- ServerTesting.java
- TellerTesting.java
- ProfileTesting.java
- MessageTesting.java
- LogEntryTesting.java

Run tests using:
```bash
java -cp bin group3.testing.*
```

## 👤 Author

**Nidhi Prajapati**
- GitHub: [@Nidhi0201](https://github.com/Nidhi0201)
- Portfolio: [Nidhi Prajapati Portfolio](https://github.com/Nidhi0201/Nidhi-Prajapati-Portfolio)

## 📄 License

This project was developed as part of CS-401 coursework. All rights reserved.

## 🙏 Acknowledgments

- Developed as part of CS-401 Software Engineering course
- Original repository: [syfabo/CS-401-Project](https://github.com/syfabo/CS-401-Project)

---

**Note**: This is a forked repository from a group project. Contributions and improvements have been made to enhance functionality and code quality.

