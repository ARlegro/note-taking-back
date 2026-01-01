package prac.demonote.domain.note;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.IntFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import prac.demonote.domain.note.dto.NoteCursor;
import prac.demonote.domain.note.dto.NoteCreateRequest;
import prac.demonote.domain.note.dto.NoteResponse;
import prac.demonote.domain.note.dto.NoteUpdateRequest;
import prac.demonote.domain.note.dto.NotesPageResponse;
import prac.demonote.domain.note.model.Note;
import prac.demonote.domain.users.User;
import prac.demonote.domain.users.UserRepository;
import prac.demonote.global.security.oauth2.OAuth2Provider;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

  @Mock
  private NoteRepository noteRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private NoteServiceImpl noteService;

  private User testUser;
  private UUID testUserId;

  @BeforeEach
  void setUp() {
    testUserId = UUID.randomUUID();
    testUser = new User("test@example.com", OAuth2Provider.GOOGLE, "provider-id");
  }

  @Nested
  class CreateNote {

    @Test
    void 노트를_생성할_수_있다() {
      // given
      NoteCreateRequest request = new NoteCreateRequest("제목", "내용");
      Note savedNote = createTestNote("제목", "내용");

      when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
      when(noteRepository.save(any(Note.class))).thenReturn(savedNote);

      // when
      NoteResponse response = noteService.createNote(testUserId, request);

      // then
      assertThat(response.title()).isEqualTo("제목");
      assertThat(response.content()).isEqualTo("내용");
      verify(noteRepository).save(any(Note.class));
    }

    @Test
    void 존재하지_않는_사용자로_노트생성시_예외발생() {
      // given
      NoteCreateRequest request = new NoteCreateRequest("제목", "내용");
      when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> noteService.createNote(testUserId, request))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("User not found");
    }
  }

  @Nested
  class GetNote {

    @Test
    void 노트를_조회할_수_있다() {
      // given
      Note note = createTestNote("제목", "내용");
      when(noteRepository.findByIdAndOwnerId(note.getId(), testUserId))
          .thenReturn(Optional.of(note));

      // when
      NoteResponse response = noteService.getNote(testUserId, note.getId());

      // then
      assertThat(response.title()).isEqualTo("제목");
      assertThat(response.content()).isEqualTo("내용");
    }

    @Test
    void 존재하지_않는_노트_조회시_예외발생() {
      // given
      UUID noteId = UUID.randomUUID();
      when(noteRepository.findByIdAndOwnerId(noteId, testUserId))
          .thenReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> noteService.getNote(testUserId, noteId))
          .isInstanceOf(NoteNotFoundException.class);
    }
  }

  @Nested
  class UpdateNote {

    @Test
    void 노트를_수정할_수_있다() {
      // given
      Note note = createTestNote("원래 제목", "원래 내용");
      NoteUpdateRequest request = new NoteUpdateRequest("수정된 제목", "수정된 내용");

      when(noteRepository.findByIdAndOwnerId(note.getId(), testUserId))
          .thenReturn(Optional.of(note));

      // when
      NoteResponse response = noteService.updateNote(testUserId, note.getId(), request);

      // then
      assertThat(response.title()).isEqualTo("수정된 제목");
      assertThat(response.content()).isEqualTo("수정된 내용");
    }
  }

  @Nested
  class DeleteNote {

    @Test
    void 단일_노트를_삭제할_수_있다() {
      // given
      UUID noteId = UUID.randomUUID();
      when(noteRepository.existsByIdAndOwnerId(noteId, testUserId)).thenReturn(true);

      // when
      noteService.deleteNote(testUserId, noteId);

      // then
      verify(noteRepository).deleteById(noteId);
    }

    @Test
    void 존재하지_않는_노트_삭제시_예외발생() {
      // given
      UUID noteId = UUID.randomUUID();
      when(noteRepository.existsByIdAndOwnerId(noteId, testUserId)).thenReturn(false);

      // when & then
      assertThatThrownBy(() -> noteService.deleteNote(testUserId, noteId))
          .isInstanceOf(NoteNotFoundException.class);
    }
  }

  @Nested
  class GetNotesPage {

    @Test
    void 첫_페이지를_커서_없이_조회할_수_있다() {
      // given
      List<Note> notes = List.of(
          createTestNote("제목1", "내용1"),
          createTestNote("제목2", "내용2")
      );
      Window<Note> window = Window.from(notes, (IntFunction<ScrollPosition>) i -> ScrollPosition.keyset(), true);

      when(noteRepository.findByOwnerId(eq(testUserId), any(ScrollPosition.class), any(Limit.class), any(Sort.class)))
          .thenReturn(window);
      when(noteRepository.countByOwnerId(testUserId)).thenReturn(15L);

      // when
      NotesPageResponse response = noteService.getNotesPage(testUserId, null, 2);

      // then
      assertThat(response.notes()).hasSize(2);
      assertThat(response.totalElements()).isEqualTo(15);
      assertThat(response.hasNext()).isTrue();
      assertThat(response.nextCursor()).isNotNull();
    }

    @Test
    void 커서를_사용해_다음_페이지를_조회할_수_있다() {
      // given
      NoteCursor cursor = new NoteCursor(LocalDateTime.now(), UUID.randomUUID());
      List<Note> notes = List.of(
          createTestNote("제목3", "내용3"),
          createTestNote("제목4", "내용4")
      );
      Window<Note> window = Window.from(notes, (IntFunction<ScrollPosition>) i -> ScrollPosition.keyset(), false);

      when(noteRepository.findByOwnerId(eq(testUserId), any(ScrollPosition.class), any(Limit.class), any(Sort.class)))
          .thenReturn(window);
      when(noteRepository.countByOwnerId(testUserId)).thenReturn(15L);

      // when
      NotesPageResponse response = noteService.getNotesPage(testUserId, cursor, 2);

      // then
      assertThat(response.notes()).hasSize(2);
      assertThat(response.hasNext()).isFalse();
      assertThat(response.nextCursor()).isNull();
    }
  }

  @Nested
  class GetReplacementNote {

    @Test
    void 커서_이후_보충_노트_1개를_반환한다() {
      // given
      NoteCursor cursor = new NoteCursor(LocalDateTime.now(), UUID.randomUUID());
      Note replacementNote = createTestNote("보충 노트", "보충 내용");

      Window<Note> window = Window.from(
          List.of(replacementNote),
          (IntFunction<ScrollPosition>) i -> ScrollPosition.keyset(),
          false
      );

      when(noteRepository.findByOwnerId(eq(testUserId), any(ScrollPosition.class), any(Limit.class), any(Sort.class)))
          .thenReturn(window);

      // when
      Optional<NoteResponse> result = noteService.getReplacementNote(testUserId, cursor);

      // then
      assertThat(result).isPresent();
      assertThat(result.get().title()).isEqualTo("보충 노트");
    }

    @Test
    void 커서_이후_노트가_없으면_빈_Optional_반환() {
      // given
      NoteCursor cursor = new NoteCursor(LocalDateTime.now(), UUID.randomUUID());

      Window<Note> emptyWindow = Window.from(
          List.of(),
          (IntFunction<ScrollPosition>) i -> ScrollPosition.keyset(),
          false
      );

      when(noteRepository.findByOwnerId(eq(testUserId), any(ScrollPosition.class), any(Limit.class), any(Sort.class)))
          .thenReturn(emptyWindow);

      // when
      Optional<NoteResponse> result = noteService.getReplacementNote(testUserId, cursor);

      // then
      assertThat(result).isEmpty();
    }
  }

  private Note createTestNote(String title, String content) {
    return new Note(title, content, testUser);
  }
}
