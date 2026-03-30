# 🎿 Winter Sports Competition System — Backend

A RESTful API for managing winter sports competitions, built with Spring Boot. Supports multiple tournament types (Olympics, World Championships, European Championships, and more), athlete registration, competition results, and automatic medal assignment.

---

## 🏗️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3 |
| ORM | Spring Data JPA / Hibernate |
| Security | Keycloak (OAuth2) |
| Database | MySQL 8 (Azure Database for MySQL Flexible Server) |
| Storage | Azure Blob Storage |
| AOP | Spring AOP (logging) |
| Testing | JUnit 5 + Mockito |
| CI/CD | GitHub Actions |
| Cloud | Microsoft Azure (App Service) |

---

## 🗂️ Project Structure

```
src/
├── main/
│   ├── java/com/wintersports/
│   │   ├── config/          # Security, Keycloak, AOP config
│   │   ├── controller/      # REST controllers
│   │   ├── dto/             # Request / Response DTOs
│   │   ├── entity/          # JPA entities
│   │   │   ├── competition/ # Competition, SlalomCompetition, BiathlonCompetition
│   │   │   └── result/      # CompetitionResult, SlalomResult, BiathlonResult
│   │   ├── exception/       # Global exception handling
│   │   ├── repository/      # Spring Data JPA repositories
│   │   ├── service/         # Business logic
│   │   └── aspect/          # AOP logging aspects
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/wintersports/
        └── service/         # Unit tests
```

---

## 🧬 Domain Model

The system uses **JPA Joined Table Inheritance** to model different competition and result types:

```
Competition (abstract)
├── SlalomCompetition     → maxRun2Participants
└── BiathlonCompetition   → lapsCount, shootingRounds, penaltySeconds

CompetitionResult (abstract)
├── SlalomResult          → run1Time, run2Time
└── BiathlonResult        → skiTime, missedShots, penaltyTime
```

Each result type implements `calculateTotalTime()` — demonstrating **polymorphism**.

---

## ⚙️ Business Logic

### Ski Slalom
- Athletes compete in **Run 1**
- Top N athletes (configurable via `maxRun2Participants`) advance to **Run 2**
- Run 2 start order is **reversed** — slowest from Run 1 starts first
- Final ranking = Run 1 time + Run 2 time
- DNF athletes are excluded from final ranking

### Biathlon
- Athletes ski a number of laps with shooting stages
- Each missed shot adds penalty time: `missedShots × penaltySeconds`
- Final time = `skiTime + penaltyTime`
- DNF athletes are excluded

### Medals
- Automatically assigned after competition is finalized
- Top 3 athletes receive GOLD, SILVER, BRONZE
- Unique constraint: one medal type per competition

---

## 🔐 Roles & Access

| Role | Permissions |
|---|---|
| **Public** | View rankings, medals, statistics — no login required |
| **ATHLETE** | Manage own profile, register for competitions (pending admin approval) |
| **ADMIN** | Manage tournaments, competitions, approve registrations, enter results |

> New athlete accounts require **admin approval** before access is granted.
> Competition registrations also require **admin approval**.

---

## 🚀 Getting Started

### Prerequisites
- Java 21
- Maven 3.9+
- MySQL 8 (local or Azure)
- Keycloak instance running

### Environment Variables

```properties
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/winter_sports
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
KEYCLOAK_AUTH_SERVER_URL=http://localhost:8080
KEYCLOAK_REALM=winter-sports
KEYCLOAK_CLIENT_ID=winter-sports-api
AZURE_BLOB_CONNECTION_STRING=your_connection_string
AZURE_BLOB_CONTAINER=covers
```

### Run Locally

```bash
git clone https://github.com/your-username/winter-sports-backend.git
cd winter-sports-backend
mvn spring-boot:run
```

API will be available at `http://localhost:8081`

---

## 📡 API Overview

| Module | Base Path |
|---|---|
| Auth | `/api/auth` |
| Tournament Types | `/api/tournament-types` |
| Tournaments | `/api/tournaments` |
| Competitions | `/api/competitions` |
| Athletes | `/api/athletes` |
| Registrations | `/api/registrations` |
| Results | `/api/results` |
| Medals | `/api/medals` |
| Rankings | `/api/rankings` |

---

## 🔄 CI/CD Pipeline

Merging a feature branch into `develop` triggers the GitHub Actions pipeline:

```
Push to develop
    └── Build & Test (Maven)
        └── Docker Build
            └── Deploy to Azure App Service (develop environment)
```

---

## 🧪 Running Tests

```bash
mvn test
```

Unit tests cover all service layer methods using JUnit 5 and Mockito.

---

## 📄 License

This project is developed as a university assignment.
