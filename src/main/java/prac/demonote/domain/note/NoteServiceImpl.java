package prac.demonote.domain.note;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prac.demonote.domain.note.dto.NoteCursor;
import prac.demonote.domain.note.dto.NoteCreateRequest;
import prac.demonote.domain.note.dto.NoteResponse;
import prac.demonote.domain.note.dto.NoteUpdateRequest;
import prac.demonote.domain.note.dto.NotesPageResponse;
import prac.demonote.domain.note.model.Note;
import prac.demonote.domain.tags.NoteTagRepository;
import prac.demonote.domain.tags.dto.TagResponse;
import prac.demonote.domain.users.User;
import prac.demonote.domain.users.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoteServiceImpl implements NoteService {

  private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "updatedAt", "id");

  private final NoteRepository noteRepository;
  private final UserRepository userRepository;
  private final NoteTagRepository noteTagRepository;

  @Override
  @Transactional
  public NoteResponse createNote(UUID userId, NoteCreateRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

    Note note = new Note(request.title(), request.content(), user);
    Note saved = noteRepository.save(note);
    return toResponse(saved);
  }

  @Override
  public NoteResponse getNote(UUID userId, UUID noteId) {
    Note note = noteRepository.findByIdAndOwnerId(noteId, userId)
        .orElseThrow(() -> new NoteNotFoundException("Note not found: " + noteId));
    return toResponse(note);
  }

  @Override
  @Transactional
  public NoteResponse updateNote(UUID userId, UUID noteId, NoteUpdateRequest request) {
    Note note = noteRepository.findByIdAndOwnerId(noteId, userId)
        .orElseThrow(() -> new NoteNotFoundException("Note not found: " + noteId));

    note.update(request.title(), request.content());
    return toResponse(note);
  }

  @Override
  @Transactional
  public void deleteNote(UUID userId, UUID noteId) {
    if (!noteRepository.existsByIdAndOwnerId(noteId, userId)) {
      throw new NoteNotFoundException("Note not found: " + noteId);
    }
    noteRepository.deleteById(noteId);
  }

  // TODO: 메서드 오버로딩 (Cuz NoteCursor null 가능)
  @Override
  public NotesPageResponse getNotesPage(UUID userId, NoteCursor cursor, int pageSize) {
    ScrollPosition scrollPosition;
    scrollPosition = (cursor == null)
        ? ScrollPosition.keyset()
        : ScrollPosition.forward(Map.of("updatedAt", cursor.lastUpdatedAt(), "id", cursor.lastNoteId()));

    Window<Note> window = noteRepository.findByOwnerId(
        userId,
        scrollPosition,
        Limit.of(pageSize),
        DEFAULT_SORT
    );

    NoteCursor nextCursor = null;
    if (window.hasNext() && !window.isEmpty()) {
      Note lastNote = window.getContent().getLast();
      nextCursor = new NoteCursor(lastNote.getUpdatedAt(), lastNote.getId());
    }

    return new NotesPageResponse(
        window.getContent().stream().map(this::toResponse).toList(),
        nextCursor,
        pageSize,
        window.hasNext()
    );
  }

  @Override
  public Optional<NoteResponse> getReplacementNote(UUID userId, NoteCursor cursor) {
    ScrollPosition scrollPosition = ScrollPosition.forward(
        Map.of("updatedAt", cursor.lastUpdatedAt(), "id", cursor.lastNoteId())
    );

    Window<Note> window = noteRepository.findByOwnerId(
        userId,
        scrollPosition,
        Limit.of(1),
        DEFAULT_SORT
    );

    if (window.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(toResponse(window.getContent().getFirst()));
  }

  private NoteResponse toResponse(Note note) {
    List<TagResponse> tags = noteTagRepository.findByNoteId(note.getId()).stream()
        .map(noteTag -> TagResponse.from(noteTag.getTag()))
        .toList();

    return new NoteResponse(
        note.getId(),
        note.getTitle(),
        note.getContent(),
        tags,
        note.getCreatedAt(),
        note.getUpdatedAt()
    );
  }
}
