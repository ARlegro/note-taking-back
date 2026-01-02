package prac.demonote.domain.users.dto;

import java.util.UUID;
import prac.demonote.global.security.oauth2.OAuth2Provider;

public record UserResponse(UUID id, String email, OAuth2Provider provider, String providerId) {

}
