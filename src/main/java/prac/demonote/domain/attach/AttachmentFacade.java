package prac.demonote.domain.attach;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import prac.demonote.domain.attach.model.Attachment;

@Slf4j
@Service
public class AttachmentFacade {

  @Transactional()
  public void save(MultipartFile attachment, UUID noteId){
    log.info("Save 유저의 id = {}", noteId);
  }

}
