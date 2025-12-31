package prac.demonote.domain.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import prac.demonote.domain.users.dto.LoginResponse;
import prac.demonote.domain.users.dto.OAuth2AuthorizationRequest;
import prac.demonote.domain.users.dto.RefreshTokenRequest;
import prac.demonote.domain.users.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/oauth2/{provider}")
    @Operation(summary = "OAuth2 로그인 시작", description = "Provider의 Authorization URL을 반환합니다")
    public ResponseEntity<OAuth2AuthorizationRequest> getOAuth2AuthorizationUrl(
        @PathVariable String provider
    ) {
        OAuth2AuthorizationRequest request = authService.getOAuth2AuthorizationUrl(provider);
        return ResponseEntity.ok(request);
    }

    @GetMapping("/oauth2/callback/{provider}")
    @Operation(summary = "OAuth2 Callback", description = "Authorization Code를 처리하고 JWT를 발급합니다")
    public ResponseEntity<LoginResponse> handleOAuth2Callback(
        @PathVariable String provider,
        @RequestParam String code,
        @RequestParam String state
    ) {
        LoginResponse response = authService.processOAuth2Callback(provider, code, state);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Token 갱신", description = "Refresh Token으로 새로운 Access Token을 발급합니다")
    public ResponseEntity<LoginResponse> refreshToken(
        @Valid @RequestBody RefreshTokenRequest request
    ) {
        LoginResponse response = authService.refreshToken(request.refreshToken());
        return ResponseEntity.ok(response);
    }
}
