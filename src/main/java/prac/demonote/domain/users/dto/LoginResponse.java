package prac.demonote.domain.users.dto;

public record LoginResponse(
    String accessToken,
    String refreshToken,
    UserResponse user
) {}
