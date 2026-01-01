package prac.demonote.domain.note.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NoteCursor(
    LocalDateTime lastUpdatedAt,
    UUID lastNoteId
) {
}
