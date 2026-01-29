# Multi-Client Banking System

A robust, multi-threaded banking application featuring both traditional Java socket-based clients and a modern web interface with REST API backend.

## 🏦 Overview

This banking system supports multiple access methods - ATM clients, Teller terminals, and a modern web interface. The application implements advanced concurrency control, ensuring thread-safe operations while handling multiple concurrent transactions across all platforms.

## ✨ Key Features

### Core Banking Features
- **Multi-Client Architecture**: Supports ATM, Teller, and Web interfaces
- **Concurrent Access**: Thread pooling for simultaneous connections
- **Thread-Safe Operations**: Synchronized file operations prevent data corruption
- **Data Persistence**: File-based storage for accounts, profiles, and transaction logs
- **Transaction Logging**: Comprehensive logging for all banking operations

### Web Interface Features
- **Modern React/Next.js Frontend**: Responsive, user-friendly web application
- **REST API Backend**: HTTP/JSON API for web and mobile clients
- **Three Login Types**:
  - **Employee Portal**: Full account management, profile editing, account creation
  - **Customer Portal**: View linked accounts, deposits, withdrawals, PIN changes
  - **ATM Interface**: Quick access with account number + PIN
- **Customer Self-Registration**: New customers can create their own profiles
- **Real-time Balance Updates**: Instant reflection of all transactions

### Desktop Client Features
- **Java Swing GUI**: Desktop interface for ATM and Teller clients
- **TCP/IP Socket Communication**: Real-time client-server communication

## 🛠️ Technologies Used

### Backend
- **Java** - Core programming language
- **REST API** - HTTP server using `com.sun.net.httpserver`
- **Gson** - JSON serialization/deserialization
- **TCP/IP Sockets** - Legacy client-server communication
- **Multithreading** - ExecutorService for concurrent connections

### Frontend (Web UI)
- **Next.js 14** - React framework
- **TypeScript** - Type-safe JavaScript
- **Tailwind CSS** - Utility-first styling
- **Axios** - HTTP client

### Desktop
- **Java Swing** - GUI framework

## 📋 Project Structure

Multi-Client-Banking-System/
├── project/
│   └── src/
│       └── group3/
│           ├── Server.java           # TCP socket server
│           ├── RestApiServer.java    # REST API server (Web)
│           ├── ATM.java              # ATM client
│           ├── Teller.java           # Teller client
│           ├── BankGUI.java          # Swing GUI
│           ├── Account.java          # Account management
│           ├── Profile.java          # User profiles
│           ├── Message.java          # Message protocol
│           ├── LogEntry.java         # Transaction logging
│           ├── accounts.txt          # Account data
│           ├── profiles.txt          # Customer profiles
│           ├── employees.txt         # Employee credentials
│           ├── log.txt               # Transaction logs
│           └── testing/              # Unit tests
├── web-ui/
│   ├── app/                          # Next.js app directory
│   ├── components/
│   │   ├── LoginPage.tsx             # Login with Employee/Customer/ATM tabs
│   │   ├── EmployeeDashboard.tsx     # Employee management interface
│   │   ├── CustomerDashboard.tsx     # Customer banking interface
│   │   ├── ATMDashboard.tsx          # ATM-style interface
│   │   └── ...                       # Other components
│   ├── lib/
│   │   └── api.ts                    # API client
│   └── package.json
├── REST_API_SETUP.md                 # Web setup instructions
└── Group3_SRS.docx                   # Software Requirements Specification

## 🚀 Getting Started

### Prerequisites

- Java Development Kit (JDK) 11 or higher
- Node.js 18+ and npm (for web interface)
- Gson library (`gson-2.10.1.jar` included)

### Option 1: Web Interface (Recommended)

#### 1. Start the REST API Server

bash
cd project
./COMPILE_AND_RUN_REST_API.sh


Or manually:
bash
cd project
javac --module-path . --add-modules com.google.gson -cp ".:gson-2.10.1.jar" -d bin src/group3/*.java src/module-info.java
java --module-path ".:gson-2.10.1.jar" --add-modules com.google.gson -cp "bin:gson-2.10.1.jar" group3.RestApiServer


The REST API server will start on `http://localhost:8080`

#### 2. Start the Web Frontend

```bash
cd web-ui
npm install
npm run dev
```

The web app will be available at `http://localhost:3000`

#### 3. Access the Application

| Login Type | Credentials | Description |
|------------|-------------|-------------|
| Employee | `employee1` / `employee1` | Full management access |
| Customer | `user1` / `pass1` | View linked accounts |
| ATM | Account: `1111`, PIN: `1111` | Direct account access |

### Option 2: Desktop Clients (Legacy)

#### 1. Start the Socket Server

```bash
cd project
javac -d bin src/group3/*.java
java -cp bin group3.Server
```

Server starts on port `7777`

#### 2. Run Desktop Clients

```bash
# ATM Client
java -cp bin group3.BankGUI

# Or run directly
java -cp bin group3.ATM
java -cp bin group3.Teller
```

## 🌐 Web Interface Guide

### Employee Dashboard
- **Search Accounts**: Enter account number to find and manage accounts
- **View/Edit Profiles**: Update customer information (name, email, phone, address, credit score, password)
- **Create Accounts**: Add new bank accounts with account number, PIN, type, and initial balance
- **Create Profiles**: Register new customer profiles
- **Link Accounts**: Associate bank accounts with customer profiles
- **Change PIN**: Update account PINs

### Customer Dashboard
- **View Accounts**: See all linked bank accounts and balances
- **Deposit/Withdraw**: Perform transactions on any linked account
- **Change PIN**: Update your account PIN
- **Profile Info**: View your profile information

### ATM Interface
- **Quick Access**: Login with just account number and PIN
- **Check Balance**: View current account balance
- **Deposit**: Add funds to account
- **Withdraw**: Take cash out
- **Change PIN**: Update your PIN
- **Quick Amount Buttons**: Fast entry for common amounts ($20, $50, $100, $200)

### Customer Registration
- New customers can create their own profiles from the login page
- Click "Customer" tab → "Create New Account"
- Fill in personal information
- After registration, an employee must link a bank account to the profile

## 🏗️ Architecture

### REST API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/auth/employee-login` | POST | Employee authentication |
| `/api/auth/customer-login` | POST | Customer authentication |
| `/api/auth/atm-login` | POST | ATM authentication (account + PIN) |
| `/api/auth/logout` | POST | End session |
| `/api/accounts` | GET | Get accounts for logged-in user |
| `/api/accounts/search` | GET | Search account by number (employee) |
| `/api/accounts/deposit` | POST | Deposit funds |
| `/api/accounts/withdraw` | POST | Withdraw funds |
| `/api/accounts/update-pin` | POST | Change account PIN |
| `/api/accounts/create` | POST | Create new account (employee) |
| `/api/accounts/link` | POST | Link account to profile (employee) |
| `/api/profiles/create` | POST | Create new profile |
| `/api/profiles/update` | POST | Update profile info (employee) |
| `/api/logs` | GET | Get transaction logs |

### Thread Safety Features

- Synchronized file access prevents race conditions
- Thread pool management for efficient resource utilization
- Atomic operations for critical banking transactions
- Session management with ConcurrentHashMap

## 📝 Data Files

| File | Description |
|------|-------------|
| `accounts.txt` | Account number, PIN, type, balance |
| `profiles.txt` | Username, password, name, phone, address, email, credit score, linked accounts |
| `employees.txt` | Employee credentials |
| `log.txt` | Transaction and operation logs |

## 🧪 Testing

The project includes unit tests in the `testing/` directory:

```bash
cd project
javac -d bin src/group3/*.java src/testing/*.java
java -cp bin testing.AccountTesting
java -cp bin testing.ATMTesting
# ... etc
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
- Web interface and REST API added as enhancement

---

**Note**: This is a forked repository from a group project. Significant enhancements include the REST API backend and modern React/Next.js web interface for cross-platform accessibility.
