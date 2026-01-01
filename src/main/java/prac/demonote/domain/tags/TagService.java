package prac.demonote.domain.tags;

import java.util.List;
import java.util.UUID;
import prac.demonote.domain.tags.dto.NoteTagsResponse;
import prac.demonote.domain.tags.dto.TagCreateRequest;
import prac.demonote.domain.tags.dto.TagResponse;
import prac.demonote.domain.tags.dto.TagsResponse;

public interface TagService {

  NoteTagsResponse addTagToNote(UUID userId, UUID noteId, TagCreateRequest request);

  void removeTagFromNote(UUID userId, UUID noteId, UUID tagId);

  TagsResponse getAllTags(UUID userId);

  void deleteTag(UUID userId, UUID tagId);

  List<TagResponse> getTagsForNote(UUID noteId);
}
