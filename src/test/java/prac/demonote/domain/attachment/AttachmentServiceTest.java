package prac.demonote.domain.attachment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import prac.demonote.domain.attachment.exception.AttachmentNotFoundException;
import prac.demonote.domain.attachment.exception.UnauthorizedAttachmentAccessException;
import prac.demonote.domain.attachment.model.Attachment;
import prac.demonote.domain.attachment.model.AttachmentStatus;
import prac.demonote.domain.attachment.model.FileMetadata;
import prac.demonote.domain.users.User;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

  @Mock
  private AttachmentRepository attachmentRepository;

  @InjectMocks
  private AttachmentService attachmentService;

  @Test
  @DisplayName("givenValidData_whenCreatePendingAttachment_thenReturnsSavedAttachment")
  void givenValidData_whenCreatePendingAttachment_thenReturnsSavedAttachment() {
    // given
    User owner = new User("test@example.com", "google", "google-id-123");
    FileMetadata metadata = new FileMetadata("test.txt", 1024L, "text/plain");
    String storedName = "attachments/user-id/test.txt";

    Attachment attachment = new Attachment(owner, metadata, storedName);
    when(attachmentRepository.save(any(Attachment.class))).thenReturn(attachment);

    // when
    Attachment result = attachmentService.createPendingAttachment(owner, metadata, storedName);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getOriginalName()).isEqualTo(metadata.originalName());
    assertThat(result.getStoredName()).isEqualTo(storedName);
    assertThat(result.getFileSize()).isEqualTo(metadata.size());
    assertThat(result.getContentType()).isEqualTo(metadata.contentType());
    assertThat(result.getStatus()).isEqualTo(AttachmentStatus.PENDING);
    verify(attachmentRepository).save(any(Attachment.class));
  }

  @Test
  @DisplayName("givenValidAttachmentId_whenMarkAsUploaded_thenStatusChangedToUploaded")
  void givenValidAttachmentId_whenMarkAsUploaded_thenStatusChangedToUploaded() {
    // given
    UUID attachmentId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    User owner = new User("test@example.com", "google", "google-id-123");
    FileMetadata metadata = new FileMetadata("test.txt", 1024L, "text/plain");
    Attachment attachment = new Attachment(owner, metadata, "attachments/user-id/test.txt");

    when(attachmentRepository.findByIdAndOwnerId(attachmentId, userId))
        .thenReturn(Optional.of(attachment));
    when(attachmentRepository.save(any(Attachment.class))).thenReturn(attachment);

    // when
    Attachment result = attachmentService.markAsUploaded(attachmentId, userId);

    // then
    assertThat(result.getStatus()).isEqualTo(AttachmentStatus.UPLOADED);
    verify(attachmentRepository).findByIdAndOwnerId(attachmentId, userId);
    verify(attachmentRepository).save(attachment);
  }

  @Test
  @DisplayName("givenNonExistentAttachmentId_whenMarkAsUploaded_thenThrowsAttachmentNotFoundException")
  void givenNonExistentAttachmentId_whenMarkAsUploaded_thenThrowsAttachmentNotFoundException() {
    // given
    UUID attachmentId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    when(attachmentRepository.findByIdAndOwnerId(attachmentId, userId))
        .thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> attachmentService.markAsUploaded(attachmentId, userId))
        .isInstanceOf(AttachmentNotFoundException.class);
  }

  @Test
  @DisplayName("givenDifferentOwner_whenMarkAsUploaded_thenThrowsAttachmentNotFoundException")
  void givenDifferentOwner_whenMarkAsUploaded_thenThrowsAttachmentNotFoundException() {
    // given
    UUID attachmentId = UUID.randomUUID();
    UUID differentUserId = UUID.randomUUID();

    when(attachmentRepository.findByIdAndOwnerId(attachmentId, differentUserId))
        .thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> attachmentService.markAsUploaded(attachmentId, differentUserId))
        .isInstanceOf(AttachmentNotFoundException.class);
  }
}
