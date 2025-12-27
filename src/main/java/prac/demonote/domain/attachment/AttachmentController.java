package prac.demonote.domain.attachment;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import prac.demonote.domain.attachment.dto.HealthCheckResponseDTO;
import prac.demonote.domain.attachment.dto.PresignedUrlResponse;

@Slf4j
@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {

  private final AttachmentFacade attachmentFacade;

  @GetMapping("")
  public ResponseEntity<HealthCheckResponseDTO> healthcheck() {
    return ResponseEntity.ok().body(new HealthCheckResponseDTO("좋았어"));
  }

//  @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
//  public ResponseEntity<String> uploadAttachment(
//      @RequestParam("attachment") MultipartFile file,
//      @RequestParam("noteId") UUID noteId
//  ) {
//    log.info("uploadAttachment Controller");
//    String fileKey = attachmentFacade.save(file, noteId);
//    return ResponseEntity.ok(fileKey);
//  }

  // todo : userId나중에 jwt용으로
  @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<PresignedUrlResponse> getPresignedUrl(
      @RequestParam("attachment") MultipartFile file,
      @RequestParam("noteId") UUID noteId,
      @RequestParam("userId") UUID userId
  ) {
    PresignedUrlResponse response = attachmentFacade.getPresignedUrl(file, noteId, userId);
    return ResponseEntity.ok(response);
  }
}
