
# Authorization Service

A secure authentication and authorization service built using Spring Boot. It handles user login, registration, logout, token generation, and permission-based access for internal and external services using JWT tokens.

---

## Key Features

- User registration and login with JWT tokens
- Access and refresh tokens
- Role-based access (`USER`, `ADMIN`)
- Permission-based authorization using method + path + role
- Internal service communication via JWT
- Logout with token blacklist

---

## Technologies Used

- Java 17
- Spring Boot
- Spring Security
- JWT (jjwt)
- PostgreSQL
- Maven (using `./mvnw`)
- JPA / Hibernate
- Lombok

---

## Project Structure

```
/src
  /main
    /java
      /com/example/auth
        /controller      → API endpoints
        /dto             → Request/response classes
        /filter          → JWT filter
        /model           → Entity classes
        /repository      → JPA repositories
        /security        → JWT and authentication helpers
        /service         → Business logic
    /resources
      application.properties
pom.xml
README.md
```

---

## Local Setup

### Prerequisites

- Java 17+
- PostgreSQL installed and running
- Git installed

### Environment Configuration

Update your `application.properties` file or set the following environment variables:

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/auth_db
spring.datasource.username=your-username
spring.datasource.password=your-password

# JWT configuration
jwt.secret=your-secret-key
jwt.access-token.expiration=900000        # 15 minutes
jwt.refresh-token.expiration=2592000000   # 30 days
```

---

### Run the Project

```bash
# Clone the project
git clone https://github.com/RubabChaudhry2022/adapay.git
cd adapay

# Checkout the authorization branch
git checkout authorization

# Create PostgreSQL database
createdb auth_db

# Build the project using Maven Wrapper
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

---

## API Endpoints

| Method | Endpoint                  | Description                                    | Access Roles           |
|--------|---------------------------|------------------------------------------------|------------------------|
| POST   | /v1/auth/register         | Register a new user                            | Public                 |
| POST   | /v1/auth/login            | Authenticate and receive tokens                | Public                 |
| POST   | /v1/auth/logout           | Invalidate token (logout)                      | Authenticated user     |
| GET    | /v1/auth/users            | Get all users                                  | ADMIN or Self          |
| GET    | /v1/auth/users/{id}       | Get user by ID                                 | ADMIN or Self          |
| POST   | /v1/auth/validate-access  | Validate access based on method, path, role    | Internal microservices |
| POST   | /v1/auth/validate-token   | Extract and return user info from JWT token    | Internal microservices |
| POST   | /v1/auth/users            | Add new user and admin                         | ADMIN                  |


---

## Access Control Design

Authorization is enforced through:
- JWT token in the `Authorization: Bearer <token>` header
- Role extracted from the token (`USER`, `ADMIN`)
- Permission table that maps:  
  `HTTP Method + URL Path + Allowed Roles`

Example:

| Method | Path                     | Allowed Roles       |
|--------|--------------------------|---------------------|
| POST   | /v1/auth/users           | ADMIN               |
| GET    | /v1/auth/users/{id}      | ADMIN, USER (self)  |
| POST   | /v1/auth/users           | ADMIN, INTERNAL     |

A custom filter checks permissions before allowing access to endpoints.

---

## Running Tests

Tests are **not implemented yet**. This section will be updated once test cases are added using JUnit and Spring Boot Test framework.

---

## Author

[**Zainab-Rafiq**](https://github.com/Zainab-Rafiq)

Project: AdhaPay

Backend Developer Intern-VaultsPay


---

## License

This project is licensed under the MIT License.
