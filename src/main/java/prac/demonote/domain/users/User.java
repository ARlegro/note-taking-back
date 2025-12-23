package prac.demonote.domain.users;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prac.demonote.common.entity.BaseTimeWithUpdateEntity;

import static lombok.AccessLevel.PROTECTED;


@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class User extends BaseTimeWithUpdateEntity {

    private String email;

    public User(String email) {
        this.email = email;
    }
}
