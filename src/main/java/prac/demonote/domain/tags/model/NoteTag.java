package prac.demonote.domain.tags.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prac.demonote.common.entity.BaseTimeWithUpdateEntity;
import prac.demonote.domain.note.model.Note;

@Entity
@Table(name = "note_tags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NoteTag extends BaseTimeWithUpdateEntity {

  Note note;
  Tag tag;
}
