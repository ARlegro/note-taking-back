package prac.demonote.domain.tags;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import org.hibernate.exception.ConstraintViolationException;
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
import prac.demonote.config.JpaAuditingConfig;
import prac.demonote.domain.note.NoteRepository;
import prac.demonote.domain.note.model.Note;
import prac.demonote.domain.tags.model.NoteTag;
import prac.demonote.domain.tags.model.Tag;
import prac.demonote.domain.users.User;
import prac.demonote.domain.users.UserRepository;
import prac.demonote.global.security.oauth2.OAuth2Provider;
import prac.demonote.support.PostgresTestContainer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(JpaAuditingConfig.class)
class NoteTagRepositoryTest extends PostgresTestContainer {

  @Autowired
  private NoteTagRepository noteTagRepository;

  @Autowired
  private TagRepository tagRepository;

  @Autowired
  private NoteRepository noteRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TestEntityManager tem;

  private User testUser;
  private Note testNote;
  private Tag testTag;

  @BeforeEach
  void setUp() {
    testUser = userRepository.save(
        new User("test@example.com", OAuth2Provider.GOOGLE, "provider-id-" + System.nanoTime())
    );
    testNote = noteRepository.save(new Note("Test Note", "Test Content", testUser));
    testTag = tagRepository.save(new Tag("java", testUser));
    tem.flush();
    tem.clear();
  }

  @Nested
  @DisplayName("노트-태그 연결 저장 및 조회")
  class SaveAndFind {

    @Test
    void 노트에_태그를_연결할_수_있다() {
      // given
      NoteTag noteTag = new NoteTag(testNote, testTag);

      // when
      NoteTag saved = noteTagRepository.save(noteTag);
      tem.flush();
      tem.clear();

      // then
      Optional<NoteTag> found = noteTagRepository.findById(saved.getId());
      assertThat(found)
          .isPresent()
          .get()
          .satisfies(nt -> {
            assertThat(nt.getNote().getId()).isEqualTo(testNote.getId());
            assertThat(nt.getTag().getId()).isEqualTo(testTag.getId());
          });
    }

    @Test
    void 노트ID로_연결된_모든_NoteTag를_조회할_수_있다() {
      // given
      Tag tag2 = tagRepository.save(new Tag("spring", testUser));
      Tag tag3 = tagRepository.save(new Tag("kotlin", testUser));
      noteTagRepository.save(new NoteTag(testNote, testTag));
      noteTagRepository.save(new NoteTag(testNote, tag2));
      noteTagRepository.save(new NoteTag(testNote, tag3));
      tem.flush();
      tem.clear();

      // when
      List<NoteTag> noteTags = noteTagRepository.findByNoteId(testNote.getId());

      // then
      assertThat(noteTags).hasSize(3);
    }

    @Test
    void 태그ID로_연결된_모든_NoteTag를_조회할_수_있다() {
      // given
      Note note2 = noteRepository.save(new Note("Note 2", "Content 2", testUser));
      Note note3 = noteRepository.save(new Note("Note 3", "Content 3", testUser));
      noteTagRepository.save(new NoteTag(testNote, testTag));
      noteTagRepository.save(new NoteTag(note2, testTag));
      noteTagRepository.save(new NoteTag(note3, testTag));
      tem.flush();
      tem.clear();

      // when
      List<NoteTag> noteTags = noteTagRepository.findByTagId(testTag.getId());

      // then
      assertThat(noteTags).hasSize(3);
    }
  }

  @Nested
  @DisplayName("중복 연결 검증")
  class DuplicateValidation {

    @Test
    void 동일_노트에_같은_태그를_중복연결할_수_없다() {
      // given
      noteTagRepository.save(new NoteTag(testNote, testTag));
      tem.flush();
      tem.clear();

      // when & then
      NoteTag duplicate = new NoteTag(testNote, testTag);
      assertThatThrownBy(() -> {
        noteTagRepository.save(duplicate);
        tem.flush();
      }).isInstanceOf(ConstraintViolationException.class);
    }
  }

  @Nested
  @DisplayName("노트-태그 연결 삭제")
  class Delete {

    @Test
    void 노트ID와_태그ID로_연결을_삭제할_수_있다() {
      // given
      noteTagRepository.save(new NoteTag(testNote, testTag));
      tem.flush();
      tem.clear();

      // when
      noteTagRepository.deleteByNoteIdAndTagId(testNote.getId(), testTag.getId());
      tem.flush();
      tem.clear();

      // then
      boolean exists = noteTagRepository.existsByNoteIdAndTagId(testNote.getId(), testTag.getId());
      assertThat(exists).isFalse();
    }

    @Test
    void 태그ID로_모든_연결을_삭제할_수_있다() {
      // given
      Note note2 = noteRepository.save(new Note("Note 2", "Content 2", testUser));
      noteTagRepository.save(new NoteTag(testNote, testTag));
      noteTagRepository.save(new NoteTag(note2, testTag));
      tem.flush();
      tem.clear();

      // when
      noteTagRepository.deleteByTagId(testTag.getId());
      tem.flush();
      tem.clear();

      // then
      List<NoteTag> remaining = noteTagRepository.findByTagId(testTag.getId());
      assertThat(remaining).isEmpty();
    }

    @Test
    void 노트ID로_모든_연결을_삭제할_수_있다() {
      // given
      Tag tag2 = tagRepository.save(new Tag("spring", testUser));
      noteTagRepository.save(new NoteTag(testNote, testTag));
      noteTagRepository.save(new NoteTag(testNote, tag2));
      tem.flush();
      tem.clear();

      // when
      noteTagRepository.deleteByNoteId(testNote.getId());
      tem.flush();
      tem.clear();

      // then
      List<NoteTag> remaining = noteTagRepository.findByNoteId(testNote.getId());
      assertThat(remaining).isEmpty();
    }
  }

  @Nested
  @DisplayName("존재 확인")
  class ExistsCheck {

    @Test
    void 노트와_태그의_연결이_존재하면_true를_반환한다() {
      // given
      noteTagRepository.save(new NoteTag(testNote, testTag));
      tem.flush();
      tem.clear();

      // when
      boolean exists = noteTagRepository.existsByNoteIdAndTagId(testNote.getId(), testTag.getId());

      // then
      assertThat(exists).isTrue();
    }

    @Test
    void 노트와_태그의_연결이_존재하지_않으면_false를_반환한다() {
      // given - 연결 없음

      // when
      boolean exists = noteTagRepository.existsByNoteIdAndTagId(testNote.getId(), testTag.getId());

      // then
      assertThat(exists).isFalse();
    }
  }
}
