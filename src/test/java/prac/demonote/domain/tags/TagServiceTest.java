package prac.demonote.domain.tags;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import prac.demonote.domain.note.NoteNotFoundException;
import prac.demonote.domain.note.NoteRepository;
import prac.demonote.domain.note.model.Note;
import prac.demonote.domain.tags.dto.NoteTagsResponse;
import prac.demonote.domain.tags.dto.TagCreateRequest;
import prac.demonote.domain.tags.dto.TagResponse;
import prac.demonote.domain.tags.dto.TagsResponse;
import prac.demonote.domain.tags.exception.TagAlreadyExistsException;
import prac.demonote.domain.tags.exception.TagNotFoundException;
import prac.demonote.domain.tags.exception.TagNotLinkedToNoteException;
import prac.demonote.domain.tags.model.NoteTag;
import prac.demonote.domain.tags.model.Tag;
import prac.demonote.domain.users.User;
import prac.demonote.global.security.oauth2.OAuth2Provider;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

  @Mock
  private TagRepository tagRepository;

  @Mock
  private NoteTagRepository noteTagRepository;

  @Mock
  private NoteRepository noteRepository;

  @InjectMocks
  private TagServiceImpl tagService;

  private User testUser;
  private UUID testUserId;
  private Note testNote;
  private UUID testNoteId;
  private Tag testTag;
  private UUID testTagId;

  @BeforeEach
  void setUp() {
    testUserId = UUID.randomUUID();
    testUser = createTestUser(testUserId);
    testNoteId = UUID.randomUUID();
    testNote = createTestNote(testNoteId, testUser);
    testTagId = UUID.randomUUID();
    testTag = createTestTag(testTagId, "java", testUser);
  }

  @Nested
  @DisplayName("노트에 태그 추가")
  class AddTagToNote {

    @Test
    void 노트에_새_태그를_추가할_수_있다() {
      // given
      TagCreateRequest request = new TagCreateRequest("java");
      when(tagRepository.existsByNameAndOwnerId("java", testUserId))
          .thenReturn(false);
      when(noteRepository.findByIdAndOwnerId(testNoteId, testUserId))
          .thenReturn(Optional.of(testNote));
      when(tagRepository.save(any(Tag.class)))
          .thenReturn(testTag);
      when(noteTagRepository.save(any(NoteTag.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      // when
      NoteTagsResponse response = tagService.addTagToNote(testUserId, testNoteId, request);

      // then
      assertThat(response.noteId()).isEqualTo(testNoteId);
      assertThat(response.tags().id()).isEqualTo(testTagId);
      assertThat(response.tags().name()).isEqualTo("java");
      verify(tagRepository).save(any(Tag.class));
      verify(noteTagRepository).save(any(NoteTag.class));
    }

    @Test
    void 태그_이름_중복이면_예외발생한다() {
      // given
      TagCreateRequest request = new TagCreateRequest("java");
      when(tagRepository.existsByNameAndOwnerId("java", testUserId))
          .thenReturn(true);

      // when & then
      assertThatThrownBy(() -> tagService.addTagToNote(testUserId, testNoteId, request))
          .isInstanceOf(TagAlreadyExistsException.class);
      verify(noteRepository, never()).findByIdAndOwnerId(any(), any());
      verify(tagRepository, never()).save(any(Tag.class));
      verify(noteTagRepository, never()).save(any(NoteTag.class));
    }

    @Test
    void 태그이름을_정규화하여_중복검사한다() {
      // given
      TagCreateRequest request = new TagCreateRequest("JAVA ");
      when(tagRepository.existsByNameAndOwnerId("java", testUserId))
          .thenReturn(true);

      // when & then
      assertThatThrownBy(() -> tagService.addTagToNote(testUserId, testNoteId, request))
          .isInstanceOf(TagAlreadyExistsException.class);
      verify(tagRepository).existsByNameAndOwnerId("java", testUserId);
    }

    @Test
    void 존재하지_않는_노트에_태그추가시_예외발생() {
      // given
      TagCreateRequest request = new TagCreateRequest("java");
      when(tagRepository.existsByNameAndOwnerId("java", testUserId))
          .thenReturn(false);
      when(noteRepository.findByIdAndOwnerId(testNoteId, testUserId))
          .thenReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> tagService.addTagToNote(testUserId, testNoteId, request))
          .isInstanceOf(NoteNotFoundException.class);
      verify(tagRepository, never()).save(any(Tag.class));
      verify(noteTagRepository, never()).save(any(NoteTag.class));
    }
  }

  @Nested
  @DisplayName("노트에서 태그 제거")
  class RemoveTagFromNote {

    @Test
    void 노트에서_태그를_제거할_수_있다() {
      // given
      when(noteRepository.existsByIdAndOwnerId(testNoteId, testUserId)).thenReturn(true);
      when(noteTagRepository.deleteByNoteIdAndTagId(testNoteId, testTagId)).thenReturn(1);

      // when
      tagService.removeTagFromNote(testUserId, testNoteId, testTagId);

      // then
      verify(noteTagRepository).deleteByNoteIdAndTagId(testNoteId, testTagId);
    }

    @Test
    void 존재하지_않는_노트에서_태그_제거시_예외발생() {
      // given
      when(noteRepository.existsByIdAndOwnerId(testNoteId, testUserId)).thenReturn(false);

      // when & then
      assertThatThrownBy(() -> tagService.removeTagFromNote(testUserId, testNoteId, testTagId))
          .isInstanceOf(NoteNotFoundException.class);
      verify(noteTagRepository, never()).deleteByNoteIdAndTagId(any(), any());
    }

    @Test
    void 노트에_연결되지_않은_태그_제거시_예외발생() {
      // given
      when(noteRepository.existsByIdAndOwnerId(testNoteId, testUserId)).thenReturn(true);
      when(noteTagRepository.deleteByNoteIdAndTagId(testNoteId, testTagId)).thenReturn(0);

      // when & then
      assertThatThrownBy(() -> tagService.removeTagFromNote(testUserId, testNoteId, testTagId))
          .isInstanceOf(TagNotLinkedToNoteException.class);
    }
  }

  @Nested
  @DisplayName("사용자의 모든 태그 조회")
  class GetAllTags {

    @Test
    void 사용자의_모든_태그를_조회할_수_있다() {
      // given
      Tag tag2 = createTestTag(UUID.randomUUID(), "spring", testUser);
      when(tagRepository.findByOwnerId(testUserId))
          .thenReturn(List.of(testTag, tag2));

      // when
      TagsResponse response = tagService.getAllTags(testUserId);

      // then
      assertThat(response.tags()).hasSize(2);
      assertThat(response.tags()).extracting(TagResponse::name)
          .containsExactlyInAnyOrder("java", "spring");
    }

    @Test
    void 태그가_없으면_빈_리스트를_반환한다() {
      // given
      when(tagRepository.findByOwnerId(testUserId)).thenReturn(List.of());

      // when
      TagsResponse response = tagService.getAllTags(testUserId);

      // then
      assertThat(response.tags()).isEmpty();
    }
  }

  @Nested
  @DisplayName("태그 삭제")
  class DeleteTag {

    @Test
    void 태그를_삭제하면_모든_노트에서_제거된다() {
      // given
      when(tagRepository.existsByIdAndOwnerId(testTagId, testUserId)).thenReturn(true);

      // when
      tagService.deleteTag(testUserId, testTagId);

      // then
      verify(noteTagRepository).deleteByTagId(testTagId);
      verify(tagRepository).deleteById(testTagId);
    }

    @Test
    void 존재하지_않는_태그_삭제시_예외발생() {
      // given
      when(tagRepository.existsByIdAndOwnerId(testTagId, testUserId)).thenReturn(false);

      // when & then
      assertThatThrownBy(() -> tagService.deleteTag(testUserId, testTagId))
          .isInstanceOf(TagNotFoundException.class);
    }
  }

  @Nested
  @DisplayName("노트의 태그 조회")
  class GetTagsForNote {

    @Test
    void 노트에_연결된_태그들을_조회할_수_있다() {
      // given
      String tag2Name = "spring";
      Tag tag2 = createTestTag(UUID.randomUUID(), tag2Name, testUser);
      when(noteTagRepository.findByNoteId(testNoteId))
          .thenReturn(List.of(
              new NoteTag(testNote, testTag),
              new NoteTag(testNote, tag2)
          ));

      // when
      List<TagResponse> tags = tagService.getTagsForNote(testNoteId);

      // then
      assertThat(tags).hasSize(2);
      assertThat(tags).extracting(TagResponse::name)
          .containsExactlyInAnyOrder("java", tag2Name);
    }
  }

  private User createTestUser(UUID userId) {
    User user = new User("test@example.com", OAuth2Provider.GOOGLE, "provider-id");
    setId(user, userId);
    return user;
  }

  private Note createTestNote(UUID noteId, User owner) {
    Note note = new Note("Test Title", "Test Content", owner);
    setId(note, noteId);
    return note;
  }

  private Tag createTestTag(UUID tagId, String name, User owner) {
    Tag tag = new Tag(name, owner);
    setId(tag, tagId);
    return tag;
  }

  private void setId(Object entity, UUID id) {
    try {
      var field = entity.getClass().getSuperclass().getSuperclass().getSuperclass()
          .getDeclaredField("id");
      field.setAccessible(true);
      field.set(entity, id);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
