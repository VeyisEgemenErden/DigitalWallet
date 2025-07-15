# Digital Wallet

## Description

This is a Spring Boot-based digital wallet application that allows users to manage virtual wallets. Customers can create wallets, deposit and withdraw funds, and view their transactions. The system enforces access control based on user roles (EMPLOYEE and CUSTOMER).

---

## Tech Stack

* Java 17
* Spring Boot 3.x
* Spring Security (JWT-based authentication)
* PostgreSQL
* JPA (Hibernate)
* JUnit + Mockito for testing
* Docker & Docker Compose

---

## Project Structure

```
digital-wallet-starter/
├── controller/        # REST controllers for handling HTTP requests
├── dto/               # Data transfer objects for requests and responses
├── entity/            # JPA entity classes representing database tables
├── enums/             # Enums for domain-specific values (Currency, Status, Roles, etc.)
├── repository/        # Interfaces for data persistence with Spring Data JPA
├── security/          # Security configurations including JWT utilities
├── service/           # Business logic layer
├── config/            # Additional configurations (e.g., admin initializer)
├── DigitalWalletStarterApplication.java
├── resources/
│   └── application.yml
├── Dockerfile
├── docker-compose.yml
└── .env
```

---

## How to Run the Application

### Docker

1. Make sure Docker and Docker Compose are installed.
2. Create a `.env` file containing:

```env
POSTGRES_DB=walletdb
POSTGRES_USER=wallet
POSTGRES_PASSWORD=wallet123
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/walletdb
SPRING_DATASOURCE_USERNAME=wallet
SPRING_DATASOURCE_PASSWORD=wallet123
JWT_SECRET=VerySecretJWTKeyForSigningTokens
JWT_EXPIRATION_MS=3600000
```

3. Run the following command:

```bash
docker-compose up --build
```

4. The application will be available at: [http://localhost:8080](http://localhost:8080)

---

##  Authentication Endpoints

* `POST /auth/login` – Authenticate and receive a JWT token

Include the JWT in the `Authorization` header of requests:

```http
Authorization: Bearer <your_token_here>
```

---
## User Endpoints

* `POST /api/createUser` – Create a user


---

## Wallet Endpoints

* `POST /api/wallets/create` – Create a wallet
* `POST /api/wallets/deposit` – Deposit funds into a wallet
* `POST /api/wallets/withdraw` – Withdraw funds from a wallet
* `GET /api/customers/{customerId}/wallets` – View a customer's wallets

---

##  Transaction Endpoints

* `GET /api/wallets/{walletId}/transactions` – List transactions (only available to the wallet owner)
* `POST /api/transactions/approve` – Approve or deny transactions (restricted to EMPLOYEE role)

---

##  Tests


These tests cover:

* Authentication and authorization logic
* Wallet creation and updates
* Transaction processing and approvals
* Role-based access control

---

##  Additional Information

* Withdrawals above a configurable threshold (default: 1000) are marked as `PENDING` and require employee approval.
* Customers are restricted to managing only their own wallets and transactions.
* A default admin user is auto-created on startup if not present:

    * Username: `admin`
    * Password: `admin123`

---

##  Docker Details

* PostgreSQL service is exposed on port `5432`
* Application service is exposed on port `8080`
* Database data is stored in a named volume `pgdata` to persist across container restarts
