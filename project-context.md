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

## Conventions
- Repositories: I prefix (IBaseRepository, ITournamentRepository)
- Services: I prefix (IBaseService, ITournamentService)
- All exceptions extend RuntimeException
- Mapping entity ↔ DTO happens in service layer
- Controllers only handle HTTP

## Completed Modules
- TournamentType ✅
- Tournament ✅

## Remaining Modules
- Competition (SlalomCompetition, BiathlonCompetition)
- Athlete + User
- Registration
- CompetitionResult (SlalomResult, BiathlonResult)
- Medal
- JWT Security

## Key Technical Decisions
- ModelMapper с STRICT matching strategy
- ModelMapperConfig.mapList() - static method
- create — mapping for entity + ModelMapper only for response
- update — modelMapper.map(request, entity) for update the fields