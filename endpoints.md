## Endpoints & Roles

### Auth
- POST /api/auth/register → Public
- POST /api/auth/login → Public

### Tournament Types
- GET /api/tournament-types → Public
- GET /api/tournament-types/{id} → Public
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
- POST /api/competitions → ADMIN
- PUT /api/competitions/{id} → ADMIN
- DELETE /api/competitions/{id} → ADMIN

### Athletes
- GET /api/athletes → Public
- GET /api/athletes/{id} → Public
- POST /api/athletes → Public (registration)
- PUT /api/athletes/{id} → ATHLETE (own profile)
- DELETE /api/athletes/{id} → ADMIN

### Registrations
- GET /api/registrations → ADMIN
- POST /api/registrations → ATHLETE
- PUT /api/registrations/{id}/status → ADMIN
- DELETE /api/registrations/{id} → ADMIN

### Results
- GET /api/results → Public
- GET /api/results/{id} → Public
- POST /api/results → ADMIN
- PUT /api/results/{id} → ADMIN

### Medals
- GET /api/medals → Public
- GET /api/medals/by-country → Public

### Finance/Reports
- GET /api/rankings → Public
- GET /api/statistics/medals-by-country → Public
- GET /api/statistics/average-age → Public
- GET /api/statistics/youngest-oldest-medalist → Public