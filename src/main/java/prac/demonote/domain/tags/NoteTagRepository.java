package prac.demonote.domain.tags;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import prac.demonote.domain.tags.model.NoteTag;

@Repository
public interface NoteTagRepository extends JpaRepository<NoteTag, UUID> {

  List<NoteTag> findByNoteId(UUID noteId);

  List<NoteTag> findByTagId(UUID tagId);

  boolean existsByNoteIdAndTagId(UUID noteId, UUID tagId);

  @Modifying
  int deleteByNoteIdAndTagId(UUID noteId, UUID tagId);

  @Modifying
  void deleteByTagId(UUID tagId);

  @Modifying
  void deleteByNoteId(UUID noteId);
}
