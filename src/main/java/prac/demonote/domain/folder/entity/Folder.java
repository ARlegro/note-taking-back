package prac.demonote.domain.folder.entity;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prac.demonote.common.entity.BaseTimeWithUpdateEntity;
import prac.demonote.domain.users.User;

@Entity
@Getter
@Table(name = "folders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Folder extends BaseTimeWithUpdateEntity {

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "path", nullable = false)
  private String path;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "parent_id")
  private Folder parent;
}
