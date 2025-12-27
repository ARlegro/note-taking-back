package prac.demonote.domain.attachment;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import prac.demonote.domain.attachment.dto.AttachmentCreateResponseDTO;
import prac.demonote.domain.attachment.dto.AttachmentResponseDTO;
import prac.demonote.domain.attachment.dto.PresignedUrlResponse;
import prac.demonote.domain.attachment.model.Attachment;
import prac.demonote.domain.attachment.model.FileMetadata;
import prac.demonote.domain.attachment.storage.StorageStrategy;
import prac.demonote.domain.attachment.util.FileUtils;
import prac.demonote.domain.users.User;
import prac.demonote.domain.users.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentFacade {

  private final StorageStrategy storageStrategy;
  private final AttachmentService attachmentService;
  private final UserRepository userRepository;
  private final AttachmentMapper attachmentMapper;

  // 초반 테스트 용
  @Transactional()
  public String save(MultipartFile attachment, UUID noteId) {
    /**
     * 1. 검증
     * 2. 스토리지에 저장
     * 3. 메타데이터 DB에 저장
     */
    log.info("AttachmentFacade Save 시작");
    FileUtils.validateMultipartFile(attachment);
    String fileKey = storageStrategy.save(attachment, noteId.toString());
    log.info("fileKey : {}", fileKey);

    // todo : 메타데이터 저장 (이건 설계 자세히 나오면 ㄱ)

    return fileKey;
  }

  @Transactional
  public AttachmentCreateResponseDTO getPresignedUrl(MultipartFile attachment, UUID userId) {
    // 1. 파일 검증
    FileUtils.validateMultipartFile(attachment);

    // 2. 파일 메타데이터 추출
    FileMetadata metadata = FileUtils.toFileMetadata(attachment);

    // 3. User 조회
    User owner = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

    // 4. PresignedURL 생성
    String normalizedFileName = FileUtils.normalizeFileName(metadata.originalName());
    PresignedUrlResponse presignedUrlResponse = storageStrategy.generatePresignedUrl(
        userId.toString(),
        normalizedFileName,
        metadata.contentType()
    );

    // 5. Attachment 메타데이터 저장 (PENDING 상태)
    Attachment savedAttachment = attachmentService.createPendingAttachment(
        owner,
        metadata,
        presignedUrlResponse.key()
    );

    // 6. 응답 생성
    return new AttachmentCreateResponseDTO(
        savedAttachment.getId(),
        presignedUrlResponse.url(),
        presignedUrlResponse.key(),
        presignedUrlResponse.expiration()
    );
  }

  public AttachmentResponseDTO markAsUploaded(UUID attachmentId){
    Attachment attachment = attachmentService.markAsUploaded(attachmentId);
    return  attachmentMapper.toResponse(attachment);
  }
}
