package prac.demonote.domain.tags.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prac.demonote.common.entity.BaseTimeWithUpdateEntity;
import prac.demonote.domain.note.model.Note;

@Entity
@Table(name = "note_tags", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"note_id", "tag_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NoteTag extends BaseTimeWithUpdateEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "note_id", nullable = false)
  private Note note;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "tag_id", nullable = false)
  private Tag tag;

  public NoteTag(Note note, Tag tag) {
    this.note = note;
    this.tag = tag;
  }
}
