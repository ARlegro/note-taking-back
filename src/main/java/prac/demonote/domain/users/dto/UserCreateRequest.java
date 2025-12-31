package prac.demonote.domain.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import prac.demonote.global.security.oauth2.OAuth2Provider;

public record UserCreateRequest(
    String email,

    @NotNull
    OAuth2Provider provider,

    @NotBlank
    String providerId) {

}
