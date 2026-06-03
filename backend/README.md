# KaamWala — Hyperlocal Worker Marketplace Backend

KaamWala is a hyperlocal worker marketplace backend that connects local skilled workers (carpenters, electricians, plumbers, painters, AC technicians, CCTV installers, RO service technicians, home cleaning services, masons, welders, and furniture makers) directly with customers in their area.

---

## 🏛️ Project Skeleton & Folder Structure

The project follows a standard Maven folder structure and implements Clean/Layered Architecture:

```text
kaamwala-backend/
├── pom.xml                                   # Project dependencies & build config
├── README.md                                 # This guide
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── kaamwala/
        │           ├── KaamWalaApplication.java # Spring Boot entry point
        │           ├── config/               # Security, WebSockets, Redis configurations
        │           ├── controller/           # REST Controllers
        │           ├── dto/                  # Request & Response Data Transfer Objects
        │           ├── entity/               # JPA Entities / Database Models
        │           ├── exception/            # Exception models & Global Exception Handler
        │           ├── repository/           # Spring Data JPA repositories
        │           ├── security/             # JWT filters, UserDetailsService
        │           ├── service/              # Business Logic Interfaces & Implementations
        │           └── websocket/            # WebSocket chat handlers & interceptors
        └── resources/
            ├── application.yml               # Database, Redis, JWT config
            └── schema.sql                    # Initial PostgreSQL & PostGIS DDL
```

---

## 📦 Core Dependencies (pom.xml)

Here is a breakdown of the dependencies configured in the [pom.xml](file:///Users/Automation/.gemini/antigravity/scratch/kaamwala/backend/pom.xml):

| Dependency | Purpose | Why We Need It |
|---|---|---|
| `spring-boot-starter-web` | REST API | Enables routing, controllers, MVC support, and standard HTTP services. |
| `spring-boot-starter-data-jpa` | ORM Layer | Enables easy integration with PostgreSQL using Hibernate. |
| `spring-boot-starter-security` | Security / Auth | Secures routes and configures authentication filters. |
| `spring-boot-starter-websocket` | Real-time Chat | Supports dual bidirectional socket connections for client-worker chat. |
| `spring-boot-starter-validation` | Input Validation | Validates API request bodies (e.g., checks phone number formats, non-empty fields). |
| `spring-boot-starter-data-redis` | In-memory Cache | Used for storing OTP codes temporarily (5-min TTL) and caching nearby workers. |
| `postgresql` | Database Driver | Driver for connecting to the PostgreSQL/PostGIS database. |
| `lombok` | Boilerplate reduction | Generates getters, setters, constructors, and builders automatically. |
| `jjwt-api` / `jjwt-impl` | JWT Authentication | Handles creation and verification of JSON Web Tokens. |
| `springdoc-openapi` | API Documentation | Generates automated Swagger UI documentation for all endpoints. |
| `mapstruct` | Object Mapping | Maps Entities to DTOs and vice-versa cleanly without manual boilerplates. |

---

## 📂 Package-by-Package Breakdown

### 1. Entities (`com.kaamwala.entity`)
Contains our database models. These use Hibernate JPA annotations to map to PostgreSQL tables:
- **`User`**: Core user accounts containing role information (`CUSTOMER`, `WORKER`, `ADMIN`).
- **`WorkerProfile`**: Profile specific to workers detailing their skills, starting price, service area, verification badges, average rating, etc.
- **`JobPost`**: Customer job requests containing the category, description, photos, location coordinates, budget range, and urgency level.
- **`Bid`**: Worker bids on job posts detailing the custom quotation price, message, and estimated duration.
- **`Booking`**: Booking contracts created when a customer accepts a bid, including the state machine statuses (`CONFIRMED`, `WORKER_EN_ROUTE`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`).
- **`Review`**: Rating and text feedback submitted by the customer after booking completion.
- **`ChatMessage`**: WebSocket/Database records for messages exchanged during a job.

### 2. Repositories (`com.kaamwala.repository`)
Interfaces extending `JpaRepository` to run database transactions. They define custom queries like:
- Finding workers within $N$ kilometers using the **Haversine formula**.
- Fetching active jobs in specific categories.
- Querying chat logs for a booking.

### 3. Services (`com.kaamwala.service`)
The business logic layer. Implements logic around:
- OTP generation, SMS integrations, and JWT issuance.
- Checking bid counts to enforce Worker subscription limits.
- OTP verification steps to verify a worker has arrived at a client's location and started working.

### 4. Controllers (`com.kaamwala.controller`)
Our API routes that receive request bodies, forward them to the Service layer, and return standard JSON formats.

### 5. DTOs (`com.kaamwala.dto`)
Data Transfer Objects that separate our database structures from the presentation layer, preventing circular references and keeping data lightweight.

### 6. Security (`com.kaamwala.security` & `config`)
- **`SecurityConfig`**: Sets up URL matchers (public vs authenticated), disables default forms, and hooks in our JWT filter.
- **`JwtAuthenticationFilter`**: Intercepts requests, reads the Authorization header, validates the signature, and injects authentication context into Spring.
- **`JwtTokenProvider`**: Utility class to issue and decrypt JWT claims.

---

## 🛠️ Let's Code Together!

Now that the skeleton and dependencies are laid out, we will build KaamWala step-by-step. Here is the sequence we will follow:

### Step 1: Core Domain Entities
- We write the `ServiceCategory` enum.
- We implement `User`, `WorkerProfile`, and `JobPost` tables.
- We set up database relationships (Many-to-One, One-to-One).

### Step 2: Custom Geolocation Repositories
- We write the geospatial native query to find workers within a specific radius using `lat` and `lng`.
- We write the query to display jobs matching a worker's category and distance.

### Step 3: Auth & Security (OTP + JWT)
- Set up phone number validation.
- Generate OTPs and save to Redis.
- Secure the backend and implement JWT filters.

### Step 4: Reverse Bidding & Bookings
- Customers posting jobs, workers bidding.
- Acceptance of bids creating bookings.
- OTP verification for beginning work and completion.

### Step 5: WebSockets Chat
- Set up broker destinations.
- Write chat websocket handlers and save history.

Let's begin! Tell me which step you'd like to dive into first, and I will write the code with detailed explanations.
