package prac.demonote.domain.note.dto;

import java.util.List;
import java.util.UUID;

public record NoteDeleteResponse(
    List<UUID> deletedIds,
    List<NoteResponse> replacementNotes,
    long totalElements
) {
}
