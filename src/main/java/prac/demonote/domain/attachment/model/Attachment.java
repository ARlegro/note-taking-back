package prac.demonote.domain.attachment.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import prac.demonote.common.entity.BaseTimeEntity;

@Entity
@Table(name = "attachments")
@Getter
public class Attachment extends BaseTimeEntity {

  private String fileName;
  private String filePath;

}
