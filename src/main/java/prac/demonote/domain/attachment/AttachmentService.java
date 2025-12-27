package prac.demonote.domain.attachment;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prac.demonote.domain.attachment.exception.AttachmentNotFoundException;
import prac.demonote.domain.attachment.model.Attachment;
import prac.demonote.domain.attachment.model.FileMetadata;
import prac.demonote.domain.users.User;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttachmentService {

  private final AttachmentRepository attachmentRepository;

  @Transactional
  public Attachment createPendingAttachment(User owner, FileMetadata metadata, String storedName) {
    Attachment attachment = new Attachment(owner, metadata, storedName);
    return attachmentRepository.save(attachment);
  }

  @Transactional
  public Attachment markAsUploaded(UUID attachmentId) {

    Attachment attachment = attachmentRepository.findById(attachmentId)
        .orElseThrow(
            () -> new AttachmentNotFoundException("Attachment not found with id: " + attachmentId));

    attachment.markAsUploaded();
    return attachment;
  }
}
