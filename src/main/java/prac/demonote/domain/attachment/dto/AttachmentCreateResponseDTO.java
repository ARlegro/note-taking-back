package prac.demonote.domain.attachment.dto;

import java.time.Instant;
import java.util.UUID;

public record AttachmentCreateResponseDTO(
    UUID attachmentId,
    String presignedUrl,
    String s3Key,
    Instant expiration
) {

}
