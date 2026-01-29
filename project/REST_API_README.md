# REST API Server

HTTP/JSON REST API server that wraps the existing banking system logic for web frontend access.

## Prerequisites

- Java 11+
- Gson library (for JSON parsing)

## Setup

1. Download Gson JAR:
```bash
cd project
wget https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar
```

Or add to your build system (Maven/Gradle).

2. Compile with Gson:
```bash
javac -cp ".:gson-2.10.1.jar" -d bin src/module-info.java src/group3/*.java
```

3. Run the REST API server:
```bash
java -cp "bin:gson-2.10.1.jar" group3.RestApiServer
```

The server will start on `http://localhost:8080`

## API Endpoints

All endpoints are prefixed with `/api`:

### Authentication
- `POST /api/auth/employee-login` - Employee login
- `POST /api/auth/customer-login` - Customer login  
- `POST /api/auth/logout` - Logout

### Accounts
- `GET /api/accounts` - Get user's accounts (requires auth)
- `GET /api/accounts/balance?accountNumber=XXX` - Get account balance
- `POST /api/accounts/deposit` - Deposit funds
- `POST /api/accounts/withdraw` - Withdraw funds
- `POST /api/accounts/update-pin` - Update account PIN

### Profiles
- `POST /api/profiles/create` - Create new customer profile

### Logs
- `GET /api/logs` - Get activity logs

## CORS

CORS is enabled for all origins to allow the web frontend to access the API.

## Session Management

Sessions are stored in memory (simple Map). In production, use proper session management (JWT tokens, Redis, etc.).
