package prac.demonote.domain.attachment;

import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import prac.demonote.domain.attachment.exception.InvalidAttachmentException;
import prac.demonote.domain.attachment.storage.StorageStrategy;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentFacade {

  private final StorageStrategy storageStrategy;
  private String uploadPath;
  private static final Set<String> ALLOWED_EXTENSIONS =
      Set.of("png", "jpg", "jpeg", "gif", "pdf");

  @Transactional()
  public void save(MultipartFile attachment, UUID noteId) {
    /**
     * 1. 검증
     * 2. 스토리지에 저장
     * 3. 메타데이터 DB에 저장
     */
    log.info("AttachmentFacade Save 시작");
    validateMultipartFile(attachment);
    String fileKey = storageStrategy.save(attachment, noteId.toString());
    log.info("fileKey : {}", fileKey);

  }

  /**
   * @param attachment
   * 빈 파일 여부
   * 확장자 존재 여부
   * 파일 크기 제한(이건 서블릿에서 ㄱ, 나중에 나눠야 되면 그때 ㄱ)
   */
  private void validateMultipartFile(MultipartFile attachment) {
    if (attachment.isEmpty()) {
      throw new InvalidAttachmentException("파일이 비어있습니다.");
    }
    String originalName = attachment.getOriginalFilename();
    if (!StringUtils.hasText(originalName)) {
      throw new InvalidAttachmentException("파일명이 유효하지 않습니다");
    }

    String extension = extractExtension(attachment.getOriginalFilename());
    if (!ALLOWED_EXTENSIONS.contains(extension)){
      throw new InvalidAttachmentException("허용되지 않은 파일 형식입니다");
    }

  }

  private String extractExtension(String fileName) {
    int lastDotIndex = fileName.lastIndexOf(".");
    if (lastDotIndex == -1) {
      throw new InvalidAttachmentException("확장자가 없는 파일은 업로드할 수 없습니다.");
    }
    return fileName.substring(lastDotIndex + 1);
  }
}
