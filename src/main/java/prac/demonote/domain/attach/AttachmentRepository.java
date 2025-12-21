package prac.demonote.domain.attach;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import prac.demonote.domain.attach.model.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

}
