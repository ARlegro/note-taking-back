package prac.demonote.domain.users;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;
import prac.demonote.support.PostgresTestContainer;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest()
class userTest extends PostgresTestContainer {

  @Test
  @DisplayName("user는 BaseTimeWithUpdateEntity의 필드를 상속받아 접근할 수 있다")
  void user_상속_필드_접근_가능() {
    // given
    User target = new User("icb1696@naver.com");
    UUID id = UUID.randomUUID();
    LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
    LocalDateTime updatedAt = LocalDateTime.now();
    String email = "test@example.com";

    ReflectionTestUtils.setField(target, "id", id);
    ReflectionTestUtils.setField(target, "createdAt", createdAt);
    ReflectionTestUtils.setField(target, "updatedAt", updatedAt);
    ReflectionTestUtils.setField(target, "email", email);

    // when
    UUID resultId = target.getId();
    LocalDateTime resultCreatedAt = target.getCreatedAt();
    LocalDateTime resultUpdatedAt = target.getUpdatedAt();
    String resultEmail = target.getEmail();

    // then
    assertThat(resultId).isEqualTo(id);
    assertThat(resultCreatedAt).isEqualTo(createdAt);
    assertThat(resultUpdatedAt).isEqualTo(updatedAt);
    assertThat(resultEmail).isEqualTo(email);
  }
}
