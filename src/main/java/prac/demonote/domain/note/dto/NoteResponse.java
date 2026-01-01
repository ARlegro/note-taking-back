package prac.demonote.domain.note.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NoteResponse(
    UUID id,
    String title,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
