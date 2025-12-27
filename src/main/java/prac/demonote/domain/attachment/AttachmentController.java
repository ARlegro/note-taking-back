package prac.demonote.domain.attachment;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import prac.demonote.domain.attachment.dto.AttachmentCreateResponseDTO;
import prac.demonote.domain.attachment.dto.AttachmentResponseDTO;

@Slf4j
@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {

  private final AttachmentFacade attachmentFacade;

  // todo : userId는 나중에 JWT에서 가져오기
  @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<AttachmentCreateResponseDTO> createPresignedUrl(
      @RequestParam("attachment") MultipartFile file,
      @RequestParam("userId") UUID userId
  ) {
    AttachmentCreateResponseDTO response = attachmentFacade.getPresignedUrl(file, userId);
    return ResponseEntity.ok(response);
  }

  // todo : userId는 나중에 JWT에서 가져오기
  @PatchMapping("/{attachmentId}/upload-complete")
  public ResponseEntity<AttachmentResponseDTO> markAsUploaded(
      @PathVariable UUID attachmentId) {
    AttachmentResponseDTO response = attachmentFacade.markAsUploaded(attachmentId);
    return ResponseEntity.ok(response);
  }
}
