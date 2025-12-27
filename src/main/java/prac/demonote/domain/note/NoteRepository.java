package prac.demonote.domain.note;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import prac.demonote.domain.note.model.Note;

public interface NoteRepository extends JpaRepository<Note, UUID> {


  // @Query("SELECT n FROM notes where id = "
  // Optional<Note> findByIdAndOwnerId(@Param("noteId") UUID id, @Param("") UUID ownerId);

  // @Query("SELECT n FROM notes n WHERE n.id = :noteId AND n.owner.id = :ownerId")
  @Query("SELECT n FROM Note n WHERE n.id = :noteId AND  n.owner.id = :ownerId")
  Optional<Note> findByIdAndOwnerId(UUID noteId, UUID ownerId);
}
