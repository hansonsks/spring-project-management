# Spring Project Management

![Build Status](https://github.com/hansonsks/spring-project-management/actions/workflows/maven.yml/badge.svg)

## Overview
This project is a web application built using Java, the Spring framework, and 
Maven. It includes functionalities for local and OAuth2 user authentication, 
project management, and collaboration. The application uses MySQL or 
PostgreSQL as the primary database and integrates with GitHub and Google APIs 
for additional features.

## Features
- User Authentication (Password-based, OAuth2)
- Role-based Access Control (Admin, User, Guest)
- Project/Task Management (CRUD operations for projects and tasks)
- Collaboration (Project collaboration, task assignment, deadline tracking)
- Notifications and Comments
- Integration with GitHub/Google/Facebook APIs (OAuth2, Access user-specific 
  data)

## Technologies Used
- **Java 17**
- **Spring Boot 3**
- **Spring Security 6**
- **Spring Data JPA**
- **Spring MVC**
- **Thymeleaf**
- **Bootstrap 5** (HTML / CSS / JavaScript)
- **MySQL / PostgreSQL**
- **H2 Database** (for testing)
- **GitHub / Google / Facebook APIs** (OAuth2, additional features)
- **JUnit + Mockito** (for testing)

## Deployment Platforms
- **AWS Elastic Beanstalk + RDS**
- **Azure App Services + Azure Database for MySQL**
- **Heroku + Heroku Postgres**

## Prerequisites
- Java 17 or higher
- Maven 4.0 or higher
- MySQL 8 / PostgreSQL 16 or higher

## Setup and Installation
1. **Clone the repository:**
   ```sh
   git clone https://github.com/hansonsks/spring-project-management.git
   cd spring-project-management
   ```
2. **Add database/OAuth2 credentials:**  
   - Create a new database in MySQL/PostgreSQL and add the credentials to 
     `src/main/resources/application.properties`.
   - Create a new OAuth2 application in Google, GitHub, and Facebook and add 
     the credentials to `src/main/resources/application.properties`.
   - Alternatively, add placeholders for the credentials so that the 
     application builds.
   - **Note:** The application uses the `application.properties` file for 
     storing sensitive information. Make sure to keep the file secure and 
     private.
3. **Build the project:**
   ```sh
    mvn clean install
    ```
4. **Run the application:**
    ```sh
    mvn spring-boot:run
    ```
5. **Access the application:**
    - Open a web browser and go to `http://localhost:8080/`.
    - The port number is 8080 by default, you may change the port number by 
      adding the `server.port` property in `application.properties`.

## Maven Dependencies

This project uses the following Maven dependencies:

- **Spring Boot Starter Data JPA**
    - `org.springframework.boot:spring-boot-starter-data-jpa`

- **Spring Boot Starter Security**
    - `org.springframework.boot:spring-boot-starter-security`

- **Spring Boot Starter Thymeleaf**
    - `org.springframework.boot:spring-boot-starter-thymeleaf`

- **Spring Boot Starter Validation**
    - `org.springframework.boot:spring-boot-starter-validation`

- **Spring Boot Starter Web**
    - `org.springframework.boot:spring-boot-starter-web`

- **Thymeleaf Extras for Spring Security 6**
    - `org.thymeleaf.extras:thymeleaf-extras-springsecurity6`

- **Spring Security OAuth2 Client**
    - `org.springframework.security:spring-security-oauth2-client`

- **Spring Boot DevTools** (optional for development)
    - `org.springframework.boot:spring-boot-devtools`

- **H2 Database** (for testing)
    - `com.h2database:h2`

- **MySQL Connector**    (Either MySQL or PostgreSQL driver is required)
    - `com.mysql:mysql-connector-j`

- **PostgreSQL Driver**  (Either MySQL or PostgreSQL driver is required)
    - `org.postgresql:postgresql`

- **Project Lombok**
    - `org.projectlombok:lombok` (optional)

- **Spring Boot Starter Test** (for testing)
    - `org.springframework.boot:spring-boot-starter-test`

- **Spring Security Test** (for testing)
    - `org.springframework.security:spring-security-test`

- **JUnit Jupiter API** (for testing)
    - `org.junit.jupiter:junit-jupiter-api`

- **Mockito Core** (for testing)
    - `org.mockito:mockito-core`

**Note:** This project originally started as a simple to-do list application. 
Over time, it evolved into a full project management app. As a result, some 
elements, such as "ToDo XYZ," may still be referred to as "Projects" within the 
application.
