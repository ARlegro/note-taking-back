package prac.demonote.domain.attachment;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import prac.demonote.domain.attachment.dto.PresignedUrlResponse;
import prac.demonote.domain.attachment.storage.StorageStrategy;
import prac.demonote.domain.attachment.util.FileUtils;
import prac.demonote.domain.note.NoteRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentFacade {

  private final StorageStrategy storageStrategy;
  private final NoteRepository noteRepository;
  private String uploadPath;

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

  // note_id가

  /**
   *
   */
  public PresignedUrlResponse getPresignedUrl(MultipartFile attachment, UUID noteId, UUID userId) {
    FileUtils.validateMultipartFile(attachment);
//    if (!noteRepository.existsByIdAndOwnerId(noteId, userId)) {
//      throw new RuntimeException("Note not found");
//    }

    System.out.println("attachment.getOriginalFilename() = " + attachment.getOriginalFilename());
    
    return storageStrategy.generatePresignedUrl(userId.toString(), attachment.getOriginalFilename(), attachment.getContentType());
  }
}
