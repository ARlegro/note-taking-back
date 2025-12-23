package prac.demonote.domain.users;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import prac.demonote.support.PostgresTestContainer;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest extends PostgresTestContainer {

    @Autowired
    private UserRepository userRepository;

    @Test
    void 사용자를_저장하면_식별자와_필드가_저장된다() {
        // given
        String email = "user@example.com";
        String provider = "GOOGLE";
        String providerId = "provider-id";
        User user = new User(email, provider, providerId);

        // when
        User savedUser = userRepository.save(user);

        // then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getProvider()).isEqualTo(provider);
        assertThat(savedUser.getProviderId()).isEqualTo(providerId);
    }

    @Test
    void 사용자_ID로_조회하면_사용자가_반환된다() {
        // given
        String email = "find-id@example.com";
        User savedUser = userRepository.save(new User(email, "GITHUB", "gh-123"));

        // when
        Optional<User> result = userRepository.findById(savedUser.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
    }

    @Test
    void 이메일로_조회하면_사용자가_반환된다() {
        // given
        String email = "find-email@example.com";
        String provider = "KAKAO";
        userRepository.save(new User(email, provider, "kakao-001"));

        // when
        Optional<User> result = userRepository.findByEmail(email);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getProvider()).isEqualTo(provider);
    }
}
