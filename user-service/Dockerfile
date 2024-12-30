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
