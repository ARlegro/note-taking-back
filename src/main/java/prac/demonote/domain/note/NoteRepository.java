package prac.demonote.domain.note;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import prac.demonote.domain.note.model.Note;

public interface NoteRepository extends JpaRepository<Note, UUID> {

}
