package prac.demonote.domain.attachment.model;

import org.springframework.web.multipart.MultipartFile;

/**
 * 파일의 메타데이터를 표현하는 Value Object
 * MultipartFile로부터 파일 정보를 추출하여 도메인 계층에서 웹 의존성 없이 사용 가능
 */
public record FileMetadata(
    String originalName,
    long size,
    String contentType
) {

  /**
   * MultipartFile로부터 FileMetadata 생성
   */
  public static FileMetadata from(MultipartFile file) {
    return new FileMetadata(
        file.getOriginalFilename(),
        file.getSize(),
        file.getContentType()
    );
  }
}
