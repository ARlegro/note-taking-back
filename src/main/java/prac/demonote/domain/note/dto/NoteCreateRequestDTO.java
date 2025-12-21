package prac.demonote.domain.note.dto;

import prac.demonote.domain.note.model.Note;

public record NoteCreateRequestDTO(String title, String content) {

  public NoteCreateRequestDTO {
//    if (title == null || title.isBlank()) {
//      throw new IllegalArgumentException("title is required");
//    }
  }

  public Note toEntity(){
    return new Note(title, content);
  }
}
