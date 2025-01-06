# Adoptify API

This repository contains a multi-service microservices application built with Spring Boot. The application comprises four main services:

1. **user-service**  
2. **pet-service**  
3. **adoption-service**  
4. **notification-service**  

Additionally, it includes:
- A **service-registry** (Eureka) for service discovery.  
- A **gateway** (Spring Cloud Gateway) to handle routing and entry points into the system.

## Architecture Diagram
![Architecture Diagram](https://github.com/user-attachments/assets/dbe6708e-99ba-480b-86a2-02b5c4abf002)


The application demonstrates a range of microservice communication patterns, security, observability, fault tolerance, and deployment using Docker.

---

## Table of Contents
1. [Architecture Overview](#architecture-overview)  
2. [Features and Technologies](#features-and-technologies)  
3. [Microservices Description](#microservices-description)  
    - [User Service](#1-user-service)  
    - [Pet Service](#2-pet-service)  
    - [Adoption Service](#3-adoption-service)  
    - [Notification Service](#4-notification-service)  
    - [Service Registry (Eureka)](#5-service-registry-eureka)  
    - [API Gateway (Spring Cloud Gateway)](#6-api-gateway-spring-cloud-gateway)  
4. [Getting Started](#getting-started)  
    - [Prerequisites](#prerequisites)  
    - [Installation](#installation)  
    - [Running the Application](#running-the-application)  
5. [Observability and Monitoring](#observability-and-monitoring)  
6. [Testing](#testing)  
7. [Continuous Integration (CI)](#continuous-integration-ci)  
8. [Project Structure](#project-structure)  
9. [License](#license)  

---

## Architecture Overview

The system follows a microservices architecture where each service handles a specific domain:
- **user-service** manages user information and authentication.
- **pet-service** manages pet information.
- **adoption-service** orchestrates the adoption process between users and pets.
- **notification-service** sends notifications (via RabbitMQ) to users.
- **service-registry** (Eureka) for service discovery.  
- **gateway** (Spring Cloud Gateway) to handle routing and entry points into the system.

Communication flow:
- **Synchronous** calls between services using [OpenFeign](https://spring.io/projects/spring-cloud-openfeign).  
- **Asynchronous** events via [RabbitMQ](https://www.rabbitmq.com/) for notification handling.  

All microservices are registered with the **Eureka Server**, and requests are routed through the **Spring Cloud Gateway**. The application uses **Spring Security** with JWT authentication to secure endpoints. **Zipkin** is used for distributed tracing, and **Resilience4j** provides fault tolerance in case of network or service failures.

A high-level diagram could look like this:

---

## Features and Technologies

1. **Spring Boot & Spring Cloud**  
   - Service Discovery: **Eureka**  
   - API Gateway: **Spring Cloud Gateway**  
   - **OpenFeign** for REST clients and synchronous communication  
   - **RabbitMQ** for asynchronous messaging  
   - **Resilience4j** for circuit breaking and retry mechanisms  

2. **Security**  
   - **Spring Security** and **JWT** for authentication and authorization  

3. **Observability**  
   - **Zipkin** for distributed tracing  

4. **Testing**  
   - **JUnit** and **Mockito** for unit and integration tests  

5. **CI/CD**  
   - **GitHub Actions** for continuous integration and automated testing  

6. **Containerization**  
   - **Docker** used for packaging microservices into containers  

---

## Microservices Description

### 1. user-service
- **Purpose**: Manages user data (registration, authentication, profile).  
- **Key Endpoints**:  
  - `POST /api/users/register`  
  - `POST /api/users/login`  
  - `REST /api/users/{id}` (secured, requires JWT)  
- **Technology Highlights**:  
  - Uses **Spring Data JPA** for persistence.  
  - Exposes REST endpoints secured with **Spring Security** (JWT).  
  - Publishes/consumes messages via RabbitMQ (for example, sending user status updates to **notification-service**).

| **Column**     | **Type**     | **Description**                                                             |
|----------------|--------------|-----------------------------------------------------------------------------|
| `user_id`      | BIGINT       | Unique identifier for the user record.                                      |
| `username`     | VARCHAR(255) | Display name or handle for the user.                                        |
| `email`        | VARCHAR(255) | Email address of the user.                                                  |
| `phone_number` | VARCHAR(20)  | Contact phone number of the user (optional).                                |
| `password`     | VARCHAR(255) | Hashed password used for user authentication.                               |
| `roles`        | VARCHAR(255) | Roles or permissions assigned to the user (e.g., `ROLE_ADMIN, ROLE_USER`).  |
| `created_at`   | DATETIME     | Timestamp indicating when the record was first created (automatically set). |
| `updated_at`   | DATETIME     | Timestamp indicating the latest update to the record (automatically set).   |


### 2. pet-service
- **Purpose**: Manages pet data (creation, updates, availability for adoption).  
- **Key Endpoints**:  
  - `POST /api/pets`
  - `GET /api/pets`
  - `GET /api/pets/{id}`
  - `PATCH /api/pets/{id}`
  - `DELETE /api/pets/{id}`
- **Technology Highlights**:  
  - Stores pet information in a relational database.  
  - Integrates with **adoption-service** via **OpenFeign** for synchronous updates.

| **Column**      | **Type**                   | **Description**                                                    |
|-----------------|----------------------------|--------------------------------------------------------------------|
| `pet_id`        | BIGINT                     | Unique identifier for the pet record.                              |
| `name`          | VARCHAR(255)               | Pet’s name.                                                        |
| `type`          | VARCHAR(255)               | Type of animal (e.g., dog, cat, bird, etc.).                       |
| `breed`         | VARCHAR(255)               | Breed of the pet (e.g., Golden Retriever, Siamese).                |
| `age`           | INT                        | Age of the pet in years (or approximate).                          |
| `description`   | TEXT                       | General description or notes about the pet.                        |
| `location`      | VARCHAR(255)               | Geographic location of the pet (city, state, etc.).                |
| `status`        | ENUM('AVAILABLE','ADOPTED')| Current adoption status of the pet (`AVAILABLE` or `ADOPTED`).      |
| `user_id`       | BIGINT                     | ID referencing the user who listed the pet.                        |
| `primary_image` | VARCHAR(255)               | URL or path to the pet’s primary image.                            |
| `created_at`    | DATETIME                   | Timestamp indicating when the record was first created (automatically set). |
| `updated_at`    | DATETIME                   | Timestamp indicating the latest update to the record (automatically set).   |


### 3. adoption-service
- **Purpose**: Coordinates the adoption process between **user-service** and **pet-service**.  
- **Key Endpoints**:  
  - `POST /api/adoptions` (initiate adoption request)  
  - `GET /api/adoptions`
  - `GET /api/adoptions/{id}`
  - `PATCH /api/adoptions/{id}`
- **Technology Highlights**:  
  - Calls **user-service** and **pet-service** via **OpenFeign** to validate user/pet data.  
  - Sends notifications to **notification-service** through RabbitMQ upon successful adoptions.

| **Column**           | **Type**                                    | **Description**                                                        |
|----------------------|---------------------------------------------|------------------------------------------------------------------------|
| `id`                 | BIGINT                                      | Unique identifier for the adoption record.                            |
| `pet_id`             | BIGINT                                      | ID referencing the pet to be adopted.                                  |
| `user_id`            | BIGINT                                      | ID referencing the user who is adopting the pet.                       |
| `status`             | ENUM('PENDING','APPROVED','REJECTED','COMPLETED') | Current status of the adoption process.                              |
| `adoption_date`      | DATETIME                                    | Timestamp for when the adoption was finalized or updated.              |
| `reason_of_adoption` | VARCHAR(255)                                | Brief explanation or notes on why the user wants to adopt the pet.      |

### 4. notification-service
- **Purpose**: Receives and processes notification events from other services.  
- **Messaging**:  
  - Listens to RabbitMQ queues for user notifications (from **user-service**) and adoption notifications (from **adoption-service**).  
- **Actions**:  
  - Sends emails or push notifications to users.

### 5. Service Registry (Eureka)
- **Purpose**: Maintains a registry of all running microservices (user, pet, adoption, and notification) along with Gateway.  
- **Usage**:  
  - Each microservice registers itself at startup.  
  - Clients discover other services through Eureka rather than using direct host:port configurations.  

### 6. API Gateway (Spring Cloud Gateway)
- **Purpose**: Central entry point to the microservices; routes external requests to the appropriate service.  
- **Key Features**:  
  - Request routing  
  - Load balancing (integrated with Eureka)  
  - JWT token forwarding  

---

## Getting Started

### Prerequisites
- Java 21
- Maven 3.4.1
- Docker installed (for containerization)  
- RabbitMQ instance (Docker container)  
- Zipkin instance (Docker container)  

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/mdsaad2305/Adoptify-Microservices.git
   cd Adoptify-Microservices
   ```
   
2. **Setup .env file in the root directory for environment variables**:
   ```
   SMTP_EMAIL=smtp_email
   SMTP_PASSWORD=smtp_password
   ```

3. **Build the project for all services or directories**:
   ```bash
   mvn clean install
   ```

### Running the Application

1. **Run via Docker Compose**:
   ```bash
   docker-compose up --build
   ```
   This command will spin up all microservices, RabbitMQ, Zipkin, etc.

2. **Access the application**:
   - Gateway routes will typically be exposed on `http://localhost:8084/`.
   - Eureka dashboard: `http://localhost:8761`.

---

## Observability and Monitoring

- **Distributed Tracing**: Zipkin is integrated into each microservice. By sending trace data, you can monitor request flow across the microservices in the Zipkin UI (`http://localhost:9411`).  
- **Logs**: Each service logs to the console.

---

## Testing

- **JUnit & Mockito**:  
  - Each microservice contains unit tests.  
  - Run tests via:
    ```bash
    mvn test
    ```
  - For more complex scenarios, consider setting up testcontainers for RabbitMQ and databases.

---

## Continuous Integration (CI)

- **GitHub Actions**:
  - A CI pipeline is configured in `.github/workflows/ci.yml` (or similar).  
  - On each pull request, the pipeline will:
    - Build the project  
    - Run tests  
    - Check code quality using spotless and checkstyle

---

## Project Structure

Our project directory structure look like this.

```
.
├── user-service
│   ├── src
│   └── pom.xml
├── pet-service
│   ├── src
│   └── pom.xml
├── adoption-service
│   ├── src
│   └── pom.xml
├── notification-service
│   ├── src
│   └── pom.xml
├── service-registry
│   ├── src
│   └── pom.xml
├── gateway
│   ├── src
│   └── pom.xml
├── docker-compose.yml
├── README.md
└── .github
    └── workflows
        └── ci.yml
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

