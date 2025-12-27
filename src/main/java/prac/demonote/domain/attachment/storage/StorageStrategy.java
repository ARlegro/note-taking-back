package prac.demonote.domain.attachment.storage;

import org.springframework.web.multipart.MultipartFile;
import prac.demonote.domain.attachment.dto.PresignedUrlResponse;

public interface StorageStrategy {

  String save(MultipartFile file, String userId);

  PresignedUrlResponse generatePresignedUrl(String userId, String fileName, String contentType);
}
