# Project Context


## Tech Stack
- Java 21, Spring Boot 4.0
- Spring Data JPA + PostgreSQL
- MapStruct for DTO mapping
- JUnit 5 + Mockito

## Architecture
- Layered: Controller → Service → Repository
 - 필요 시 Facade 
- DTO pattern with MapStruct
- RestControllerAdvice for exception handling
- attach(File)처리 시 전략패턴 사용

## TDD Convention
1. Repository Layer: @DataJpaTest
2. Service Layer: @ExtendWith(MockitoExtension.class)
3. Controller Layer: @WebMvcTest

**⚠ CRITICAL : 이 프로젝트는 반드시 Test-Driven Development로 개발합니다.**

TDD 필수 규칙(위반 금지):
❌테스트 없이 구현 코드를 먼저 작성하면 안 됩니다.
❌테스트 실패를 확인하지 않고 구현하면 안 됩니다.
❌구현 중에 테스트를 수정하면 안됩니다.

✅반드시 이 순서를 따라야 합니다:

1. 🟥RED: 테스트를 먼저 작성(구현은 없음)
2. 🟥RED: 테스트를 실행하여 실패 확인
3. 🟢GREEN : 테스트를 통과시키는 최소한의 구현
4. 🟢GREEN : 테스트를 실행하여 통과 확인
5. 🟦REFACTORING: 코드 개선(테스트는 여전히 통과)
6. 🟢GREEN : 테스트를 재실행하며 여전히 통과 확인

## Test Naming
- given_when_then 패턴

## TDD Steps
### Step 1: Repository Layer
- Test: 저장/조회 테스트
- Impl: Entity, Repository 구현

### Step 2: Service Layer
- Test: 비즈니스 로직 테스트
- Impl: Service, Mapper 구현

### Step 3: Controller Layer
- Test: API 엔드포인트 테스트
- Impl: Controller 구현

## Database Changes
- [ ] Migration script needed?

## MapStruct Convention
@Mapper(componentModel = "spring")
public interface {Entity}Mapper {
{Entity}Response toResponse({Entity}Entity entity);
{Entity}Entity toEntity({Entity}Request request);
}


## Naming Convention
- Entity: ~.java
- Repository: ~Repository.java
- Service: ~Service.java / ~ServiceImpl.java
- DTO: ~Request.java / ~Response.java
- Mapper: ~Mapper.java

