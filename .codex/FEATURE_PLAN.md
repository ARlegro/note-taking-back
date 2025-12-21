# Feature: {feature_name}

## Requirements
- {요구사항}

## Affected Files
### New Files
- [ ] src/main/java/.../entity/{Entity}Entity.java
- [ ] src/main/java/.../repository/{Entity}Repository.java
- [ ] src/main/java/.../service/{Entity}Service.java
- [ ] src/main/java/.../dto/{Entity}Request.java
- [ ] src/test/java/.../repository/{Entity}RepositoryTest.java
- [ ] ...



### Modified Files
- [ ] existing file path

# AGENTS.md에 추가

## MapStruct Convention
@Mapper(componentModel = "spring")
public interface {Entity}Mapper {
    {Entity}Response toResponse({Entity}Entity entity);
    {Entity}Entity toEntity({Entity}Request request);
}

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
```

## Codex 프롬프트 예시
```
# .codex/prompts/analyze-feature.md

You are a TDD expert for Java Spring project.

Context: Read AGENTS.md for full project context.

Task: Analyze the feature request and create a detailed TDD plan.

User Request: {feature_request}

Generate:
1. Feature Plan using feature-template.md
2. List all files to create/modify
3. Detailed TDD steps (test first, then implementation)
4. Database migration if needed

Output format: Markdown following feature-template.md

## PLAN 관리
사용자가 요구사항을 만들면 docs/tdd-plans/ 에 보관한다. 
보관은 주요 feature로 이름 정한다.

