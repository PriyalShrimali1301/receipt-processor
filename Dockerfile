# Use an official Maven image to run the build
FROM maven:3.8.6-openjdk-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the project and run tests
RUN mvn clean install

# Use an official OpenJDK image as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built artifacts from the build stage
COPY --from=build /app/target/receipt-processor-0.0.1-SNAPSHOT.jar ./receipt-processor.jar

# Command to run the application
CMD ["java", "-jar", "receipt-processor.jar"]