FROM maven:3.9.5-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:21-slim

ARG JAR_FILE=/app/target/notification-service-0.0.1-SNAPSHOT.jar

COPY --from=builder ${JAR_FILE} app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "/app.jar"]