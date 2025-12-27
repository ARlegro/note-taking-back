package prac.demonote.domain.note;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import prac.demonote.domain.note.model.Note;
import prac.demonote.domain.users.User;
import prac.demonote.domain.users.UserRepository;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class NoteRepositoryTest {

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

  private User createTestUser(){
    User user = new User("test@example.com", "GOOGLE", "Provider-id");
    User savedUser = userRepository.save(user);

    assertThat(savedUser).isNotNull();
    assertThat(savedUser.getId()).isNotNull();
    return savedUser;
  }
}
