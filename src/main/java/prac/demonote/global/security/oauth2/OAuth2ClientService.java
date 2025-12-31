package prac.demonote.global.security.oauth2;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import prac.demonote.domain.users.dto.OAuth2TokenResponse;
import prac.demonote.global.security.oauth2.exception.OAuth2ProviderException;
import prac.demonote.global.security.oauth2.userinfo.OAuth2UserInfo;
import prac.demonote.global.security.oauth2.userinfo.OAuth2UserInfoFactory;

@Service
@RequiredArgsConstructor
public class OAuth2ClientService {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final RestClient restClient;
    private final OAuth2UserInfoFactory userInfoFactory;

    public String getAuthorizationUrl(OAuth2Provider provider, String state) {
        ClientRegistration registration = getClientRegistration(provider);

        return UriComponentsBuilder
            .fromUriString(registration.getProviderDetails().getAuthorizationUri())
            .queryParam("client_id", registration.getClientId())
            .queryParam("redirect_uri", resolveRedirectUri(registration))
            .queryParam("response_type", "code")
            .queryParam("scope", String.join(" ", registration.getScopes()))
            .queryParam("state", state)
            .build()
            .toUriString();
    }

    public OAuth2TokenResponse exchangeCodeForToken(OAuth2Provider provider, String code) {
        ClientRegistration registration = getClientRegistration(provider);

        // OAuth2 Token Request는 기본 스펙이 application/x-www-form-urlencoded 형식 이라 쩔수 DTO대신 MultivalueMap으로
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", registration.getClientId());
        params.add("client_secret", registration.getClientSecret());
        params.add("code", code);
        params.add("redirect_uri", resolveRedirectUri(registration));

        try {
            return restClient.post()
                .uri(registration.getProviderDetails().getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(params)
                .retrieve()
                .body(OAuth2TokenResponse.class);
        } catch (Exception e) {
            throw new OAuth2ProviderException(provider.getRegistrationId(),
                "Failed to exchange code for token", e);
        }
    }

    public OAuth2UserInfo getUserInfo(OAuth2Provider provider, String accessToken) {
        ClientRegistration registration = getClientRegistration(provider);

        try {
            Map<String, Object> attributes = restClient.get()
                .uri(registration.getProviderDetails().getUserInfoEndpoint().getUri())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

            return userInfoFactory.create(provider, attributes);
        } catch (Exception e) {
            throw new OAuth2ProviderException(provider.getRegistrationId(),
                "Failed to get user info", e);
        }
    }

    private ClientRegistration getClientRegistration(OAuth2Provider provider) {
        ClientRegistration registration = clientRegistrationRepository
            .findByRegistrationId(provider.getRegistrationId());

        if (registration == null) {
            throw new OAuth2ProviderException(provider.getRegistrationId(),
                "Client registration not found");
        }

        return registration;
    }

    private String resolveRedirectUri(ClientRegistration registration) {
        String redirectUri = registration.getRedirectUri();
        return redirectUri
            .replace("{baseUrl}", "http://localhost:8080") // todo 하드코딩 지우기
            .replace("{registrationId}", registration.getRegistrationId());
    }
}
