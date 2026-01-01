package prac.demonote.domain.note;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import prac.demonote.domain.note.dto.NoteCursor;
import prac.demonote.domain.note.dto.NoteCreateRequest;
import prac.demonote.domain.note.dto.NoteResponse;
import prac.demonote.domain.note.dto.NoteUpdateRequest;
import prac.demonote.domain.note.dto.NotesPageResponse;
import prac.demonote.global.security.CustomUserDetails;
import prac.demonote.global.security.Role;

@ExtendWith(MockitoExtension.class)
class NoteControllerTest {

  @Mock
  private NoteService noteService;

  @InjectMocks
  private NoteController noteController;

  private CustomUserDetails mockUser;
  private UUID testUserId;
  private UUID testNoteId;

  @BeforeEach
  void setUp() {
    testUserId = UUID.randomUUID();
    testNoteId = UUID.randomUUID();
    mockUser = new CustomUserDetails(testUserId, "test@example.com", Role.ROLE_USER);
  }

  @Nested
  class CreateNote {

    @Test
    void 노트를_생성할_수_있다() {
      // given
      NoteCreateRequest request = new NoteCreateRequest("제목", "내용");
      NoteResponse response = new NoteResponse(
          testNoteId, "제목", "내용",
          LocalDateTime.now(), LocalDateTime.now()
      );

      when(noteService.createNote(testUserId, request)).thenReturn(response);

      // when
      ResponseEntity<NoteResponse> result = noteController.createNote(mockUser, request);

      // then
      assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
      assertThat(result.getBody()).isNotNull();
      assertThat(result.getBody().title()).isEqualTo("제목");
    }
  }

  @Nested
  class GetNote {

    @Test
    void 노트를_조회할_수_있다() {
      // given
      NoteResponse response = new NoteResponse(
          testNoteId, "제목", "내용",
          LocalDateTime.now(), LocalDateTime.now()
      );

      when(noteService.getNote(testUserId, testNoteId)).thenReturn(response);

      // when
      ResponseEntity<NoteResponse> result = noteController.getNote(mockUser, testNoteId);

      // then
      assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(result.getBody()).isNotNull();
      assertThat(result.getBody().title()).isEqualTo("제목");
    }

    @Test
    void 존재하지_않는_노트_조회시_예외발생() {
      // given
      when(noteService.getNote(testUserId, testNoteId))
          .thenThrow(new NoteNotFoundException("Note not found"));

      // when & then
      assertThatThrownBy(() -> noteController.getNote(mockUser, testNoteId))
          .isInstanceOf(NoteNotFoundException.class);
    }
  }

  @Nested
  class UpdateNote {

    @Test
    void 노트를_수정할_수_있다() {
      // given
      NoteUpdateRequest request = new NoteUpdateRequest("수정된 제목", "수정된 내용");
      NoteResponse response = new NoteResponse(
          testNoteId, "수정된 제목", "수정된 내용",
          LocalDateTime.now(), LocalDateTime.now()
      );

      when(noteService.updateNote(testUserId, testNoteId, request)).thenReturn(response);

      // when
      ResponseEntity<NoteResponse> result = noteController.updateNote(mockUser, testNoteId, request);

      // then
      assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(result.getBody().title()).isEqualTo("수정된 제목");
    }
  }

  @Nested
  class DeleteNote {

    @Test
    void 노트를_삭제할_수_있다() {
      // when
      ResponseEntity<Void> result = noteController.deleteNote(mockUser, testNoteId);

      // then
      assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      verify(noteService).deleteNote(testUserId, testNoteId);
    }
  }

  @Nested
  class GetNotesPage {

    @Test
    void 노트_목록을_keyset_페이지네이션으로_조회할_수_있다() {
      // given
      NotesPageResponse response = new NotesPageResponse(
          List.of(
              new NoteResponse(testNoteId, "제목1", "내용1", LocalDateTime.now(), LocalDateTime.now())
          ),
          new NoteCursor(LocalDateTime.now(), testNoteId),
          10,
          15,
          true
      );

      when(noteService.getNotesPage(eq(testUserId), any(), eq(10))).thenReturn(response);

      // when
      ResponseEntity<NotesPageResponse> result = noteController.getNotesPage(mockUser, null, null, 10);

      // then
      assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(result.getBody()).isNotNull();
      assertThat(result.getBody().hasNext()).isTrue();
      assertThat(result.getBody().totalElements()).isEqualTo(15);
    }

    @Test
    void 커서를_사용해_다음_페이지를_조회할_수_있다() {
      // given
      LocalDateTime cursorTime = LocalDateTime.now();
      UUID cursorId = UUID.randomUUID();
      NotesPageResponse response = new NotesPageResponse(
          List.of(
              new NoteResponse(testNoteId, "제목2", "내용2", LocalDateTime.now(), LocalDateTime.now())
          ),
          null,
          10,
          15,
          false
      );

      when(noteService.getNotesPage(eq(testUserId), any(NoteCursor.class), eq(10)))
          .thenReturn(response);

      // when
      ResponseEntity<NotesPageResponse> result = noteController.getNotesPage(
          mockUser, cursorTime, cursorId, 10
      );

      // then
      assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(result.getBody().hasNext()).isFalse();
    }
  }

  @Nested
  class GetReplacementNote {

    @Test
    void 보충_노트를_조회할_수_있다() {
      // given
      LocalDateTime cursorTime = LocalDateTime.now();
      UUID cursorId = UUID.randomUUID();
      NoteResponse response = new NoteResponse(
          testNoteId, "보충 노트", "내용",
          LocalDateTime.now(), LocalDateTime.now()
      );

      when(noteService.getReplacementNote(eq(testUserId), any(NoteCursor.class)))
          .thenReturn(Optional.of(response));

      // when
      ResponseEntity<NoteResponse> result = noteController.getReplacementNote(
          mockUser, cursorTime, cursorId
      );

      // then
      assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(result.getBody()).isNotNull();
      assertThat(result.getBody().title()).isEqualTo("보충 노트");
    }

    @Test
    void 보충_노트가_없으면_204_반환() {
      // given
      LocalDateTime cursorTime = LocalDateTime.now();
      UUID cursorId = UUID.randomUUID();

      when(noteService.getReplacementNote(eq(testUserId), any(NoteCursor.class)))
          .thenReturn(Optional.empty());

      // when
      ResponseEntity<NoteResponse> result = noteController.getReplacementNote(
          mockUser, cursorTime, cursorId
      );

      // then
      assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      assertThat(result.getBody()).isNull();
    }
  }

}
