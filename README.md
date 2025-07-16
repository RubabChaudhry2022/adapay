# Card Service API

A Spring Boot REST API service for managing credit/debit cards with JWT-based authentication and role-based authorization.

## Features

- **Card Management**: Create, view, update, block, and freeze cards
- **Authentication**: JWT-based token authentication with Authorization header
- **Authorization**: Role-based access control (ADMIN and USER roles)
- **Account Association**: Cards are linked to specific user accounts
- **Security**: PIN management with 3-digit validation
- **User-specific Access**: Users can only access their own resources
- **Admin Privileges**: Full system access for administrators
- **Card Search**: Search functionality for finding specific cards
- **Reporting**: Admin-only reporting functionality

## API Endpoints

### Authentication
All endpoints require a valid JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

### Card Endpoints

#### 1. Get All Cards (ADMIN only)
```
GET /api/v1/cards
```
Returns a list of all cards in the system using `CardListDTO`.
- **Access**: ADMIN role only
- **Response**: List of CardListDTO objects

#### 2. Get Cards by Account ID
```
GET /api/v1/cards/{accountId}
```
- **ADMIN**: Can view any account's cards
- **USER**: Can only view their own account's cards (validated via `isAccountOwnedByUser`)
- **Response**: List of CardModel objects

#### 3. Create New Card
```
POST /api/v1/cards/{userId}/account/{accountId}
```
- **ADMIN**: Can create cards for any user
- **USER**: Can only create cards for themselves (userId must match token userId)
- **Request Body**: CardModel object
- **Response**: CreateCardDTO object

#### 4. Search Cards
```
GET /api/v1/cards/search
```
Search for cards based on CardModel criteria.
- **Access**: Both ADMIN and USER (results may be filtered by service layer)
- **Request**: CardModel as query parameters
- **Response**: List of CardModel objects

#### 5. Block Card (ADMIN only)
```
PUT /api/v1/cards/{cardId}/block
```
Permanently blocks a card.
- **Access**: ADMIN role only
- **Response**: Success message with card number

#### 6. Get Cards by User ID
```
GET /api/v1/cards/user/{userId}
```
Retrieve all cards for a specific user.
- **ADMIN**: Can view any user's cards
- **USER**: Can only view their own cards (userId must match token userId)
- **Response**: Success message (actual cards retrieved via service)

#### 7. Update Card
```
PUT /api/v1/cards/{cardId}
```
Update card PIN or status.
- **Access**: ADMIN (full access) or USER (own cards only, validated via `isCardOwnedByUser`)

**Request Body Examples:**
```json
// Change PIN
{
  "cardpin": "456"
}

// Change Status
{
  "cardstatus": "freeze"
}

// Both operations
{
  "cardpin": "789",
  "cardstatus": "active"
}
```

**Validation Rules:**
- PIN must be exactly 3 digits (regex: `\\d{3}`)
- Status must be either "freeze" or "active" (case-insensitive)
- Users can only update their own cards

#### 8. Generate Report (ADMIN only)
```
GET /api/v1/cards/report
```
Generates system reports.
- **Access**: ADMIN role only
- **Response**: Success message

## Service Layer Methods

Based on the controller implementation, the CardService includes:

- `getCards()` - Returns CardListDTO list
- `getAllCardsByAccountId(accountId)` - Returns CardModel list
- `createCard(accountId, userId, card)` - Returns CreateCardDTO
- `getcards(card)` - Search method returning CardModel list
- `blockcard(cardId)` - Returns blocked CardModel
- `findbyuser(userId)` - Finds cards by user
- `changepin(cardId, pin)` - Updates card PIN
- `freezecard(cardId, status)` - Updates card status
- `isAccountOwnedByUser(accountId, userId)` - Ownership validation
- `isCardOwnedByUser(cardId, userId)` - Card ownership validation

## Data Transfer Objects (DTOs)

- **CardListDTO**: Used for listing cards (GET /cards)
- **CreateCardDTO**: Used when creating new cards
- **CardModel**: Main card entity used in most operations

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/example/Card_Service/
│   │       ├── CardServiceApplication.java
│   │       ├── SecurityConfig.java
│   │       ├── controllers/
│   │       │   ├── CardController.java
│   │       │   └── TestController.java
│   │       ├── models/
│   │       │   └── CardModel.java
│   │       ├── repositories/
│   │       │   └── CardRepo.java
│   │       └── services/
│   │           ├── AuthService.java
│   │           ├── CardService.java
│   │           ├── CardStatusScheduler.java
│   │           ├── JwtUtil.java
│   │           └── dtos/
│   │               ├── CardDTO.java
│   │               ├── CardListDTO.java
│   │               └── CreateCardDTO.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/
        └── com/example/Card_Service/
            └── CardServiceApplicationTests.java
```

## Technologies Used

- **Java**: Programming language
- **Spring Boot**: Application framework
- **Spring Security**: Authentication and authorization
- **JWT**: Token-based authentication
- **Maven**: Dependency management and build tool
- **JPA/Hibernate**: Data persistence (implied by repository pattern)

## Security Implementation

### Authentication Flow
1. Client sends request with JWT token in Authorization header
2. `AuthService.validateToken()` validates the token
3. Returns user info map containing `role` and `userId`
4. Controller checks permissions based on role and user ownership

### Authorization Levels
- **ADMIN**: Full access to all operations and all users' data
- **USER**: Limited access to own resources only
  - Can view own account cards
  - Can create cards for themselves
  - Can update own cards
  - Can search cards (filtered results)

### Ownership Validation
- Account ownership: `cardService.isAccountOwnedByUser(accountId, userId)`
- Card ownership: `cardService.isCardOwnedByUser(cardId, userId)`

## Getting Started

### Prerequisites
- Java 11 or higher
- Maven 3.6+
- Database (configuration in application.properties)

### Running the Application

1. **Clone the repository**
2. **Navigate to project directory**
   ```bash
   cd Card_service_V2
   ```

3. **Build the project**
   ```bash
   ./mvnw clean install
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

The application will start on the default port (usually 8080).

## Configuration

Configure your database and other settings in `src/main/resources/application.properties`.

## Error Responses

The API returns appropriate HTTP status codes:
- `200`: Success
- `400`: Bad Request (invalid PIN format, invalid status values)
- `401`: Unauthorized (invalid/expired token)
- `403`: Forbidden (insufficient permissions, trying to access other user's resources)

## Validation Details

### PIN Validation
- Must be exactly 3 digits
- Regex pattern: `\\d{3}`
- Examples: "123", "456", "789"

### Status Validation
- Allowed values: "freeze", "active"
- Case-insensitive comparison
- Converted to lowercase before processing

## Debug Information
The update card endpoint includes debug logging:
```java
System.out.println("cardId = " + cardId + ", body = " + body + ", authHeader = " + authHeader + pin);
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

[Add your license information here]
