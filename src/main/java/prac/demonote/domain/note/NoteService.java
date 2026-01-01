package prac.demonote.domain.note;

import java.util.Optional;
import java.util.UUID;
import prac.demonote.domain.note.dto.NoteCursor;
import prac.demonote.domain.note.dto.NoteCreateRequest;
import prac.demonote.domain.note.dto.NoteResponse;
import prac.demonote.domain.note.dto.NoteUpdateRequest;
import prac.demonote.domain.note.dto.NotesPageResponse;

public interface NoteService {

  NoteResponse createNote(UUID userId, NoteCreateRequest request);

  NoteResponse getNote(UUID userId, UUID noteId);

  NoteResponse updateNote(UUID userId, UUID noteId, NoteUpdateRequest request);

  void deleteNote(UUID userId, UUID noteId);

  NotesPageResponse getNotesPage(UUID userId, NoteCursor cursor, int pageSize);

  Optional<NoteResponse> getReplacementNote(UUID userId, NoteCursor cursor);
}
