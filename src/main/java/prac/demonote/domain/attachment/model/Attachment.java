package prac.demonote.domain.attachment.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

import static jakarta.persistence.GenerationType.UUID;

@Entity
@Table(name = "attachments")
@Getter
public class Attachment {

  @Id @GeneratedValue(strategy = UUID)
  @Column(name = "id")
  private UUID id;
  private String fileName;

}
