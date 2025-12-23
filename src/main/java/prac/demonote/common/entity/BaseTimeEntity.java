package prac.demonote.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public abstract class BaseTimeEntity extends BaseEntity {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
