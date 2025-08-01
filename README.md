Markdown Live Preview
Reset
Copy

159160161162163164165166167175158168169170171172173174176177178179180181182183184185186187188189190
**Uswa Shaukat**  for Accounts Module <br>
**Mehak Mushtaq** for Payments Module
Backend Developer Interns – VaultsPay  
Project: **Adhapay App**  
Duration: **1st July 2025 – 31st July 2025**

---

##  License


Banking Service – VaultsPay Microservices Architecture
Overview
Account Module and Payment Module are part of Spring Boot microservice (Banking Service) responsible for managing user accounts and managing transactions within the AdhaPay fintech ecosystem. It handles account creation, retrieval, status updates, and enforces role-based access using JWT tokens, and Payment Module is responsible for handling core payment functionalities such as deposits, withdrawals, transfers, purchases, and transaction history retrieval based on role using JWT tokens.

This service is part of a larger distributed system which includes the AuthService, Banking Service, CardService.

Features
Automatic Account Creation on Login

Listens to AuthService for new user

Automatically generates account with:

accountId
userId
fullName as title
Unique accountNumber
Default balance = 0 and status = ACTIVE
Deposit money into account

Withdraw money from account

Transfer money between accounts

Purchase functionality (used when a user pays using card)

View transaction history (paginated and access-controlled)

Global ledger transaction logging for tracking

JWT-based token authentication and authorization

Role-based access (Admin vs User)

Validation and centralized exception handling

Role-Based Access Control

ADMIN can:

View all accounts
Update account status
View all users Transaction History including Global Account
View any specific user Transaction History including Global Account
View his own Transaction History including Global Account
USER can:

View their own accounts
Create single account
View his own Transaction History excluding Global Account
JWT Security

Verifies tokens from AuthService

Extracts and decodes the token using JwtUtil, and validates it using TokenUtil for secure access to protected endpoints.

AuthService token secret: a123FZxys0987LmnopQR456TuvWXz123

Inter-Service Communication

Uses RestTemplate to fetch user data from AuthService

Optionally issues internal JWTs for services like CardService

Project Structure
com.example.demo
├── config
├── constants
├── controller
├── dto
│── enums
├── exception
├── interceptor
├── mapper
├── model
├── repository
├── Service
    ├── handler
    ├── helper
├── util
Endpoints
Method	Endpoint	Role	Description
POST	/api/v1/accounts	USER	Create new account
GET	/api/v1/accounts	ADMIN	Get all accounts
GET	/api/v1/accounts	USER	Get logged-in user's accounts
GET	/api/v1/accounts/{accountId}	ADMIN	Get account by ID
PUT	/api/v1/accounts/{accountId}	ADMIN	Update account status
GET	/api/v1/accounts/by-token	USER, ADMIN	Issue account token for services
POST	/api/v1/accounts/deposit	USER, ADMIN	Deposit money
POST	/api/v1/accounts/withdraw	USER, ADMIN	Withdraw money using card
POST	/api/v1/accounts/purchase	USER, ADMIN	Make a purchase using card
POST	/api/v1/accounts/transfer	USER, ADMIN	Transfer between accounts
GET	/api/v1/accounts/history	ADMIN	Get transaction history of all users including Global Account(paginated)
GET	/api/v1/accounts/history?accounId	ADMIN	Get transaction history of specific user including Global Account (paginated)
GET	/api/v1/accounts/history?accounId	USER	Get his own transaction history excluding Global Account (paginated)
JWT Token Claims
Claim	Type	Required
id	Long	Yes
email	String	Yes
role	USER / ADMIN	Yes
type	ACCESS / REFRESH	Yes
Technologies Used
Java 17+
Spring Boot 3.x
Spring Security
Spring Web
Lombok
PostgreSQL
JWT
RestTemplate
Maven
Setup Instructions
Clone the repository:

git clone https://github.com/RubabChaudhry2022/adapay.git
cd adapay
Configure your application.properties:

jwt:
  secret: a123FZxys0987LmnopQR456TuvWXz123
auth-service:
  url: http://localhost:8080/api/v1/auth
Configure Database

spring.datasource.url=jdbc:postgresql://localhost:5432/yourdb
spring.datasource.username=yourusername
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
Build and run the app:

mvn clean install
mvn spring-boot:run
Access API Base path: http://localhost:8080/api/v1/payment for Payments Base path: http://localhost:8080/api/v1/accounts for Accounts

Test endpoints using Postman or Swagger.

Sample Request
Create Account
POST /api/v1/accounts
Authorization: Bearer <JWT>
Deposit
POST /api/v1/payment/deposit
Authorization: Bearer <JWT>
Author
Uswa Shaukat for Accounts Module
Mehak Mushtaq for Payments Module Backend Developer Interns – VaultsPay
Project: Adhapay App
Duration: 1st July 2025 – 31st July 2025

License
This project is for educational and demonstration purposes under VaultsPay. No commercial license is granted.

