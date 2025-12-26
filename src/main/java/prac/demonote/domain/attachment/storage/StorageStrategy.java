package prac.demonote.domain.attachment.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageStrategy {

  String save(MultipartFile file, String userId);
}
