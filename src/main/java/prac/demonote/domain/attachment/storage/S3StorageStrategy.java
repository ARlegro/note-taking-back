package prac.demonote.domain.attachment.storage;


import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import prac.demonote.domain.attachment.dto.PresignedUrlResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Component
@ConditionalOnProperty(name = "storage.type", havingValue = "s3")
@RequiredArgsConstructor
public class S3StorageStrategy implements StorageStrategy {

  private final S3Presigner s3Presigner;

  @Override
  public String save(MultipartFile file, String userId) {

    return "";
  }

  public PresignedUrlResponse generatePresignedUrl(String userId, String fileName, String contentType) {
    String key = generateKey(userId, fileName);

    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket("demo-note-bucket")
        .key(key)
        .contentType(contentType)
        .build();

    PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10))
        .putObjectRequest(putObjectRequest)
        .build();

    PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(
        presignRequest);

    return new PresignedUrlResponse(
        presignedRequest.url().toString(),
        key,
        presignedRequest.expiration()
    );
  }

  private String generateKey(String userId, String fileName) {
    return String.format("attachments/%s/%s", userId, fileName);
  }
}
