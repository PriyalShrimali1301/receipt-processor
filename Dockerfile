# Use the official OpenJDK base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file from the target directory to the container
COPY target/receipt-processor-0.0.1-SNAPSHOT.jar /app/receipt-processor-0.0.1-SNAPSHOT.jar

# Expose the port the application will run on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/receipt-processor-0.0.1-SNAPSHOT.jar"]
