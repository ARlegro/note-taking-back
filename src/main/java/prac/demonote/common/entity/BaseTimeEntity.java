package prac.demonote.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

@MappedSuperclass
@Getter
public abstract class BaseTimeEntity extends BaseEntity {

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;
}
