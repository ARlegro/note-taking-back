package prac.demonote.domain.folder.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import prac.demonote.common.PermissionType;
import prac.demonote.common.entity.BaseTimeWithUpdateEntity;

@Entity
@Table(name = "folder_permissions")
@Getter
public class FolderPermission extends BaseTimeWithUpdateEntity {

  @Column(nullable = false)
  private PermissionType permissionType;
}
