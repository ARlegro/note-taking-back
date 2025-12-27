package prac.demonote.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

@MappedSuperclass
@Getter
public abstract class BaseTimeWithUpdateEntity extends BaseTimeEntity {

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
