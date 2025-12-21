package prac.demonote.domain.attach;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import java.net.http.HttpResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import prac.demonote.domain.attach.dto.HealthCheckResponseDTO;

@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {

  private final AttachmentFacade attachmentFacade;

  @GetMapping("")
  public ResponseEntity<HealthCheckResponseDTO> healthcheck() {
    return ResponseEntity.ok().body(new HealthCheckResponseDTO("좋았어"));
  }

  @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> uploadAttachment(
      @RequestParam("attachment") MultipartFile file,
      @RequestParam("noteId") UUID noteId
  ) {
    if (file.isEmpty()) {
      return ResponseEntity.badRequest().build();
    }
    System.out.println("컨트롤러 통과");
    attachmentFacade.save(file, noteId);
    return ResponseEntity.ok().build();
  }
}
