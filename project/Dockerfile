# Use official OpenJDK image
FROM openjdk:17-slim

# Set working directory
WORKDIR /app

# Copy source files and dependencies
COPY src/ ./src/
COPY gson-2.10.1.jar ./

# Create directories
RUN mkdir -p bin data

# Copy data files to data directory
COPY src/group3/accounts.txt ./data/
COPY src/group3/profiles.txt ./data/
COPY src/group3/employees.txt ./data/
RUN touch ./data/log.txt

# Set environment variable for data directory
ENV DATA_DIR=/app/data

# Compile Java files
RUN javac -cp ".:gson-2.10.1.jar" -d bin src/group3/*.java

# Expose port
EXPOSE 8080

# Run the REST API server
CMD ["java", "-cp", "bin:gson-2.10.1.jar", "group3.RestApiServer"]
