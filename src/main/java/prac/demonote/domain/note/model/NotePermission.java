package prac.demonote.domain.note.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import prac.demonote.common.PermissionType;
import prac.demonote.common.entity.BaseTimeWithUpdateEntity;

@Entity
@Table(name = "note_permissions")
@Getter
public class NotePermission extends BaseTimeWithUpdateEntity {

  private UUID noteId;
  private UUID granteeId; // 허가 받은 사람
  private PermissionType permissionType;

}
