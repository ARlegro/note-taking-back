package prac.demonote.domain.note.dto;

import java.util.List;
import java.util.UUID;

public record NoteDeleteRequest(
    List<UUID> noteIds,
    NoteCursor currentCursor,
    int pageSize
) {
}
