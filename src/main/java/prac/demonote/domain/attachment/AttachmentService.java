package prac.demonote.domain.attachment;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prac.demonote.domain.attachment.exception.AttachmentNotFoundException;
import prac.demonote.domain.attachment.model.Attachment;
import prac.demonote.domain.users.User;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttachmentService {

  private final AttachmentRepository attachmentRepository;

  @Transactional
  public Attachment createPendingAttachment(User owner, String originalName, String storedName,
      long fileSize, String contentType) {
    Attachment attachment = new Attachment(owner, originalName, storedName, fileSize, contentType);
    return attachmentRepository.save(attachment);
  }

  @Transactional
  public Attachment markAsUploaded(UUID attachmentId, UUID userId) {

    Attachment attachment = attachmentRepository.findByIdAndOwnerId(attachmentId, userId)
        .orElseThrow(() -> new AttachmentNotFoundException(
            "Attachment not found with id: " + attachmentId + " and userId: " + userId));

    attachment.markAsUploaded();
    return attachmentRepository.save(attachment);
  }
}
