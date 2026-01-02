package prac.demonote.domain.tags;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import prac.demonote.domain.tags.util.TagNameNormalizer;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagServiceImpl implements TagService {

  private final TagRepository tagRepository;
  private final NoteTagRepository noteTagRepository;
  private final NoteRepository noteRepository;

  @Override
  @Transactional
  public NoteTagsResponse addTagToNote(UUID userId, UUID noteId, TagCreateRequest request) {
    String normalizedName = TagNameNormalizer.normalize(request.tagName());

    if (tagRepository.existsByNameAndOwnerId(normalizedName, userId)) {
      throw new TagAlreadyExistsException("이미 존재하는 태그입니다.");
    }

    Note note = noteRepository.findByIdAndOwnerId(noteId, userId)
        .orElseThrow(() -> new NoteNotFoundException("노트를 찾을 수 없습니다."));

    Tag savedTag = tagRepository.save(new Tag(normalizedName, note.getOwner()));
    noteTagRepository.save(new NoteTag(note, savedTag));

    return new NoteTagsResponse(noteId, TagResponse.from(savedTag));
  }

  @Override
  @Transactional
  public void removeTagFromNote(UUID userId, UUID noteId, UUID tagId) {
    if (!noteRepository.existsByIdAndOwnerId(noteId, userId)) {
      throw new NoteNotFoundException("노트를 찾을 수 없습니다.");
    }

    int deleted = noteTagRepository.deleteByNoteIdAndTagId(noteId, tagId);
    if (deleted == 0) {
      throw new TagNotLinkedToNoteException("해당 노트에 연결되지 않은 태그입니다.");
    }
  }

  @Override
  public TagsResponse getAllTags(UUID userId) {
    List<Tag> tags = tagRepository.findByOwnerId(userId);
    List<TagResponse> tagResponses = tags.stream()
        .map(TagResponse::from)
        .toList();
    return new TagsResponse(tagResponses);
  }

  @Override
  @Transactional
  public void deleteTag(UUID userId, UUID tagId) {
    if (!tagRepository.existsByIdAndOwnerId(tagId, userId)) {
      throw new TagNotFoundException("태그를 찾을 수 없습니다.");
    }

    noteTagRepository.deleteByTagId(tagId);
    tagRepository.deleteById(tagId);
  }

  @Override
  public List<TagResponse> getTagsForNote(UUID noteId) {
    return noteTagRepository.findByNoteId(noteId).stream()
        .map(noteTag -> TagResponse.from(noteTag.getTag()))
        .toList();
  }
}
