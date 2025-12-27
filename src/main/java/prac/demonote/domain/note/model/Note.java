package prac.demonote.domain.note.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import prac.demonote.common.entity.BaseTimeWithUpdateEntity;
import prac.demonote.domain.users.User;

@Entity
@Table(name = "notes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Note extends BaseTimeWithUpdateEntity {

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "owner_id", nullable = false)
  private User owner;

  public Note(String title, String content, User owner) {
    this.title = title;
    this.content = content;
    this.owner = owner;
  }
}
