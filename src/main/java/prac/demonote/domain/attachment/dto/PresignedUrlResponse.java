package prac.demonote.domain.attachment.dto;

import java.time.Instant;
import java.util.Objects;

public record PresignedUrlResponse(
        String url,
        String key,
        Instant expiration) {

    public PresignedUrlResponse {
        Objects.requireNonNull(url, "url must not be null");
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(expiration, "expiration must not be null");
    }
}
