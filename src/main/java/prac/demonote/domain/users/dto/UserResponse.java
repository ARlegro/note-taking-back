package prac.demonote.domain.users.dto;

import java.util.UUID;

public record UserResponse(UUID id, String email, String provider, String providerId) {

}
