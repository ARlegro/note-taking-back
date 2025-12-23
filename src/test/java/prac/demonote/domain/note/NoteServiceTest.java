package prac.demonote.domain.note;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.commons.util.ReflectionUtils.HierarchyTraversalMode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import prac.demonote.domain.note.dto.NoteCreateRequestDTO;
import prac.demonote.domain.note.model.Note;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

  @Mock
  private NoteRepository noteRepository;

  @InjectMocks
  private NoteService noteService;

  private NoteCreateRequestDTO requestDTO;
  private UUID noteId;

  @BeforeEach
  void setUp() {
    requestDTO = new NoteCreateRequestDTO("테스트 제목", "테스트 내용");
    noteId = UUID.randomUUID();
  }

  @Test
  @DisplayName("노트 저장 성공 - 정상적으로 저장되고 UUID를 반환한다")
  void 노트를_저장하면_아이디를_반환한다() {
    // given
    Note savedNote = new Note(requestDTO.title(), requestDTO.content());
    ReflectionTestUtils.setField(savedNote, "id", noteId);
    given(noteRepository.save(any(Note.class))).willReturn(savedNote);

    // when
    UUID result = noteService.save(requestDTO);

    // then
    assertNotNull(result);
    assertEquals(noteId, result);
    verify(noteRepository, times(1)).save(any(Note.class));
  }

  @Test
  @DisplayName("노트 저장 실패 - title 누락")
  void 제목이_없으면_예외가_발생한다(){

    // given
    NoteCreateRequestDTO dto = new NoteCreateRequestDTO(null, requestDTO.content());

    // when
    Executable action = () -> noteService.save(dto);

    // then
    Assertions.assertThrows(RuntimeException.class, action);
  }


  @Test
  @DisplayName("내용이 null이어도 노트 생성이 가능하다")
  void 내용이_없어도_노트를_생성할_수_있다() {
    // given
    String title = "제목";
    NoteCreateRequestDTO dto = new NoteCreateRequestDTO(title, null);
    Note savedNote = new Note(title, null);

    given(noteRepository.save(any(Note.class))).willReturn(savedNote);

    // when
    UUID result = noteService.save(dto);

    // then
    assertNotNull(result);
    assertEquals(noteId, result);
  }

  @Test
  @DisplayName("내용이 null이어도 저장 시 엔티티에 그대로 반영된다")
  void 내용이_없어도_저장_요청이_엔티티에_반영된다() {
    // given
    String title = "제목";
    NoteCreateRequestDTO dto = new NoteCreateRequestDTO(title, null);
    Note savedNote = new Note(title, null);

    given(noteRepository.save(any(Note.class))).willReturn(savedNote);

    // when
    noteService.save(dto);

    // then
    ArgumentCaptor<Note> noteCaptor = ArgumentCaptor.forClass(Note.class);
    verify(noteRepository).save(noteCaptor.capture());
    Note capturedNote = noteCaptor.getValue();
    assertEquals(dto.title(), capturedNote.getTitle());
    assertNull(capturedNote.getContent());
  }
}
