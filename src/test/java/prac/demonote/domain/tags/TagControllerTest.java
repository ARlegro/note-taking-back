package prac.demonote.domain.tags;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import prac.demonote.domain.note.NoteNotFoundException;
import prac.demonote.domain.tags.dto.NoteTagsResponse;
import prac.demonote.domain.tags.dto.TagCreateRequest;
import prac.demonote.domain.tags.dto.TagResponse;
import prac.demonote.domain.tags.dto.TagsResponse;
import prac.demonote.domain.tags.exception.TagNotFoundException;
import prac.demonote.global.security.CustomUserDetails;
import prac.demonote.global.security.Role;

@ExtendWith(MockitoExtension.class)
class TagControllerTest {

  @Mock
  private TagService tagService;

  @InjectMocks
  private TagController tagController;

  private CustomUserDetails mockUser;
  private UUID testUserId;
  private UUID testNoteId;
  private UUID testTagId;

  @BeforeEach
  void setUp() {
    testUserId = UUID.randomUUID();
    testNoteId = UUID.randomUUID();
    testTagId = UUID.randomUUID();
    mockUser = new CustomUserDetails(testUserId, "test@example.com", Role.ROLE_USER);
  }

  @Test
  void 노트에_태그를_추가하면_201_반환() {
    // given
    TagCreateRequest request = new TagCreateRequest("java");
    TagResponse tag = new TagResponse(testTagId, "java", LocalDateTime.now());
    NoteTagsResponse response = new NoteTagsResponse(testNoteId, tag);

    when(tagService.addTagToNote(testUserId, testNoteId, request)).thenReturn(response);

    // when
    ResponseEntity<NoteTagsResponse> result = tagController.addTagToNote(mockUser, testNoteId, request);

    // then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().noteId()).isEqualTo(testNoteId);
    assertThat(result.getBody().tags()).isNotNull();
  }

  @Test
  void 존재하지_않는_노트에_태그추가시_예외발생() {
    // given
    TagCreateRequest request = new TagCreateRequest("java");
    when(tagService.addTagToNote(testUserId, testNoteId, request))
        .thenThrow(new NoteNotFoundException("노트를 찾을 수 없습니다."));

    // when & then
    assertThatThrownBy(() -> tagController.addTagToNote(mockUser, testNoteId, request))
        .isInstanceOf(NoteNotFoundException.class);
  }

  @Test
  void 노트에서_태그를_제거하면_204_반환() {
    // when
    ResponseEntity<Void> result = tagController.removeTagFromNote(mockUser, testNoteId, testTagId);

    // then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    verify(tagService).removeTagFromNote(testUserId, testNoteId, testTagId);
  }

  @Test
  void 존재하지_않는_태그_제거시_예외발생() {
    // given
    doThrow(new TagNotFoundException("태그를 찾을 수 없습니다."))
        .when(tagService).removeTagFromNote(testUserId, testNoteId, testTagId);

    // when & then
    assertThatThrownBy(() -> tagController.removeTagFromNote(mockUser, testNoteId, testTagId))
        .isInstanceOf(TagNotFoundException.class);
  }

  @Test
  void 사용자의_모든_태그를_조회하면_200_반환() {
    // given
    List<TagResponse> tags = List.of(
        new TagResponse(testTagId, "java", LocalDateTime.now()),
        new TagResponse(UUID.randomUUID(), "spring", LocalDateTime.now())
    );
    TagsResponse response = new TagsResponse(tags);

    when(tagService.getAllTags(testUserId)).thenReturn(response);

    // when
    ResponseEntity<TagsResponse> result = tagController.getAllTags(mockUser);

    // then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).isNotNull();
    assertThat(result.getBody().tags()).hasSize(2);
  }

  @Test
  void 태그를_삭제하면_204_반환() {
    // when
    ResponseEntity<Void> result = tagController.deleteTag(mockUser, testTagId);

    // then
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    verify(tagService).deleteTag(testUserId, testTagId);
  }

  @Test
  void 존재하지_않는_태그_삭제시_예외발생() {
    // given
    doThrow(new TagNotFoundException("태그를 찾을 수 없습니다."))
        .when(tagService).deleteTag(testUserId, testTagId);

    // when & then
    assertThatThrownBy(() -> tagController.deleteTag(mockUser, testTagId))
        .isInstanceOf(TagNotFoundException.class);
  }
}
