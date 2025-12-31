package prac.demonote.domain.users;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import prac.demonote.global.security.oauth2.OAuth2Provider;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);

  Optional<User> findByProviderAndProviderId(OAuth2Provider provider, String providerId);
}
