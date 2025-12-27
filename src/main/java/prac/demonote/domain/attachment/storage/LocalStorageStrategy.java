package prac.demonote.domain.attachment.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import prac.demonote.domain.attachment.dto.PresignedUrlResponse;
import prac.demonote.domain.attachment.util.FileUtils;

@Slf4j
@Component
@ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
public class LocalStorageStrategy implements StorageStrategy {

  @Value("${upload.local.path:./uploads}")
  private String basePath;

  @Override
  public String save(MultipartFile file, String userId) {
    Path userDir = Paths.get(basePath, userId);
    log.info("userDir : {}", userDir);
    try {
      Files.createDirectories(userDir);  // 디렉토리가 생성/존재하면 그냥 통과

      String extension = FileUtils.extractExtension(Objects.requireNonNull(file.getOriginalFilename()));
      String fileName = UUID.randomUUID() + "." + extension;

      Path targetPath = userDir.resolve(fileName);
      try (var in = file.getInputStream()){
        Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
      }

      return Path.of(userId, fileName).toString();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public PresignedUrlResponse generatePresignedUrl(String userId, String fileName,
      String contentType) {
    throw new RuntimeException("Local storage는 presignedUrl을 지원하지 않습니다.");
  }
}
