package prac.demonote.domain.note.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prac.demonote.common.entity.BaseTimeWithUpdateEntity;

@Entity
@Table(name = "notes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Note extends BaseTimeWithUpdateEntity {

  private String title;
  private String content;

  public Note(String title, String content) {
    this.title = title;
    this.content = content;
  }
}
