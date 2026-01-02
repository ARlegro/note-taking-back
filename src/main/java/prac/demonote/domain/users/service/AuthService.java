package prac.demonote.domain.users.service;

import prac.demonote.domain.users.dto.LoginResponse;
import prac.demonote.domain.users.dto.OAuth2AuthorizationRequest;

public interface AuthService {

    OAuth2AuthorizationRequest getOAuth2AuthorizationUrl(String provider);

    LoginResponse processOAuth2Callback(String provider, String code, String state);

    LoginResponse refreshToken(String refreshToken);
}
