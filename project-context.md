# Winter Sports Competition System - Project Context

## Architecture
- Spring Boot 4, Java 21, Gradle
- MySQL (Azure Flexible Server)
- Package: com.wintersports
- Port: 8081

## Package Structure
- entities/ → JPA entities extending BaseEntity
- repositories/{module}/ → interfaces extending IBaseRepository
- services/{module}/ → IBaseService<TResponse, TCreateRequest, TUpdateRequest, ID>
- controllers/{module}/ → REST controllers
- dtos/requests/ → request DTOs with validations
- dtos/responses/ → response DTOs
- exceptions/{ExceptionName}/ → custom exceptions
- aspects/ → AOP logging
- enums/ → Gender and other enums

## Conventions
- Repositories: I prefix (IBaseRepository, ITournamentRepository)
- Services: I prefix (IBaseService, ITournamentService)
- All exceptions extend RuntimeException
- Mapping entity ↔ DTO happens in service layer
- Controllers only handle HTTP

## Completed Modules
- TournamentType ✅
- Tournament ✅
- Competition (SlalomCompetition, BiathlonCompetition) ✅
- CI/CD pipeline ✅
- Unit тестове (53 теста) ✅
- GlobalExceptionHandler ✅
- ModelMapper конфигурация ✅
- JWT Security ✅
- Auth (register/login) ✅
- Athlete Profile ✅
- User Management (approve/reject) ✅
- Registration ✅

## Remaining Modules
- Results (SlalomResult, BiathlonResult)
- Medal
- Statistics endpoints
- AOP logging

## Key Technical Decisions
- ModelMapper with STRICT matching strategy (to avoid FK fields being mapped to id)
- ModelMapperConfig.mapList() as a static method accepting ModelMapper as parameter
- create — manual entity mapping + ModelMapper only for response (avoids ObjectOptimisticLockingFailureException)
- update — modelMapper.map(request, entity) to update existing entity fields
- Gender as enum in enums/ package with @Enumerated(EnumType.STRING)
- CompetitionService handles getAll/getById/delete for all competition types
- SlalomCompetitionService and BiathlonCompetitionService handle only create/update
- Single CompetitionController for all competition endpoints
- InvalidCompetitionDateException — validates competition date is within tournament start/end dates
- All custom exceptions extend RuntimeException and live in exceptions/{ExceptionName}/ packages
- GlobalExceptionHandler (@RestControllerAdvice) handles 400/401/403/404/409/500
- JWT Security with JwtService, JwtAuthenticationFilter, SecurityConfig
- @PreAuthorize for method-level security (ADMIN/ATHLETE roles)
- AthleteProfile separate from User (1:1) for extensibility
- Registration validates: deadline, gender, age, duplicate
- SecurityContextHolder used in create methods to get logged-in user
- @DataJpaTest available via spring-boot-data-jpa-test dependency (Spring Boot 4)

## Testing Conventions
- Unit tests only for service layer
- Mockito + JUnit 5
- @ExtendWith(MockitoExtension.class)
- Mock ModelMapper in every service test