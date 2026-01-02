package prac.demonote.domain.users.dto;

public record OAuth2AuthorizationRequest(
    String authorizationUrl,
    String state
) {}
