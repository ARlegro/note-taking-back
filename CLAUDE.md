# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Note-taking backend application built with Spring Boot 4.0 and Java 21, using PostgreSQL for data persistence. The project follows strict Test-Driven Development (TDD) methodology.

## Tech Stack

- Java 21
- Spring Boot 4.0
- Spring Data JPA + PostgreSQL
- MapStruct for DTO mapping
- JUnit 5 + Mockito for testing
- SpringDoc OpenAPI (Swagger UI)

## Development Commands

### Build & Run

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Start PostgreSQL database
docker-compose up -d postgres

# Stop database
docker-compose down
```

### Testing

```bash
# Run all tests
./gradlew test

# Run a specific test class
./gradlew test --tests "prac.demonote.domain.note.NoteServiceTest"

# Run tests with detailed output
./gradlew test --info
```

### Other Commands

```bash
# Clean build artifacts
./gradlew clean

# Check dependencies
./gradlew dependencies

# Generate MapStruct mappers (happens automatically during build)
./gradlew compileJava
```

## Database Configuration

- **Database**: PostgreSQL (running in Docker)
- **Connection**: localhost:5432/note
- **Credentials**: user/user1234 (see docker-compose.yml)
- **Hibernate DDL**: create (recreates schema on startup)
- **Schema Design**: See schema.sql for the planned database structure

## Architecture & Code Organization

### Layered Architecture

Controller → (Facade) → Service → Repository

- **Controller**: REST endpoints, request/response handling
- **Facade**: Optional layer for complex orchestration (e.g., file processing with strategy pattern)
- **Service**: Business logic
- **Repository**: Data access with Spring Data JPA

### Package Structure

```
prac.demonote/
├── domain/
│   ├── note/
│   │   ├── model/         # JPA entities
│   │   ├── dto/           # Request/Response DTOs
│   │   ├── NoteController.java
│   │   ├── NoteService.java
│   │   ├── NoteRepository.java
│   │   └── NoteMapper.java (if using MapStruct)
│   ├── attachment/
│   │   ├── AttachmentFacade.java  # Strategy pattern for file processing
│   │   └── ...
│   └── [other domains]/
└── DemoNoteApplication.java
```

### Naming Conventions

- **Entity**: `{Domain}.java` (e.g., `Note.java`)
- **Repository**: `{Domain}Repository.java`
- **Service**: `{Domain}Service.java` / `{Domain}ServiceImpl.java`
- **Controller**: `{Domain}Controller.java`
- **Facade**: `{Domain}Facade.java`
- **DTOs**: `{Domain}{Action}RequestDTO.java` / `{Domain}{Action}ResponseDTO.java`
- **Mapper**: `{Domain}Mapper.java`

### MapStruct Convention

```java
@Mapper(componentModel = "spring")
public interface {Entity}Mapper {
    {Entity}Response toResponse({Entity} entity);
    {Entity} toEntity({Entity}Request request);
}
```

## CRITICAL: Test-Driven Development (TDD)

This project MUST follow TDD. Never write implementation code before tests.

### TDD Cycle (MANDATORY)

1. **RED**: Write the test first (no implementation exists yet)
2. **RED**: Run the test and confirm it fails
3. **GREEN**: Write minimal code to pass the test
4. **GREEN**: Run the test and confirm it passes
5. **REFACTOR**: Improve code quality while keeping tests passing
6. **GREEN**: Re-run tests to ensure they still pass

### Prohibited Actions

- Writing implementation code before tests
- Running tests without first confirming failure
- Modifying tests during implementation phase

### Test Naming & Structure

- Use `given_when_then` pattern for test method names
- Example: `givenValidNote_whenSave_thenReturnsSavedNote()`

### Test Implementation Order

#### 1. Repository Layer (`@DataJpaTest`)
- Test entity persistence and retrieval
- Implement Entity and Repository

#### 2. Service Layer (`@ExtendWith(MockitoExtension.class)`)
- Test business logic with mocked dependencies
- Implement Service and Mapper

#### 3. Controller Layer (`@WebMvcTest`)
- Test API endpoints with MockMvc
- Implement Controller

## API Documentation

- Swagger UI: http://localhost:8080/swagger
- Default content type: application/json

## Design Patterns

- **Strategy Pattern**: Used for attachment/file processing (see `AttachmentFacade`)
- **DTO Pattern**: MapStruct for entity-DTO conversion
- **Global Exception Handling**: RestControllerAdvice for centralized error responses

## Important Notes

- Application runs on port 8080 (default Spring Boot)
- DevTools enabled for auto-restart during development
- File upload max size: 10MB per file, 50MB per request
- Temp upload location: /tmp
- Generated MapStruct mappers are in src/main/generated/
