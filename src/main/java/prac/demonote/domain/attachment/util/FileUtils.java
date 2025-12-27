package prac.demonote.domain.attachment.util;

import java.util.Set;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import prac.demonote.domain.attachment.exception.InvalidAttachmentException;
import prac.demonote.domain.attachment.model.FileMetadata;

public final class FileUtils {

  private static final Set<String> ALLOWED_EXTENSIONS = Set.of("png", "jpg", "jpeg", "gif", "pdf");

  /**
   * @param attachment 빈 파일 여부 확장자 존재 여부 파일 크기 제한(이건 서블릿에서 ㄱ, 나중에 나눠야 되면 그때 ㄱ)
   */
  public static void validateMultipartFile(MultipartFile attachment) {
    if (attachment.isEmpty()) {
      throw new InvalidAttachmentException("파일이 비어있습니다.");
    }
    String originalName = attachment.getOriginalFilename();
    if (!StringUtils.hasText(originalName)) {
      throw new InvalidAttachmentException("파일명이 유효하지 않습니다");
    }

    String extension = extractExtension(attachment.getOriginalFilename()).toLowerCase();
    if (!ALLOWED_EXTENSIONS.contains(extension)) {
      throw new InvalidAttachmentException("허용되지 않은 파일 형식입니다");
    }
  }

  public static String extractExtension(String fileName) {
    int lastDotIndex = fileName.lastIndexOf(".");
    if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
      throw new InvalidAttachmentException("확장자가 없는 파일입니다.");
    }
    return fileName.substring(lastDotIndex + 1);
  }

  public static FileMetadata toFileMetadata(MultipartFile file) {
    validateMultipartFile(file);
    return new FileMetadata(
        file.getOriginalFilename(),
        file.getSize(),
        file.getContentType());
   }

  public static String normalizeFileName(String fileName) {
    if (!StringUtils.hasText(fileName)) {
      throw new InvalidAttachmentException("파일명이 유효하지 않습니다");
    }
    return fileName.trim().replaceAll("\\s+", "_");
  }
}
