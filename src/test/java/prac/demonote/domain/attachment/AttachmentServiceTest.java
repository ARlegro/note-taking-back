package prac.demonote.domain.attachment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
  @DisplayName("첨부파일 생성 시 대기 상태로 저장된다")
  void 첨부파일_생성시_대기상태로_저장된다() {
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
  @DisplayName("업로드 완료 처리 시 상태가 업로드됨으로 변경된다")
  void 업로드_완료_처리시_상태가_업로드됨으로_변경된다() {
    // given
    UUID attachmentId = UUID.randomUUID();

    User owner = new User("test@example.com", "google", "google-id-123");
    FileMetadata metadata = new FileMetadata("test.txt", 1024L, "text/plain");
    Attachment attachment = new Attachment(owner, metadata, "attachments/user-id/test.txt");

    when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.of(attachment));

    // when
    Attachment result = attachmentService.markAsUploaded(attachmentId);

    // then
    assertThat(result.getStatus()).isEqualTo(AttachmentStatus.UPLOADED);
    verify(attachmentRepository).findById(attachmentId);
  }

  @Test
  @DisplayName("존재하지 않는 첨부파일이면 예외가 발생한다")
  void 존재하지_않는_첨부파일이면_예외가_발생한다() {
    // given
    UUID attachmentId = UUID.randomUUID();

    when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> attachmentService.markAsUploaded(attachmentId))
        .isInstanceOf(AttachmentNotFoundException.class);
  }
}
