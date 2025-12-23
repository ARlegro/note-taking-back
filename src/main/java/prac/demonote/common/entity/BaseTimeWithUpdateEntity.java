package prac.demonote.common.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public abstract class BaseTimeWithUpdateEntity extends BaseTimeEntity {
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
