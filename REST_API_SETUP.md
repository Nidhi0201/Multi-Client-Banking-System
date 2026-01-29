# Complete Setup Instructions for Web UI

## Architecture

- **Backend**: Java REST API Server (`RestApiServer.java`) - runs on port 8080
- **Frontend**: Next.js React app (`web-ui/`) - runs on port 3000

---

## Step 1: Setup Java REST API Server

### Option A: Using Gson (Recommended)

1. Download Gson JAR:
```bash
cd project
curl -O https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar
```

2. Compile:
```bash
javac -cp ".:gson-2.10.1.jar" -d bin src/group3/RestApiServer.java
```

3. Run:
```bash
java -cp "bin:gson-2.10.1.jar" group3.RestApiServer
```

### Option B: Manual JSON (No External Dependencies)

If you prefer not to use Gson, I can modify `RestApiServer.java` to use manual JSON parsing. Let me know!

---

## Step 2: Setup Next.js Frontend

1. Install Node.js dependencies:
```bash
cd web-ui
npm install
```

2. Start development server:
```bash
npm run dev
```

3. Open browser: [http://localhost:3000](http://localhost:3000)

---

## Step 3: Test the System

1. **Start REST API Server** (Terminal 1):
   - Should see: `REST API Server running on http://localhost:8080`

2. **Start Next.js Frontend** (Terminal 2):
   - Should see: `Ready on http://localhost:3000`

3. **Login**:
   - Employee: `employee1` / `employee1`
   - Customer: `user1` / `pass1`

4. **Use the Dashboard**:
   - View accounts in card layout
   - Make deposits/withdrawals
   - See activity logs in real-time

---

## Troubleshooting

- **CORS errors**: Make sure REST API server is running and CORS headers are set
- **Connection refused**: Check that API server is on port 8080
- **No accounts shown**: Make sure you're logged in as a customer with accounts
- **Build errors**: Run `npm install` again in `web-ui/` directory

---

## Production Deployment

For production:
1. Build Next.js: `cd web-ui && npm run build && npm start`
2. Use proper session management (JWT tokens) instead of in-memory sessions
3. Add authentication middleware to protect API endpoints
4. Use environment variables for API URLs
