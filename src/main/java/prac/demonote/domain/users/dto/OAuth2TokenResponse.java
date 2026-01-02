package prac.demonote.domain.users.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record OAuth2TokenResponse(
    String accessToken,
    String tokenType,
    Integer expiresIn,  // 문서에는 초단위로 보내준다고 나와있음(액세스 토큰 기준)
    String refreshToken,
    String scope  // 대소문자를 구분하는 문자열 목록 - 공백으로 구분됨
) {}

// 예시 - google 기준 {
//  "access_token": "1/fFAGRNJru1FTz70BzhT3Zg",
//  "expires_in": 3920,
//  "token_type": "Bearer",
//  "scope": "https://www.googleapis.com/auth/drive.metadata.readonly https://www.googleapis.com/auth/calendar.readonly",
//  "refresh_token": "1//xEoDL4iW3cxlI7yDbSRFYNG01kVKM2C-259HOF2aQbI"
//}