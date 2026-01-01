package prac.demonote.domain.tags.dto;

import java.util.UUID;

public record NoteTagsResponse(
    UUID noteId,
    TagResponse tags
) {
}
