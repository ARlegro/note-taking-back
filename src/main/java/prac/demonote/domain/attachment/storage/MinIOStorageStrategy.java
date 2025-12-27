package prac.demonote.domain.attachment.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import prac.demonote.domain.attachment.dto.PresignedUrlResponse;

@Component
@ConditionalOnProperty(name = "storage.type", havingValue = "minio")
public class MinIOStorageStrategy implements StorageStrategy {


  @Override
  public String save(MultipartFile file, String userId) {
    return "";
  }

  @Override
  public PresignedUrlResponse generatePresignedUrl(String userId, String fileName,
      String contentType) {
    return null;
  }
}
