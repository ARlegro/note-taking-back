package prac.demonote.domain.note.dto;

import java.util.List;

public record NotesPageResponse(
    List<NoteResponse> notes,
    NoteCursor nextCursor,
    int pageSize,
    boolean hasNext
) {
}
