package prac.demonote.domain.attachment;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import prac.demonote.domain.attachment.model.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
  @Query("SELECT a FROM Attachment a WHERE a.id = :id AND a.owner.id = :ownerId")
  Optional<Attachment> findByIdAndOwnerId(UUID id, UUID ownerId);
}
