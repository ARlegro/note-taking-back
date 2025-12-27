package prac.demonote.domain.attachment.dto;

public record AttachmentCreateRequestDTO(
    String originalName,
    long fileSize,
    String contentType
) {

}
