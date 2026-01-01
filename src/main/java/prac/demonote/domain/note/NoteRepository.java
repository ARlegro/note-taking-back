package prac.demonote.domain.note;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import prac.demonote.domain.note.model.Note;

public interface NoteRepository extends JpaRepository<Note, UUID> {

  @Query("SELECT n FROM Note n WHERE n.id = :noteId AND n.owner.id = :ownerId")
  Optional<Note> findByIdAndOwnerId(UUID noteId, UUID ownerId);

  @Query("SELECT (count(n) > 0) FROM Note n WHERE n.id = :noteId AND n.owner.id = :ownerId")
  boolean existsByIdAndOwnerId(UUID noteId, UUID ownerId);

  Page<Note> findByOwnerId(UUID ownerId, Pageable pageable);

  long countByOwnerId(UUID ownerId);

  // Spring Data Keyset 페이지네이션 (Window 기반)
  Window<Note> findByOwnerId(UUID ownerId, ScrollPosition scrollPosition, Limit limit, Sort sort);
}
