package prac.demonote.domain.attachment;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import prac.demonote.domain.attachment.model.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

}
