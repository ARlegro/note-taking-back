package prac.demonote.domain.note;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prac.demonote.domain.note.dto.NoteCreateRequestDTO;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

  private final NoteService noteService;

  @PostMapping
  public ResponseEntity<?> save(@RequestBody NoteCreateRequestDTO dto){
    UUID noteId = noteService.save(dto);
    return ResponseEntity.ok(noteId.toString());
  }
}
