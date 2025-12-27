package prac.demonote.domain.attachment.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import prac.demonote.domain.attachment.model.AttachmentStatus;

public record AttachmentResponseDTO(
    UUID id,
    String originalName,
    String storedName,
    long fileSize,
    String contentType,
    AttachmentStatus status,
    LocalDateTime createdAt
) {

}
