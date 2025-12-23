package prac.demonote.domain.users.dto;

import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest(
        @NotBlank
        String email,

        @NotBlank
        String provider,

        @NotBlank
        String providerId) {
}
