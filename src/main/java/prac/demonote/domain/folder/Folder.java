package prac.demonote.domain.folder;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prac.demonote.common.entity.BaseTimeWithUpdateEntity;
import prac.demonote.domain.users.User;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Table(name = "folders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Folder extends BaseTimeWithUpdateEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String path;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = LAZY)
    @Column(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = LAZY)
    @Column(name = "parent_id", nullable = false)
    private Folder parent;
}
