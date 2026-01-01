package prac.demonote.domain.tags;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.hibernate.exception.ConstraintViolationException;
import prac.demonote.config.JpaAuditingConfig;
import prac.demonote.domain.tags.model.Tag;
import prac.demonote.domain.users.User;
import prac.demonote.domain.users.UserRepository;
import prac.demonote.global.security.oauth2.OAuth2Provider;
import prac.demonote.support.PostgresTestContainer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(JpaAuditingConfig.class)
class TagRepositoryTest extends PostgresTestContainer {

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TestEntityManager tem;

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = userRepository.save(
        new User("test@example.com", OAuth2Provider.GOOGLE, "provider-id-" + System.nanoTime())
    );
    tem.flush();
    tem.clear();
  }

  @Nested
  @DisplayName("태그 저장 및 조회")
  class SaveAndFind {

    @Test
    void 태그를_저장하고_조회할_수_있다() {
      // given
      Tag tag = new Tag("java", testUser);

      // when
      Tag savedTag = tagRepository.save(tag);
      tem.flush();
      tem.clear();

      // then
      Optional<Tag> found = tagRepository.findById(savedTag.getId());
      assertThat(found)
          .isPresent()
          .get()
          .satisfies(t -> {
            assertThat(t.getName()).isEqualTo("java");
            assertThat(t.getOwner().getId()).isEqualTo(testUser.getId());
          });
    }

    @Test
    void 사용자ID로_해당_사용자의_모든_태그를_조회할_수_있다() {
      // given
      tagRepository.save(new Tag("java", testUser));
      tagRepository.save(new Tag("spring", testUser));
      tagRepository.save(new Tag("kotlin", testUser));
      tem.flush();
      tem.clear();

      // when
      List<Tag> tags = tagRepository.findByOwnerId(testUser.getId());

      // then
      assertThat(tags).hasSize(3);
      assertThat(tags).extracting(Tag::getName)
          .containsExactlyInAnyOrder("java", "spring", "kotlin");
    }

    @Test
    void 사용자ID와_태그이름으로_태그를_조회할_수_있다() {
      // given
      tagRepository.save(new Tag("java", testUser));
      tem.flush();
      tem.clear();

      // when
      Optional<Tag> found = tagRepository.findByNameAndOwnerId("java", testUser.getId());

      // then
      assertThat(found)
          .isPresent()
          .get()
          .satisfies(t -> assertThat(t.getName()).isEqualTo("java"));
    }

    @Test
    void 존재하지_않는_태그이름으로_조회하면_빈값을_반환한다() {
      // given
      tagRepository.save(new Tag("java", testUser));
      tem.flush();
      tem.clear();

      // when
      Optional<Tag> found = tagRepository.findByNameAndOwnerId("python", testUser.getId());

      // then
      assertThat(found).isEmpty();
    }
  }

  @Nested
  @DisplayName("태그 중복 검증")
  class DuplicateValidation {

    @Test
    void 동일_사용자에게_중복된_태그이름은_저장할_수_없다() {
      // given
      tagRepository.save(new Tag("java", testUser));
      tem.flush();
      tem.clear();

      // when & then
      Tag duplicateTag = new Tag("java", testUser);
      assertThatThrownBy(() -> {
        tagRepository.save(duplicateTag);
        tem.flush();
      }).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void 다른_사용자는_같은_이름의_태그를_가질_수_있다() {
      // given
      User anotherUser = userRepository.save(
          new User("another@example.com", OAuth2Provider.GOOGLE, "another-provider-id")
      );
      tagRepository.save(new Tag("java", testUser));
      tem.flush();
      tem.clear();

      // when
      Tag anotherUserTag = tagRepository.save(new Tag("java", anotherUser));
      tem.flush();
      tem.clear();

      // then
      assertThat(anotherUserTag.getId()).isNotNull();
      assertThat(anotherUserTag.getName()).isEqualTo("java");
    }
  }

  @Nested
  @DisplayName("태그 존재 확인")
  class ExistsCheck {

    @Test
    void 사용자ID와_태그ID로_태그존재여부를_확인할_수_있다_존재함() {
      // given
      Tag tag = tagRepository.save(new Tag("java", testUser));
      tem.flush();
      tem.clear();

      // when
      boolean exists = tagRepository.existsByIdAndOwnerId(tag.getId(), testUser.getId());

      // then
      assertThat(exists).isTrue();
    }

    @Test
    void 사용자ID와_태그ID로_태그존재여부를_확인할_수_있다_존재안함() {
      // given
      Tag tag = tagRepository.save(new Tag("java", testUser));
      User anotherUser = userRepository.save(
          new User("another@example.com", OAuth2Provider.GOOGLE, "another-provider-id")
      );
      tem.flush();
      tem.clear();

      // when
      boolean exists = tagRepository.existsByIdAndOwnerId(tag.getId(), anotherUser.getId());

      // then
      assertThat(exists).isFalse();
    }
  }

  @Nested
  @DisplayName("태그 삭제")
  class Delete {

    @Test
    void 태그ID로_태그를_삭제할_수_있다() {
      // given
      Tag tag = tagRepository.save(new Tag("java", testUser));
      tem.flush();
      tem.clear();

      // when
      tagRepository.deleteById(tag.getId());
      tem.flush();
      tem.clear();

      // then
      Optional<Tag> found = tagRepository.findById(tag.getId());
      assertThat(found).isEmpty();
    }
  }
}
