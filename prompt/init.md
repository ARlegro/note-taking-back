# CLAUDE.md (Spring)

## Stack
Java 21, Spring Boot, JPA, Querydsl, PostgreSQL

## Rules (CRITICAL)
- 무조건 TDD: RED → GREEN → REFACTOR
- Controller는 로직 금지 (요청/응답만)
- Service는 Entity 반환 금지 (Response DTO 반환)
- 도메인 예외는 @RestControllerAdvice에서 HTTP로 매핑
- JPA의 변경감지 활용하기 
- 

## Architecture
- domain/api/app/infra 분리
- Repository는 DB 접근만
- Entity는 의미있는 메서드로 상태 변경 (setter 최소화)

## Testing
- Service: JUnit5 + Mockito
- Repository: Testcontainers(PostgreSQL)
- Web: MockMvc

