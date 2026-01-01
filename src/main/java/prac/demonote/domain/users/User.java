package prac.demonote.domain.users;


import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prac.demonote.common.entity.BaseTimeWithUpdateEntity;
import prac.demonote.global.security.oauth2.OAuth2Provider;


@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"provider", "provider_id"})
})
@Getter
@NoArgsConstructor(access = PROTECTED)
public class User extends BaseTimeWithUpdateEntity {

  @Column
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OAuth2Provider provider;

  @Column(name = "provider_id", nullable = false)
  private String providerId;

  public User(String email, OAuth2Provider provider, String providerId) {
    this.email = email;
    this.provider = provider;
    this.providerId = providerId;
  }
}
