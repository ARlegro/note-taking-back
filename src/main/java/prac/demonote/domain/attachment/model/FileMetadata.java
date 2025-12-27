package prac.demonote.domain.attachment.model;

/**
 * 파일의 메타데이터를 표현하는 Value Object
 */
public record FileMetadata(
    String originalName,
    long size,
    String contentType
) {
}
