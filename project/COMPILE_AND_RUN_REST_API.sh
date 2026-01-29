#!/bin/bash

# Navigate to project directory
cd "$(dirname "$0")"

echo "=== Setting up REST API Server ==="

# Download Gson if not present
if [ ! -f "gson-2.10.1.jar" ]; then
    echo "Downloading Gson library..."
    curl -O https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar
fi

# Create bin directory if it doesn't exist
mkdir -p bin

# Compile all Java files
echo "Compiling Java files..."
javac --module-path .:gson-2.10.1.jar --add-modules jdk.httpserver,com.google.gson -d bin src/module-info.java src/group3/*.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo ""
    echo "Starting REST API Server..."
    echo "Server will run on http://localhost:8080"
    echo ""
    java --module-path bin:gson-2.10.1.jar --add-modules jdk.httpserver,com.google.gson -m project/group3.RestApiServer
else
    echo "Compilation failed. Please check the errors above."
    exit 1
fi
