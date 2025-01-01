# First stage: Build the application
FROM maven:3.9.5-eclipse-temurin-21 AS builder

# Create and set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the entire source code
COPY src ./src

# Build the application (skip tests if desired)
RUN mvn clean package -DskipTests

# Use the official OpenJDK 21 runtime as a parent image
FROM openjdk:21-slim

# Specify the name of the JAR file we want to copy
ARG JAR_FILE=target/user-service-0.0.1-SNAPSHOT.jar

# Copy the JAR file into the container
COPY ${JAR_FILE} app.jar

# Expose the port your Spring Boot app listens on (default is 8080)
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app.jar"]
