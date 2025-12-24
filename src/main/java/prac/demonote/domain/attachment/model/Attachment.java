package prac.demonote.domain.attachment.model;

import static jakarta.persistence.GenerationType.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;

@Entity
@Table(name = "attachments")
@Getter
public class Attachment {

  @Id
  @GeneratedValue(strategy = UUID)
  @Column(name = "id")
  private UUID id;
  private String fileName;

}
