# Winter Sports Competition System - Project Context

## Architecture
- Spring Boot 4, Java 21, Gradle
- MySQL (Azure Flexible Server)
- Package: com.wintersports
- Port: 8081

## Package Structure
- entities/ → JPA entities extending BaseEntity
- entities/competition/ → Competition hierarchy (SlalomCompetition, BiathlonCompetition)
- entities/result/ → Result hierarchy (SlalomResult, BiathlonResult)
- repositories/{module}/ → interfaces extending IBaseRepository
- services/{module}/ → IBaseService<TResponse, TCreateRequest, TUpdateRequest, ID>
- controllers/{module}/ → REST controllers
- dtos/requests/ → request DTOs with validations
- dtos/responses/ → response DTOs
- exceptions/{ExceptionName}/ → custom exceptions
- aspects/ → AOP logging
- enums/ → Gender, Role, UserStatus, RegistrationStatus and other enums
- filters/ → JwtAuthenticationFilter

## Conventions
- Repositories: I prefix (IBaseRepository, ITournamentRepository)
- Services: I prefix (IBaseService, ITournamentService)
- All exceptions extend RuntimeException
- Mapping entity ↔ DTO happens in service layer
- Controllers only handle HTTP
- create — manual entity mapping + ModelMapper only for response
- update — modelMapper.map(request, entity) to update existing entity fields

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
- Results (SlalomResult, BiathlonResult) ✅

## Remaining Modules
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
- CompetitionResultService handles getAll/getById/delete for all result types
- SlalomResultService and BiathlonResultService handle only create/update
- Single CompetitionController for all competition endpoints
- Single ResultController for all result endpoints
- InvalidCompetitionDateException — validates competition date is within tournament start/end dates
- All custom exceptions extend RuntimeException and live in exceptions/{ExceptionName}/ packages
- GlobalExceptionHandler (@RestControllerAdvice) handles 400/401/403/404/409/500
- JWT Security with JwtService, JwtAuthenticationFilter, SecurityConfig
- @PreAuthorize for method-level security (ADMIN/ATHLETE roles)
- AthleteProfile separate from User (1:1) for extensibility
- Registration validates: deadline, gender, age, duplicate, approved registration required for results
- SecurityContextHolder used in create methods to get logged-in user
- InheritanceType.JOINED for Competition and CompetitionResult hierarchies
- @PrimaryKeyJoinColumn(name = "result_id") on SlalomResult and BiathlonResult
- BigDecimal for all time fields (precision 10, scale 3)
- totalTime = null for DNF athletes
- SlalomResult: run2 qualifiers validated before update
- BiathlonResult: penaltyTime = missedShots * penaltySeconds, totalTime = skiTime + penaltyTime
- globally_quoted_identifiers=true in test application.properties (H2 compatibility)
- @DataJpaTest available via spring-boot-data-jpa-test dependency (Spring Boot 4)

## Testing Conventions
- Unit tests only for service layer
- Mockito + JUnit 5
- @ExtendWith(MockitoExtension.class)
- Mock ModelMapper in every service test
- Repository tests with @DataJpaTest + TestEntityManager
- Security context mocked with SecurityContextHolder in tests that need it
- Private setupCompetition()/setupSecurityContext() helper methods for reusable stubs