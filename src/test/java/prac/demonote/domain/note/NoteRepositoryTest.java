package prac.demonote.domain.note;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import prac.demonote.config.JpaAuditingConfig;
import prac.demonote.domain.note.model.Note;
import prac.demonote.domain.users.User;
import prac.demonote.domain.users.UserRepository;
import prac.demonote.global.security.oauth2.OAuth2Provider;
import prac.demonote.support.PostgresTestContainer;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(JpaAuditingConfig.class)
class NoteRepositoryTest extends PostgresTestContainer {

  @Autowired
  private NoteRepository noteRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TestEntityManager tem;

  @Test
  void 사용자ID와_노트ID로_노트를_조회할_수_있다_성공(){
    // given
    User user = createTestUser();
    Note savedNote = noteRepository.save(new Note("title", "content", user));
    tem.flush();
    tem.clear();

    // when
    Optional<Note> result = noteRepository.findByIdAndOwnerId(savedNote.getId(), user.getId());

    // then
    assertThat(result)
        .isPresent()
        .get()
        .satisfies(found -> {
          assertThat(found.getId()).isEqualTo(savedNote.getId());
          assertThat(found.getOwner().getId()).isEqualTo(user.getId());
          assertThat(found.getTitle()).isEqualTo("title");
          assertThat(found.getContent()).isEqualTo("content");
        });
  }

  @Test
  void 사용자의_노트를_최근_수정순으로_페이지네이션_조회할_수_있다() {
    // given
    User user = createTestUser();
    for (int i = 0; i < 15; i++) {
      Note note = new Note("title" + i, "content" + i, user);
      noteRepository.save(note);
    }
    tem.flush();
    tem.clear();

    PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "updatedAt"));

    // when
    Page<Note> result = noteRepository.findByOwnerId(user.getId(), pageRequest);

    // then
    assertThat(result.getContent()).hasSize(10);
    assertThat(result.getTotalElements()).isEqualTo(15);
    assertThat(result.getTotalPages()).isEqualTo(2);
  }

  @Test
  void Window_Keyset_페이지네이션으로_첫_페이지를_조회할_수_있다() {
    // given
    User user = createTestUser();
    for (int i = 0; i < 15; i++) {
      Note note = new Note("title" + i, "content" + i, user);
      noteRepository.save(note);
    }
    tem.flush();
    tem.clear();

    Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt", "id");

    // when
    Window<Note> result = noteRepository.findByOwnerId(
        user.getId(),
        ScrollPosition.keyset(),
        Limit.of(10),
        sort
    );

    // then
    assertThat(result.getContent()).hasSize(10);
    assertThat(result.hasNext()).isTrue();
  }

  @Test
  void Window_Keyset_페이지네이션으로_커서_이후_조회할_수_있다() {
    // given
    User user = createTestUser();
    for (int i = 0; i < 20; i++) {
      Note note = new Note("title" + i, "content" + i, user);
      noteRepository.save(note);
    }
    tem.flush();
    tem.clear();

    Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt", "id");

    // 첫 페이지 조회
    Window<Note> firstWindow = noteRepository.findByOwnerId(
        user.getId(),
        ScrollPosition.keyset(),
        Limit.of(10),
        sort
    );

    // when: 커서 이후 조회
    Window<Note> nextWindow = noteRepository.findByOwnerId(
        user.getId(),
        firstWindow.positionAt(firstWindow.getContent().size() - 1),
        Limit.of(10),
        sort
    );

    // then: 첫 페이지와 겹치지 않고, 나머지 노트들을 반환
    assertThat(nextWindow.getContent()).isNotEmpty();
    assertThat(nextWindow.getContent()).allSatisfy(note ->
        assertThat(firstWindow.getContent()).doesNotContain(note)
    );
  }

  private User createTestUser(){
    User user = new User("test@example.com", OAuth2Provider.GOOGLE, "Provider-id");
    User savedUser = userRepository.save(user);

    assertThat(savedUser).isNotNull();
    assertThat(savedUser.getId()).isNotNull();
    return savedUser;
  }
}
