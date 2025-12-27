package prac.demonote.domain.attachment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prac.demonote.common.entity.BaseTimeEntity;
import prac.demonote.domain.users.User;

@Entity
@Table(name = "attachments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attachment extends BaseTimeEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "owner_id", nullable = false)
  private User owner;

  @Column(nullable = false)
  private String originalName;

  @Column(nullable = false)
  private String storedName;

  @Column(nullable = false)
  private long fileSize;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AttachmentStatus status;

  @Column(nullable = false)
  private String contentType;

  public Attachment(User owner, FileMetadata metadata, String storedName) {
    this.owner = owner;
    this.originalName = metadata.originalName();
    this.storedName = storedName;
    this.fileSize = metadata.size();
    this.contentType = metadata.contentType();
    this.status = AttachmentStatus.PENDING;
  }

  public void markAsUploaded() {
    this.status = AttachmentStatus.UPLOADED;
  }
}
