package prac.demonote.domain.users;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest()
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class userTest {

  @Test
  @DisplayName("user는 BaseTimeWithUpdateEntity의 필드를 상속받아 접근할 수 있다")
  void user_상속_필드_접근_가능() {
    User target = new User("icb1696@naver.com");
    UUID id = UUID.randomUUID();
    LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
    LocalDateTime updatedAt = LocalDateTime.now();
    String email = "test@example.com";

    ReflectionTestUtils.setField(target, "id", id);
    ReflectionTestUtils.setField(target, "createdAt", createdAt);
    ReflectionTestUtils.setField(target, "email", email);

    assertThat(id).isEqualTo(target.getId());
    assertThat(createdAt).isEqualTo(target.getCreatedAt());
    assertThat(email).isEqualTo(target.getEmail());
  }
}

