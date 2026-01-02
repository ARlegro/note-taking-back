package prac.demonote.domain.note;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import prac.demonote.domain.note.dto.NoteCreateRequest;
import prac.demonote.domain.note.dto.NoteCursor;
import prac.demonote.domain.note.dto.NoteResponse;
import prac.demonote.domain.note.dto.NoteUpdateRequest;
import prac.demonote.domain.note.dto.NotesPageResponse;
import prac.demonote.global.security.CustomUserDetails;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

  private final NoteService noteService;

  @PostMapping
  public ResponseEntity<NoteResponse> createNote(
      @AuthenticationPrincipal CustomUserDetails user,
      @RequestBody NoteCreateRequest request) {
    NoteResponse response = noteService.createNote(user.getUserId(), request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{noteId}")
  public ResponseEntity<NoteResponse> getNote(
      @AuthenticationPrincipal CustomUserDetails user,
      @PathVariable UUID noteId) {
    NoteResponse response = noteService.getNote(user.getUserId(), noteId);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{noteId}")
  public ResponseEntity<NoteResponse> updateNote(
      @AuthenticationPrincipal CustomUserDetails user,
      @PathVariable UUID noteId,
      @RequestBody NoteUpdateRequest request) {
    NoteResponse response = noteService.updateNote(user.getUserId(), noteId, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{noteId}")
  public ResponseEntity<Void> deleteNote(
      @AuthenticationPrincipal CustomUserDetails user,
      @PathVariable UUID noteId) {
    noteService.deleteNote(user.getUserId(), noteId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<NotesPageResponse> getNotesPage(
      @AuthenticationPrincipal CustomUserDetails user,
      @RequestParam(required = false) LocalDateTime cursorUpdatedAt,
      @RequestParam(required = false) UUID cursorNoteId,
      @RequestParam(defaultValue = "10") int pageSize) {

    NoteCursor cursor = null;
    if (cursorUpdatedAt != null && cursorNoteId != null) {
      cursor = new NoteCursor(cursorUpdatedAt, cursorNoteId);
    }

    NotesPageResponse response = noteService.getNotesPage(user.getUserId(), cursor, pageSize);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/replacement")
  public ResponseEntity<NoteResponse> getReplacementNote(
      @AuthenticationPrincipal CustomUserDetails user,
      @RequestParam LocalDateTime cursorUpdatedAt,
      @RequestParam UUID cursorNoteId) {

    NoteCursor cursor = new NoteCursor(cursorUpdatedAt, cursorNoteId);
    return noteService.getReplacementNote(user.getUserId(), cursor)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.noContent().build());
  }
}
