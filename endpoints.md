## Endpoints & Roles

### Auth
- POST /api/auth/register/athlete → Public
- POST /api/auth/register/admin → ADMIN
- POST /api/auth/login → Public

### Tournament Types
- GET /api/tournament-types → ADMIN
- GET /api/tournament-types/{id} → ADMIN
- POST /api/tournament-types → ADMIN
- PUT /api/tournament-types/{id} → ADMIN
- DELETE /api/tournament-types/{id} → ADMIN

### Tournaments
- GET /api/tournaments → Public
- GET /api/tournaments/{id} → Public
- POST /api/tournaments → ADMIN
- PUT /api/tournaments/{id} → ADMIN
- DELETE /api/tournaments/{id} → ADMIN

### Competitions
- GET /api/competitions → Public
- GET /api/competitions/{id} → Public
- POST /api/competitions/slalom → ADMIN
- POST /api/competitions/biathlon → ADMIN
- PUT /api/competitions/slalom/{id} → ADMIN
- PUT /api/competitions/biathlon/{id} → ADMIN
- DELETE /api/competitions/{id} → ADMIN

### Users
- GET /api/users → ADMIN
- PUT /api/users/{id}/status → ADMIN

### Athletes
- GET /api/athletes → Public
- GET /api/athletes/{id} → Public
- PUT /api/athletes/{id} → ATHLETE (own profile)
- DELETE /api/athletes/{id} → ADMIN

### Registrations
- GET /api/registrations → ADMIN (grouped by athlete)
- POST /api/registrations → ATHLETE
- PUT /api/registrations/{id}/status → ADMIN
- DELETE /api/registrations/{id} → ADMIN

### Results
- GET /api/results → Public
- GET /api/results/{id} → Public
- GET /api/results/competition/{competitionId} → Public
- GET /api/results/slalom/{competitionId}/run2-qualifiers → Public
- POST /api/results/slalom → ADMIN
- PUT /api/results/slalom/{id} → ADMIN
- POST /api/results/biathlon → ADMIN
- PUT /api/results/biathlon/{id} → ADMIN
- DELETE /api/results/{id} → ADMIN

### Medals
- GET /api/medals → Public
- GET /api/medals/by-country → Public

### Statistics
- GET /api/rankings/{competitionId} → Public
- GET /api/statistics/medals-by-country → Public
- GET /api/statistics/average-age/{competitionId} → Public
- GET /api/statistics/youngest-oldest-medalist → Public