# Banking System Web Frontend

Modern React/Next.js web application for the Multi-Client Banking System.

## Prerequisites

- Node.js 18+ and npm/yarn
- Java REST API server running on `http://localhost:8080`

## Setup

1. Install dependencies:
```bash
cd web-ui
npm install
```

2. Start the development server:
```bash
npm run dev
```

3. Open [http://localhost:3000](http://localhost:3000) in your browser

## Features

- Modern, responsive dashboard UI
- Employee and Customer login
- Account management
- Real-time transaction processing
- Activity logs panel
- Clean card-based layout

## API Endpoints

The frontend communicates with the Java REST API server at `http://localhost:8080/api`:

- `POST /api/auth/employee-login` - Employee authentication
- `POST /api/auth/customer-login` - Customer authentication
- `GET /api/accounts` - Get user accounts
- `POST /api/accounts/deposit` - Deposit funds
- `POST /api/accounts/withdraw` - Withdraw funds
- `GET /api/logs` - Get activity logs
