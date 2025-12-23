# TDD Features Roadmap

이 문서는 기능명세v1.md의 기능들을 TDD 방식으로 개발하기 위한 로드맵입니다.

## 개발 원칙 (CRITICAL)

**⚠ 반드시 Test-Driven Development로 개발합니다.**

### TDD 필수 순서
1. 🟥 RED: 테스트 먼저 작성 (구현 없음)
2. 🟥 RED: 테스트 실행하여 실패 확인
3. 🟢 GREEN: 테스트 통과시키는 최소 구현
4. 🟢 GREEN: 테스트 실행하여 통과 확인
5. 🟦 REFACTOR: 코드 개선
6. 🟢 GREEN: 테스트 재실행하여 통과 확인

### 레이어별 개발 순서
각 기능마다 아래 순서로 개발:
1. Repository Layer (`@DataJpaTest`)
2. Service Layer (`@ExtendWith(MockitoExtension.class)`)
3. Controller Layer (`@WebMvcTest`)

---

## Phase 1: Core Features (MVP)

### 1.1 User - 기본 사용자 관리

#### Feature 1-1-1: 사용자 엔티티 및 저장
**우선순위**: P0 (최우선)

**Repository Layer**
- [ ] Test: User 엔티티 저장 테스트
  - `givenUserEntity_whenSave_thenReturnsSavedUser()`
  - `givenUserId_whenFindById_thenReturnsUser()`
  - `givenEmail_whenFindByEmail_thenReturnsUser()`
- [ ] Impl: User 엔티티 생성
  - UUID id, email, provider, providerId, createdAt, updatedAt
- [ ] Impl: UserRepository 인터페이스

**Service Layer**
- [ ] Test: User 조회/저장 서비스 테스트
  - `givenUserId_whenGetUser_thenReturnsUserResponse()`
  - `givenNewUserRequest_whenCreateUser_thenReturnsCreatedUser()`
  - `givenInvalidUserId_whenGetUser_thenThrowsNotFoundException()`
- [ ] Impl: UserService 구현
- [ ] Impl: UserMapper (MapStruct)
- [ ] Impl: UserRequest/UserResponse DTO

**Controller Layer**
- [ ] Test: User API 엔드포인트 테스트
  - `givenValidUserId_whenGetUser_thenReturns200()`
  - `givenInvalidUserId_whenGetUser_thenReturns404()`
- [ ] Impl: UserController

[//]: # (#### Feature 1-1-2: OAuth 2.0 로그인 &#40;Google, GitHub, Kakao&#41;)

[//]: # (**우선순위**: P0)

[//]: # ()
[//]: # (- [ ] Spring Security OAuth2 Client 설정)

[//]: # (- [ ] OAuth2 Provider 설정 &#40;application.yaml&#41;)

[//]: # (- [ ] OAuth2SuccessHandler 구현)

[//]: # (- [ ] JWT 토큰 생성/검증 로직)

#### Feature 1-1-3: 세션 관리 (JWT)
**우선순위**: P0

- [ ] JwtTokenProvider 구현
- [ ] JwtAuthenticationFilter 구현
- [ ] 토큰 갱신 로직

#### Feature 1-1-4: 로그아웃
**우선순위**: P1

- [ ] 단일 디바이스 로그아웃
- [ ] 전체 디바이스 로그아웃

---

### 1.2 User Settings - 사용자 설정

#### Feature 1-2-1: 사용자 설정 관리
**우선순위**: P2

**Repository Layer**
- [ ] Test: UserSettings 엔티티 저장/조회 테스트
- [ ] Impl: UserSettings 엔티티
  - theme (LIGHT/DARK)
  - defaultVisibility (PRIVATE/PUBLIC)
  - trashRetentionDays (기본 30일)
  - autoSaveEnabled (기본 true)
- [ ] Impl: UserSettingsRepository

**Service Layer**d
- [ ] Test: 설정 조회/수정 서비스 테스트
- [ ] Impl: UserSettingsService
- [ ] Impl: UserSettingsMapper
- [ ] Impl: UserSettingsRequest/Response DTO

**Controller Layer**
- [ ] Test: 설정 API 테스트
- [ ] Impl: UserSettingsController
  - GET /api/users/{userId}/settings
  - PUT /api/users/{userId}/settings

---

### 1.3 Folder - 폴더 관리

#### Feature 1-3-1: 폴더 CRUD
**우선순위**: P0

**Repository Layer**
- [ ] Test: Folder 엔티티 저장/조회 테스트
  - `givenFolder_whenSave_thenReturnsSavedFolder()`
  - `givenUserId_whenFindByUserId_thenReturnsFolders()`
  - `givenParentId_whenFindByParentId_thenReturnsChildFolders()`
- [ ] Impl: Folder 엔티티
  - UUID id, userId, parentId, name, path, deletedAt
- [ ] Impl: FolderRepository

**Service Layer**
- [ ] Test: 폴더 생성 테스트
  - `givenValidFolderRequest_whenCreateFolder_thenReturnsCreatedFolder()`
  - `givenMaxDepthExceeded_whenCreateFolder_thenThrowsException()`
  - `givenDuplicateName_whenCreateFolder_thenThrowsException()`
- [ ] Test: 폴더 이동 테스트
  - `givenValidTarget_whenMoveFolder_thenUpdatesPathAndChildren()`
  - `givenDepthExceeds_whenMoveFolder_thenThrowsException()`
- [ ] Test: 폴더 삭제 테스트 (Soft Delete)
  - `givenFolderId_whenDeleteFolder_thenSetsDeletedAt()`
- [ ] Impl: FolderService
- [ ] Impl: FolderMapper
- [ ] Impl: FolderRequest/Response DTO

**Controller Layer**
- [ ] Test: Folder API 테스트
- [ ] Impl: FolderController
  - POST /api/folders (폴더 생성)
  - GET /api/folders/{id} (폴더 조회)
  - GET /api/folders?userId={userId} (사용자 폴더 목록)
  - PUT /api/folders/{id} (폴더 수정)
  - DELETE /api/folders/{id} (폴더 삭제)
  - PUT /api/folders/{id}/move (폴더 이동)

**제약사항**
- 폴더 깊이 최대 5 (임시, schema.sql에는 10으로 명시)
- 같은 depth의 중복 이름 불허
- 폴더명 길이 제한 (100자)

---

### 1.4 Note - 노트 관리

#### Feature 1-4-1: 노트 CRUD
**우선순위**: P0

**Repository Layer**
- [ ] Test: Note 엔티티 저장/조회 테스트
  - `givenNote_whenSave_thenReturnsSavedNote()`
  - `givenNoteId_whenFindById_thenReturnsNote()`
  - `givenFolderId_whenFindByFolderId_thenReturnsNotes()`
  - `givenOwnerId_whenFindByOwnerId_thenReturnsNotes()`
- [ ] Impl: Note 엔티티
  - UUID id, ownerId, folderId, title, contentMarkdown, version, deletedAt
- [ ] Impl: NoteRepository

**Service Layer**
- [ ] Test: 노트 생성 테스트
  - `givenValidNoteRequest_whenCreateNote_thenReturnsCreatedNote()`
  - `givenNoFolder_whenCreateNote_thenCreatesInRootFolder()`
- [ ] Test: 노트 수정 테스트
  - `givenValidUpdate_whenUpdateNote_thenReturnsUpdatedNote()`
  - `givenConcurrentUpdate_whenUpdateNote_thenHandlesConflict()` (version 관리)
- [ ] Test: 노트 이동 테스트
  - `givenTargetFolder_whenMoveNote_thenUpdatesFolderId()`
- [ ] Test: 노트 삭제 테스트 (Soft Delete)
  - `givenNoteId_whenDeleteNote_thenSetsDeletedAt()`
- [ ] Impl: NoteService
- [ ] Impl: NoteMapper
- [ ] Impl: NoteRequest/Response DTO

**Controller Layer**
- [ ] Test: Note API 테스트
- [ ] Impl: NoteController
  - POST /api/notes (노트 생성)
  - GET /api/notes/{id} (노트 조회)
  - GET /api/notes?folderId={folderId} (폴더별 노트 목록)
  - PUT /api/notes/{id} (노트 수정)
  - DELETE /api/notes/{id} (노트 삭제)
  - PUT /api/notes/{id}/move (노트 이동)

#### Feature 1-4-2: 노트 자동 저장 (Auto-Save)
**우선순위**: P1

**요구사항**
- Debounce: 1.5초 (키 입력 멈춘 후)
- Throttle: 5초 (연속 입력 중에도 강제 저장)
- 즉시 저장: 폴더 이동, Visibility 변경

**구현**
- [ ] Frontend에서 debounce/throttle 처리 (Backend는 단순 UPDATE API)
- [ ] PUT /api/notes/{id} 엔드포인트 최적화
- [ ] Version 충돌 처리 로직

#### Feature 1-4-3: 노트 미리보기
**우선순위**: P1

**Service Layer**
- [ ] Test: 노트 미리보기 조회 테스트
  - `givenFolderId_whenGetNotePreviews_thenReturnsPreviewList()`
  - `givenUserId_whenGetRecentNotes_thenReturnsRecentPreviews()`
- [ ] Impl: 미리보기 전용 DTO
  - title, tags, content (N자 제한, 마크다운 제거), 최근 수정 시간

**Controller Layer**
- [ ] GET /api/notes/previews?folderId={folderId}
- [ ] GET /api/notes/previews/recent?userId={userId}

#### Feature 1-4-4: 커서 위치 기억 (옵션)
**우선순위**: P3

- [ ] NoteCursor 엔티티 (noteId, userId, cursorPosition)
- [ ] Cursor 저장/조회 API

---

### 1.5 Tag - 태그 관리

#### Feature 1-5-1: 태그 CRUD
**우선순위**: P1

**Repository Layer**
- [ ] Test: Tag 엔티티 저장/조회 테스트
- [ ] Test: NoteTag 관계 테스트 (N:M)
- [ ] Impl: Tag 엔티티
  - UUID id, ownerId, name
- [ ] Impl: NoteTag 엔티티 (연결 테이블)
- [ ] Impl: TagRepository

**Service Layer**
- [ ] Test: 태그 생성 테스트
  - `givenValidTagName_whenCreateTag_thenReturnsCreatedTag()`
  - `givenInvalidTagName_whenCreateTag_thenThrowsException()` (공백, 특수문자 검증)
  - `givenDuplicateTag_whenCreateTag_thenReturnsExisting()` (대소문자 무시)
- [ ] Test: 노트에 태그 추가/제거 테스트
- [ ] Test: 태그 검색 테스트
  - `givenTagName_whenSearchByTag_thenReturnsNotes()`
- [ ] Impl: TagService
- [ ] Impl: TagMapper

**Controller Layer**
- [ ] POST /api/tags (태그 생성)
- [ ] GET /api/tags?userId={userId} (사용자 태그 목록)
- [ ] POST /api/notes/{noteId}/tags (노트에 태그 추가)
- [ ] DELETE /api/notes/{noteId}/tags/{tagId} (노트에서 태그 제거)
- [ ] DELETE /api/tags/{id} (태그 삭제 - Hard Delete)

**제약사항**
- 공백 불허
- 대소문자 구분 없음 (정규화)
- 특수문자 불허 (협의 필요)

---

### 1.6 Trash - 휴지통

#### Feature 1-6-1: 휴지통 조회 및 복구
**우선순위**: P1

**Service Layer**
- [ ] Test: 휴지통 조회 테스트
  - `givenUserId_whenGetTrashItems_thenReturnsDeletedItems()`
- [ ] Test: 복구 테스트
  - `givenDeletedNote_whenRestore_thenSetsDeletedAtToNull()`
  - `givenDeletedFolder_whenRestore_thenRestoresWithChildren()`
  - `givenMissingParent_whenRestore_thenRestoresToRoot()`
- [ ] Impl: TrashService

**Controller Layer**
- [ ] GET /api/trash?userId={userId} (휴지통 목록)
- [ ] POST /api/trash/{id}/restore (복구)
- [ ] DELETE /api/trash/{id} (영구 삭제)
- [ ] DELETE /api/trash (휴지통 비우기)

#### Feature 1-6-2: 자동 Hard Delete (Batch Job)
**우선순위**: P2

- [ ] Spring Batch 또는 Scheduled Task 구현
- [ ] 설정된 보관 기간(기본 30일) 경과 시 자동 삭제
- [ ] Cascade 삭제 (폴더 하위 노트/자산 포함)

---

### 1.7 Search - 검색

#### Feature 1-7-1: 기본 검색 (제목, 본문, 태그)
**우선순위**: P1

**Service Layer**
- [ ] Test: 제목 검색 테스트
- [ ] Test: 본문 검색 테스트
- [ ] Test: 태그 검색 테스트
- [ ] Test: 복합 검색 테스트
- [ ] Test: 휴지통 포함 옵션 테스트
- [ ] Impl: SearchService

**Controller Layer**
- [ ] GET /api/search?q={query}&includeTrash={true/false}

**구현 전략**
- Phase 1: LIKE 검색 (PostgreSQL)
- Phase 2: Full-Text Search (PostgreSQL FTS)
- Phase 3: Vector Search (향후)

---

## Phase 2: Collaboration Features

### 2.1 Share - 공유

#### Feature 2-1-1: 노트/폴더 공유 (특정 사용자)
**우선순위**: P1

**Repository Layer**
- [ ] Test: NotePermission/FolderPermission 엔티티 테스트
- [ ] Impl: NotePermission 엔티티
- [ ] Impl: FolderPermission 엔티티
- [ ] Impl: PermissionRepository

**Service Layer**
- [ ] Test: 권한 부여 테스트
  - `givenNoteAndUser_whenGrantPermission_thenUserCanRead()`
  - `givenFolder_whenGrantPermission_thenInheritsToChildren()`
- [ ] Test: 권한 제거 테스트
- [ ] Impl: PermissionService

**Controller Layer**
- [ ] POST /api/notes/{id}/permissions (권한 부여)
- [ ] DELETE /api/notes/{id}/permissions/{userId} (권한 제거)
- [ ] GET /api/notes/{id}/permissions (권한 목록)

#### Feature 2-1-2: 공유 링크
**우선순위**: P1

**Repository Layer**
- [ ] Test: ShareLink 엔티티 테스트
- [ ] Impl: ShareLink 엔티티
  - UUID id, targetType (NOTE/FOLDER), targetId, token, expiredAt
- [ ] Impl: ShareLinkRepository

**Service Layer**
- [ ] Test: 공유 링크 생성 테스트
  - `givenNote_whenCreateShareLink_thenReturnsToken()`
  - `givenExpiredLink_whenAccess_thenThrowsException()`
- [ ] Test: 공유 링크 비활성화 테스트
- [ ] Impl: ShareLinkService

**Controller Layer**
- [ ] POST /api/shares (공유 링크 생성)
- [ ] GET /api/shares/{token} (공유 링크로 접근)
- [ ] DELETE /api/shares/{id} (공유 링크 비활성화)

---

### 2.2 Attachment - 파일 첨부

#### Feature 2-2-1: 파일 업로드 (이미지, PDF)
**우선순위**: P2

**Repository Layer**
- [ ] Test: Attachment 엔티티 테스트
- [ ] Test: NoteAttachmentRef 엔티티 테스트
- [ ] Impl: Attachment 엔티티
  - UUID id, type, storageKey, fileName
- [ ] Impl: NoteAttachmentRef 엔티티
- [ ] Impl: AttachmentRepository

**Service Layer**
- [ ] Test: 파일 업로드 테스트
  - `givenImageFile_whenUpload_thenReturnsAttachmentId()`
  - `givenInvalidFileType_whenUpload_thenThrowsException()`
  - `givenFileSizeExceeds_whenUpload_thenThrowsException()`
- [ ] Impl: AttachmentService
- [ ] Impl: StorageStrategy 인터페이스 (전략 패턴)
- [ ] Impl: MinIOStorageStrategy

**Controller Layer**
- [ ] POST /api/attachments (파일 업로드)
- [ ] GET /api/attachments/{id} (파일 다운로드)
- [ ] DELETE /api/attachments/{id} (파일 삭제)

**제약사항**
- 허용 타입: png, jpg, pdf (Phase 1)
- 최대 크기: 10MB per file, 50MB per request

#### Feature 2-2-2: 이미지 리사이즈 (옵션)
**우선순위**: P3

- [ ] Python 라이브러리 연동 또는 Java ImageIO 사용
- [ ] 썸네일 자동 생성

#### Feature 2-2-3: PDF 프리뷰
**우선순위**: P3

- [ ] PDF 첫 페이지 썸네일 생성

---

## Phase 3: Advanced Features

### 3.1 Sync - 멀티 디바이스 동기화

#### Feature 3-1-1: 충돌 관리
**우선순위**: P2

- [ ] Version 기반 충돌 감지
- [ ] Last-Write-Wins 또는 Manual Merge 전략
- [ ] WebSocket 실시간 동기화 (옵션)

---

### 3.2 Export - PDF 내보내기

#### Feature 3-2-1: 노트 → PDF 변환
**우선순위**: P2

- [ ] Markdown → HTML 변환
- [ ] HTML → PDF 변환 (Flying Saucer, iText 등)

---

### 3.3 Workspace - 워크스페이스 (MVP 이후)

#### Feature 3-3-1: 워크스페이스 생성 및 관리
**우선순위**: P3

- [ ] Workspace 엔티티
- [ ] 멤버 초대 (이메일)
- [ ] 역할 관리 (Owner, Admin, Member)
- [ ] 소유권 이전
- [ ] 워크스페이스 삭제

---

## TDD 체크리스트 (각 기능마다 확인)

### Repository Layer
- [ ] Entity 정의 (JPA annotations)
- [ ] Repository 인터페이스 (Spring Data JPA)
- [ ] `@DataJpaTest` 작성
- [ ] 테스트 실패 확인 (RED)
- [ ] 구현 (GREEN)
- [ ] 리팩토링 (REFACTOR)

### Service Layer
- [ ] Service 인터페이스/클래스
- [ ] DTO 정의 (Request/Response)
- [ ] Mapper 인터페이스 (MapStruct)
- [ ] `@ExtendWith(MockitoExtension.class)` 테스트 작성
- [ ] 테스트 실패 확인 (RED)
- [ ] 구현 (GREEN)
- [ ] 리팩토링 (REFACTOR)

### Controller Layer
- [ ] Controller 클래스
- [ ] `@WebMvcTest` 테스트 작성
- [ ] 테스트 실패 확인 (RED)
- [ ] 구현 (GREEN)
- [ ] 리팩토링 (REFACTOR)
- [ ] Swagger 문서 확인 (http://localhost:8080/swagger)

---

## 개발 시 주의사항

1. **절대 테스트 없이 구현하지 않는다**
2. **반드시 테스트 실패를 먼저 확인한다**
3. **최소한의 코드로 테스트를 통과시킨다**
4. **리팩토링 후 항상 테스트를 재실행한다**
5. **given_when_then 네이밍 규칙을 따른다**
6. **각 레이어별로 순차적으로 개발한다** (Repository → Service → Controller)
