package prac.demonote.domain.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prac.demonote.domain.users.User;
import prac.demonote.domain.users.UserMapper;
import prac.demonote.domain.users.UserNotFoundException;
import prac.demonote.domain.users.UserRepository;
import prac.demonote.domain.users.dto.LoginResponse;
import prac.demonote.domain.users.dto.OAuth2AuthorizationRequest;
import prac.demonote.domain.users.dto.OAuth2TokenResponse;
import prac.demonote.global.security.jwt.JwtProvider;
import prac.demonote.global.security.oauth2.OAuth2ClientService;
import prac.demonote.global.security.oauth2.OAuth2Provider;
import prac.demonote.global.security.oauth2.OAuth2StateService;
import prac.demonote.global.security.oauth2.exception.OAuth2AuthenticationException;
import prac.demonote.global.security.oauth2.userinfo.OAuth2UserInfo;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final OAuth2ClientService oAuth2ClientService;
    private final OAuth2StateService stateService;
    private final UserMapper userMapper;

    @Override
    public OAuth2AuthorizationRequest getOAuth2AuthorizationUrl(String provider) {
        OAuth2Provider oAuth2Provider = OAuth2Provider.fromString(provider);
        String state = stateService.generateState(provider);
        String authorizationUrl = oAuth2ClientService.getAuthorizationUrl(oAuth2Provider, state);

        return new OAuth2AuthorizationRequest(authorizationUrl, state);
    }

    @Override
    @Transactional
    public LoginResponse processOAuth2Callback(String provider, String code, String state) {
        // 1. State 검증
        stateService.validateAndConsume(state);

        // 2. Provider 확인
        OAuth2Provider oAuth2Provider = OAuth2Provider.fromString(provider);

        // 3. Authorization Code로 Access Token 교환
        OAuth2TokenResponse tokenResponse = oAuth2ClientService.exchangeCodeForToken(oAuth2Provider, code);

        // 4. Access Token으로 사용자 정보 조회
        OAuth2UserInfo userInfo = oAuth2ClientService.getUserInfo(oAuth2Provider, tokenResponse.accessToken());

        // 5. 사용자 조회 또는 생성
        User user = findOrCreateUser(userInfo);

        // 6. JWT 토큰 발급
        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtProvider.createRefreshToken(user.getId(), user.getEmail());

        return new LoginResponse(
            accessToken,
            refreshToken,
            userMapper.toResponse(user)
        );
    }

    @Override
    @Transactional
    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtProvider.isValidToken(refreshToken)) {
            throw new OAuth2AuthenticationException("Auth_003", "Invalid or expired refresh token");
        }

        var userId = jwtProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        String newAccessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail());
        String newRefreshToken = jwtProvider.createRefreshToken(user.getId(), user.getEmail());

        return new LoginResponse(
            newAccessToken,
            newRefreshToken,
            userMapper.toResponse(user)
        );
    }

    // todo : 나누기
    private User findOrCreateUser(OAuth2UserInfo userInfo) {
        return userRepository.findByProviderAndProviderId(
                userInfo.getProvider(),
                userInfo.getProviderId()
            )
            .orElseGet(() -> userRepository.save(
                new User(
                    userInfo.getEmail(),
                    userInfo.getProvider(),
                    userInfo.getProviderId()
                )
            ));
    }
}
