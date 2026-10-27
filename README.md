# EventHub

A secure REST API for discovering, publishing and applying to events. Organizers (managers) publish events, attendees browse, save and apply to them, and admins manage organizers, users and contact messages.

Built with **Spring Boot 4**, **Spring Security (JWT)**, **Spring Data JPA** and **MySQL**.

---

## Features

- **JWT authentication**: register and log in, stateless token validation through a custom filter
- **Role-based access control** with three roles:
  - **Admin**: manage organizers, users, promote users to manager, review contact messages
  - **Manager (organizer staff)**: create events, change event status, review and update applications
  - **Attendee**: manage profile (picture and resume), save events, apply to events
- **Event management**: create events, update status, track applications and their status
- **Organizers directory**: public listing, admin CRUD
- **Contact form**: public submission, admin review with sorting, pagination and status updates
- **CSRF protection** via cookie token (`/api/csrf-token/public`)
- **CORS** configurable per profile (`dev`, `qa`, `prod`)
- **Caching** with Caffeine (per-cache TTL and max size)
- **Auditing**: JPA auditing (`created_by`, `updated_by`, timestamps) plus AOP aspects for logging, performance timing, login-success and exception auditing, and registration validation
- **Global exception handling** with consistent error responses
- **API versioning** through the media type parameter (`application/vnd.eazyapp+json;v=1.0`)
- **Observability**: Spring Boot Actuator, OpenTelemetry (traces, metrics, logs) and Logback file logging
- **OpenAPI / Swagger UI** documentation via springdoc
- **Declarative HTTP clients** (`@ImportHttpServices`) demonstrated against JSONPlaceholder (`/api/posts`, `/api/todos`)

---

## Tech Stack

| Area            | Technology                                        |
| --------------- | ------------------------------------------------- |
| Language        | Java 26                                           |
| Framework       | Spring Boot 4.1.1 (Web MVC)                       |
| Security        | Spring Security, JJWT 0.13.0, BCrypt              |
| Persistence     | Spring Data JPA, MySQL                            |
| Caching         | Spring Cache + Caffeine                           |
| Validation      | Jakarta Bean Validation                           |
| Observability   | Actuator, OpenTelemetry, Logback                  |
| API docs        | springdoc-openapi 2.8.14                          |
| Build           | Maven                                             |
| Local infra     | Docker Compose (MySQL)                            |

---

## Project Structure

```
src/main/java/com/hagag/eventhub
├── EventHubAppliaction.java   # Application entry point
├── aspects/                   # Logging, performance, audit and validation aspects
├── audit/                     # JPA auditor (created_by / updated_by)
├── auth/                      # Login and register endpoints
├── cache/                     # Caffeine cache configuration
├── client/                    # Declarative HTTP clients (posts, todos demo)
├── config/web/                # API versioning and /api path prefix
├── constants/                 # Application constants
├── contacts/                  # Contact messages (controller + service)
├── dto/                       # Request / response DTOs
├── entity/                    # JPA entities
├── event/                     # Events (controller + service)
├── exception/                 # Global exception handler
├── organizer/                 # Organizers (controller + service)
├── otel/                      # OpenTelemetry log appender setup
├── repository/                # Spring Data repositories
├── security/                  # Security config, JWT filter, auth providers, CORS
├── user/                      # Users, profiles, saved events, applications
└── util/                      # Helpers
src/main/resources
├── application.properties     # Base config (port, CORS)
├── application-qa.properties  # QA profile
├── application-prod.properties# Production profile
├── logback-spring.xml         # Logging config
└── sql/
    ├── EventHub.schema.sql    # Database schema
    └── EventHub.data.sql      # Sample data
```

---

## Getting Started

### Prerequisites

- JDK 26
- Maven 3.9+ (or the included wrapper if you add one)
- Docker and Docker Compose (for the local MySQL database)

### 1. Clone the repository

```bash
git clone https://github.com/ahmedhagag4/-EventHub.git
cd -EventHub
```

### 2. Start the database

The project ships with a `compose.yml` that runs MySQL on host port **4306**:

```yaml
services:
  dbservice:
    image: mysql:latest
    container_name: EventHubDB
    ports:
      - "4306:3306"
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: EventHub
```

> **Note:** the bundled `compose.yml` mounts a volume from a path on the author's machine (`/home/hagag/Documents/eventHubSQL-doc`) and runs as user `1000:1000`. Change the volume path (or use a named volume) for your own environment.

In the default (dev) profile, `spring-boot-docker-compose` starts this file automatically when you run the app. You can also start it yourself:

```bash
docker compose up -d
```

### 3. Create the schema and load sample data

The SQL scripts are not run automatically. Load them once into the `EventHub` database:

```bash
mysql -h 127.0.0.1 -P 4306 -u root -proot EventHub < src/main/resources/sql/EventHub.schema.sql
mysql -h 127.0.0.1 -P 4306 -u root -proot EventHub < src/main/resources/sql/EventHub.data.sql
```

The schema creates these tables: `organizer`, `contacts`, `events`, `roles`, `users`, `profiles`, `saved_events` and `event_applications`.

### 4. Run the application

```bash
mvn spring-boot:run
```

The API starts on **http://localhost:8081** and all endpoints are served under the `/api` prefix.

---

## Configuration

| Property / Env variable                       | Description                                  | Default                    |
| --------------------------------------------- | -------------------------------------------- | -------------------------- |
| `server.port`                                 | HTTP port                                    | `8081`                     |
| `DATABASE_HOST`                               | MySQL host (qa / prod)                       | `localhost`                |
| `DATABASE_PORT`                               | MySQL port (qa / prod)                       | `4306`                     |
| `DATABASE_NAME`                               | Database name (qa / prod)                    | `eventhub`                 |
| `DATABASE_USERNAME`                           | Database user (qa / prod)                    | `root`                     |
| `DATABASE_PASSWORD`                           | Database password (qa / prod)                | `root`                     |
| `JWT_SECRET`                                  | HMAC secret used to sign tokens              | built-in dev value         |
| `jwt.expiration.hours`                        | Token lifetime (non-prod)                    | `1`                        |
| `jwt.prod.expiration.hours`                   | Token lifetime (prod)                        | `1`                        |
| `app.cors.allowed-origins`                    | Allowed CORS origins                         | set per profile            |
| `cache.jobs.ttl-minutes`, `cache.companies.ttl-minutes` | Cache TTLs                         | `5` (`15` on qa)           |

> **Important:** always set `JWT_SECRET` to a long random value outside local development. Never rely on the built-in default in QA or production.

### Profiles

```bash
# QA
mvn spring-boot:run -Dspring-boot.run.profiles=qa

# Production
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

The `prod` profile disables Docker Compose integration and OpenTelemetry exporters.

---

## Authentication

1. Fetch a CSRF token if your client needs one: `GET /api/csrf-token/public`
2. Register: `POST /api/auth/register/public` (new users get the attendee role)
3. Log in: `POST /api/auth/login/public`

```json
{
  "username": "user@example.com",
  "password": "your-password"
}
```

The response contains the user details and a JWT. Send it on every protected request:

```
Authorization: Bearer <token>
```

---

## API Overview

All paths are prefixed with `/api`. Endpoints ending in `/public` need no token; the suffix `/admin`, `/manager` or `/attendee` indicates the required role.

### Auth

| Method | Endpoint                    | Access |
| ------ | --------------------------- | ------ |
| POST   | `/auth/register/public`     | Public |
| POST   | `/auth/login/public`        | Public |
| GET    | `/csrf-token/public`        | Public |

### Events (Manager)

| Method | Endpoint                              | Description                      |
| ------ | ------------------------------------- | -------------------------------- |
| GET    | `/events/manager`                     | List the manager's events        |
| POST   | `/events/manager`                     | Create an event                  |
| PATCH  | `/events/{eventId}/status/manager`    | Update event status              |
| GET    | `/events/applications/{eventId}/manager` | List applications for an event |
| PATCH  | `/events/applications/manager`        | Update an application's status   |

### Attendee

| Method | Endpoint                                      | Description                  |
| ------ | --------------------------------------------- | ---------------------------- |
| GET / PUT | `/users/profile/attendee`                  | View / update profile        |
| GET    | `/users/profile/picture/attendee`             | Get profile picture          |
| GET    | `/users/profile/resume/attendee`              | Get resume                   |
| GET    | `/users/saved-events/attendee`                | List saved events            |
| POST / DELETE | `/users/saved-events/{eventId}/attendee` | Save / unsave an event       |
| GET / POST | `/users/event-applications/attendee`      | List / submit applications   |
| DELETE | `/users/event-applications/{eventId}/attendee`| Withdraw an application      |

### Organizers

| Method | Endpoint                | Access |
| ------ | ----------------------- | ------ |
| GET    | `/organizer/public`     | Public |
| GET    | `/organizer/admin`      | Admin  |
| POST   | `/organizer/admin`      | Admin  |
| PUT    | `/organizer/{id}/admin` | Admin  |
| DELETE | `/organizer/{id}/admin` | Admin  |

### Contacts

| Method | Endpoint                         | Access |
| ------ | -------------------------------- | ------ |
| POST   | `/contacts/public`               | Public |
| GET    | `/contacts/admin`                | Admin  |
| GET    | `/contacts/sort/admin`           | Admin  |
| GET    | `/contacts/page/admin`           | Admin  |
| PATCH  | `/contacts/{id}/status/admin`    | Admin  |

### Users (Admin)

| Method | Endpoint                                       | Description                       |
| ------ | ---------------------------------------------- | --------------------------------- |
| GET    | `/users/search/admin`                          | Search users                      |
| PATCH  | `/users/{userId}/role/manager/admin`           | Promote a user to manager         |
| PATCH  | `/users/{userId}/organizer/{organizerId}/admin`| Assign a user to an organizer     |

### Demo (external HTTP clients)

`/api/posts` and `/api/todos` support full CRUD and proxy to [JSONPlaceholder](https://jsonplaceholder.typicode.com) to showcase Spring's declarative HTTP service clients.

For full request and response schemas, open the Swagger UI once the app is running (`/swagger-ui.html`).

---

## Roles

| Role in code     | Meaning   |
| ---------------- | --------- |
| `ROLE_ADMIN`     | Admin     |
| `ROLE_EMPLOYER`  | Manager   |
| `ROLE_JOB_SEEKER`| Attendee  |

---

## Monitoring and Logging

- Actuator endpoints are exposed for health and metrics.
- Application logs are written to `logs/eventhub.log` (see `logback-spring.xml`).
- OpenTelemetry traces, metrics and logs are enabled in dev and disabled in the `prod` profile by default.

---

## Build

```bash
mvn clean package
java -jar target/jobportal-aws-deployment.jar
```

---

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m "Add my feature"`
4. Push the branch and open a Pull Request

---

## Author

**Ahmed Hagag**: [github.com/ahmedhagag4](https://github.com/ahmedhagag4)
