package prac.demonote.domain.tags;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prac.demonote.domain.tags.dto.NoteTagsResponse;
import prac.demonote.domain.tags.dto.TagCreateRequest;
import prac.demonote.domain.tags.dto.TagsResponse;
import prac.demonote.global.security.CustomUserDetails;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TagController {

  private final TagService tagService;

  @PostMapping("/notes/{noteId}/tags")
  public ResponseEntity<NoteTagsResponse> addTagToNote(
      @AuthenticationPrincipal CustomUserDetails user,
      @PathVariable UUID noteId,
      @RequestBody TagCreateRequest request) {
    NoteTagsResponse response = tagService.addTagToNote(user.getUserId(), noteId, request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/tags")
  public ResponseEntity<TagsResponse> getAllTags(
      @AuthenticationPrincipal CustomUserDetails user) {
    TagsResponse response = tagService.getAllTags(user.getUserId());
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/notes/{noteId}/tags/{tagId}")
  public ResponseEntity<Void> removeTagFromNote(
      @AuthenticationPrincipal CustomUserDetails user,
      @PathVariable UUID noteId,
      @PathVariable UUID tagId) {
    tagService.removeTagFromNote(user.getUserId(), noteId, tagId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/tags/{tagId}")
  public ResponseEntity<Void> deleteTag(
      @AuthenticationPrincipal CustomUserDetails user,
      @PathVariable UUID tagId) {
    tagService.deleteTag(user.getUserId(), tagId);
    return ResponseEntity.noContent().build();
  }
}
