package prac.demonote.domain.tags.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import prac.demonote.domain.tags.model.Tag;

public record TagResponse(
    UUID id,
    String name,
    LocalDateTime createdAt
) {
  public static TagResponse from(Tag tag) {
    return new TagResponse(tag.getId(), tag.getName(), tag.getCreatedAt());
  }
}
