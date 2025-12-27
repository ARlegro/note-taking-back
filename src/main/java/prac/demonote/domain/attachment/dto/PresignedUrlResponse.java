package prac.demonote.domain.attachment.dto;


import java.time.Instant;

public record PresignedUrlResponse(

    String url,
    String key,
    Instant expiration
) {

}
