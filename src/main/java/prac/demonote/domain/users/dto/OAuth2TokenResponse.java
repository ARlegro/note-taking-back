package prac.demonote.domain.users.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record OAuth2TokenResponse(
    String accessToken,
    String tokenType,
    Integer expiresIn,
    String refreshToken,
    String scope
) {}
