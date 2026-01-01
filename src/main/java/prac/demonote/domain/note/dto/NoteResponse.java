package prac.demonote.domain.note.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import prac.demonote.domain.tags.dto.TagResponse;

public record NoteResponse(
    UUID id,
    String title,
    String content,
    List<TagResponse> tags,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
