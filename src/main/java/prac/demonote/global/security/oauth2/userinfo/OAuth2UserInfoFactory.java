package prac.demonote.global.security.oauth2.userinfo;

import java.util.Map;
import org.springframework.stereotype.Component;
import prac.demonote.global.security.oauth2.OAuth2Provider;

@Component
public class OAuth2UserInfoFactory {

    public OAuth2UserInfo create(OAuth2Provider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
            case KAKAO -> new KakaoOAuth2UserInfo(attributes);
        };
    }
}
