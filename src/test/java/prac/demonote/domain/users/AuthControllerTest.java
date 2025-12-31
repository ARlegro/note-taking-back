package prac.demonote.domain.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import prac.demonote.domain.users.dto.LoginResponse;
import prac.demonote.domain.users.dto.OAuth2AuthorizationRequest;
import prac.demonote.domain.users.dto.RefreshTokenRequest;
import prac.demonote.domain.users.dto.UserResponse;
import prac.demonote.domain.users.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void 유효한_provider로_OAuth2_AuthorizationUrl을_요청하면_200을_반환한다() {
        // given
        String provider = "google";
        String authUrl = "https://accounts.google.com/o/oauth2/v2/auth?client_id=...";
        String state = "random-state";

        when(authService.getOAuth2AuthorizationUrl(provider))
            .thenReturn(new OAuth2AuthorizationRequest(authUrl, state));

        // when
        ResponseEntity<OAuth2AuthorizationRequest> response = authController.getOAuth2AuthorizationUrl(provider);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().authorizationUrl()).isEqualTo(authUrl);
        assertThat(response.getBody().state()).isEqualTo(state);
    }

    @Test
    void 유효한_callback을_처리하면_JWT_토큰을_반환한다() {
        // given
        String provider = "google";
        String code = "auth-code";
        String state = "valid-state";
        UUID userId = UUID.randomUUID();

        UserResponse userResponse = new UserResponse(userId, "test@gmail.com", "google", "google-123");
        LoginResponse loginResponse = new LoginResponse("access-token", "refresh-token", userResponse);

        when(authService.processOAuth2Callback(provider, code, state))
            .thenReturn(loginResponse);

        // when
        ResponseEntity<LoginResponse> response = authController.handleOAuth2Callback(provider, code, state);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().accessToken()).isEqualTo("access-token");
        assertThat(response.getBody().refreshToken()).isEqualTo("refresh-token");
        assertThat(response.getBody().user().email()).isEqualTo("test@gmail.com");
    }

    @Test
    void 유효한_refreshToken으로_토큰을_갱신하면_새로운_토큰을_반환한다() {
        // given
        UUID userId = UUID.randomUUID();
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
        UserResponse userResponse = new UserResponse(userId, "test@gmail.com", "google", "google-123");
        LoginResponse loginResponse = new LoginResponse("new-access-token", "new-refresh-token", userResponse);

        when(authService.refreshToken("valid-refresh-token"))
            .thenReturn(loginResponse);

        // when
        ResponseEntity<LoginResponse> response = authController.refreshToken(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().accessToken()).isEqualTo("new-access-token");
        assertThat(response.getBody().refreshToken()).isEqualTo("new-refresh-token");
    }

    @Test
    void kakao_provider로_OAuth2_AuthorizationUrl을_요청하면_200을_반환한다() {
        // given
        String provider = "kakao";
        String authUrl = "https://kauth.kakao.com/oauth/authorize?client_id=...";
        String state = "random-state";

        when(authService.getOAuth2AuthorizationUrl(provider))
            .thenReturn(new OAuth2AuthorizationRequest(authUrl, state));

        // when
        ResponseEntity<OAuth2AuthorizationRequest> response = authController.getOAuth2AuthorizationUrl(provider);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().authorizationUrl()).isEqualTo(authUrl);
        assertThat(response.getBody().state()).isEqualTo(state);
    }
}
