package prac.demonote.domain.users.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import prac.demonote.domain.users.User;
import prac.demonote.domain.users.UserMapper;
import prac.demonote.domain.users.UserRepository;
import prac.demonote.domain.users.dto.LoginResponse;
import prac.demonote.domain.users.dto.OAuth2AuthorizationRequest;
import prac.demonote.domain.users.dto.OAuth2TokenResponse;
import prac.demonote.domain.users.dto.UserResponse;
import prac.demonote.global.security.jwt.JwtProvider;
import prac.demonote.global.security.oauth2.OAuth2ClientService;
import prac.demonote.global.security.oauth2.OAuth2Provider;
import prac.demonote.global.security.oauth2.OAuth2StateService;
import prac.demonote.global.security.oauth2.exception.OAuth2AuthenticationException;
import prac.demonote.global.security.oauth2.userinfo.GoogleOAuth2UserInfo;
import prac.demonote.global.security.oauth2.userinfo.OAuth2UserInfo;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private OAuth2ClientService oAuth2ClientService;

    @Mock
    private OAuth2StateService stateService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void 유효한_provider로_AuthorizationUrl을_요청하면_URL과_state를_반환한다() {
        // given
        String provider = "google";
        String state = "random-state-123";
        String authUrl = "https://accounts.google.com/o/oauth2/v2/auth?client_id=...";

        when(stateService.generateState(provider)).thenReturn(state);
        when(oAuth2ClientService.getAuthorizationUrl(OAuth2Provider.GOOGLE, state)).thenReturn(authUrl);

        // when
        OAuth2AuthorizationRequest result = authService.getOAuth2AuthorizationUrl(provider);

        // then
        assertThat(result.authorizationUrl()).isEqualTo(authUrl);
        assertThat(result.state()).isEqualTo(state);
    }

    @Test
    void 유효한_code와_state로_callback을_처리하면_JWT와_사용자정보를_반환한다() {
        // given
        String provider = "google";
        String code = "auth-code-123";
        String state = "valid-state";
        UUID userId = UUID.randomUUID();

        OAuth2TokenResponse tokenResponse = new OAuth2TokenResponse(
            "provider-access-token", "Bearer", 3600, null, "email profile"
        );
        OAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(Map.of(
            "sub", "google-123",
            "email", "test@gmail.com",
            "name", "Test User"
        ));
        User user = new User("test@gmail.com", OAuth2Provider.GOOGLE, "google-123");
        UserResponse userResponse = new UserResponse(userId, "test@gmail.com", OAuth2Provider.GOOGLE, "google-123");

        when(stateService.validateAndConsume(state)).thenReturn(provider);
        when(oAuth2ClientService.exchangeCodeForToken(OAuth2Provider.GOOGLE, code)).thenReturn(tokenResponse);
        when(oAuth2ClientService.getUserInfo(OAuth2Provider.GOOGLE, "provider-access-token")).thenReturn(userInfo);
        when(userRepository.findByProviderAndProviderId(OAuth2Provider.GOOGLE, "google-123")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);
        when(jwtProvider.createAccessToken(any(), eq("test@gmail.com"))).thenReturn("jwt-access-token");
        when(jwtProvider.createRefreshToken(any(), eq("test@gmail.com"))).thenReturn("jwt-refresh-token");

        // when
        LoginResponse result = authService.processOAuth2Callback(provider, code, state);

        // then
        assertThat(result.accessToken()).isEqualTo("jwt-access-token");
        assertThat(result.refreshToken()).isEqualTo("jwt-refresh-token");
        assertThat(result.user()).isEqualTo(userResponse);
    }

    @Test
    void 신규_사용자인_경우_사용자를_생성한다() {
        // given
        String provider = "google";
        String code = "auth-code";
        String state = "state";

        OAuth2TokenResponse tokenResponse = new OAuth2TokenResponse(
            "access-token", "Bearer", 3600, null, "email"
        );
        OAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(Map.of(
            "sub", "new-user-id",
            "email", "new@gmail.com",
            "name", "New User"
        ));
        User newUser = new User("new@gmail.com", OAuth2Provider.GOOGLE, "new-user-id");

        when(stateService.validateAndConsume(state)).thenReturn(provider);
        when(oAuth2ClientService.exchangeCodeForToken(OAuth2Provider.GOOGLE, code)).thenReturn(tokenResponse);
        when(oAuth2ClientService.getUserInfo(OAuth2Provider.GOOGLE, "access-token")).thenReturn(userInfo);
        when(userRepository.findByProviderAndProviderId(OAuth2Provider.GOOGLE, "new-user-id")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        when(userMapper.toResponse(any())).thenReturn(new UserResponse(UUID.randomUUID(), "new@gmail.com", OAuth2Provider.GOOGLE, "new-user-id"));
        when(jwtProvider.createAccessToken(any(), any())).thenReturn("access");
        when(jwtProvider.createRefreshToken(any(), any())).thenReturn("refresh");

        // when
        authService.processOAuth2Callback(provider, code, state);

        // then
        verify(userRepository).save(any(User.class));
    }

    @Test
    void 기존_사용자인_경우_새로_생성하지_않는다() {
        // given
        String provider = "google";
        String code = "auth-code";
        String state = "state";
        UUID existingUserId = UUID.randomUUID();

        OAuth2TokenResponse tokenResponse = new OAuth2TokenResponse(
            "access-token", "Bearer", 3600, null, "email"
        );
        OAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(Map.of(
            "sub", "existing-user-id",
            "email", "existing@gmail.com",
            "name", "Existing User"
        ));
        User existingUser = new User("existing@gmail.com", OAuth2Provider.GOOGLE, "existing-user-id");

        when(stateService.validateAndConsume(state)).thenReturn(provider);
        when(oAuth2ClientService.exchangeCodeForToken(OAuth2Provider.GOOGLE, code)).thenReturn(tokenResponse);
        when(oAuth2ClientService.getUserInfo(OAuth2Provider.GOOGLE, "access-token")).thenReturn(userInfo);
        when(userRepository.findByProviderAndProviderId(OAuth2Provider.GOOGLE, "existing-user-id")).thenReturn(Optional.of(existingUser));
        when(userMapper.toResponse(existingUser)).thenReturn(new UserResponse(existingUserId, "existing@gmail.com", OAuth2Provider.GOOGLE, "existing-user-id"));
        when(jwtProvider.createAccessToken(any(), any())).thenReturn("access");
        when(jwtProvider.createRefreshToken(any(), any())).thenReturn("refresh");

        // when
        authService.processOAuth2Callback(provider, code, state);

        // then
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void 유효한_refreshToken으로_토큰을_갱신하면_새로운_토큰을_반환한다() {
        // given
        String refreshToken = "valid-refresh-token";
        UUID userId = UUID.randomUUID();
        User user = new User("test@gmail.com", OAuth2Provider.GOOGLE, "google-123");
        UserResponse userResponse = new UserResponse(userId, "test@gmail.com", OAuth2Provider.GOOGLE, "google-123");

        when(jwtProvider.isValidToken(refreshToken)).thenReturn(true);
        when(jwtProvider.getUserIdFromToken(refreshToken)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);
        when(jwtProvider.createAccessToken(any(), eq("test@gmail.com"))).thenReturn("new-access-token");
        when(jwtProvider.createRefreshToken(any(), eq("test@gmail.com"))).thenReturn("new-refresh-token");

        // when
        LoginResponse result = authService.refreshToken(refreshToken);

        // then
        assertThat(result.accessToken()).isEqualTo("new-access-token");
        assertThat(result.refreshToken()).isEqualTo("new-refresh-token");
        assertThat(result.user()).isEqualTo(userResponse);
    }

    @Test
    void 유효하지_않은_refreshToken으로_갱신하면_예외를_던진다() {
        // given
        String invalidToken = "invalid-refresh-token";
        when(jwtProvider.isValidToken(invalidToken)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(invalidToken))
            .isInstanceOf(OAuth2AuthenticationException.class)
            .hasMessageContaining("Invalid or expired refresh token");
    }
}
