package prac.demonote.global.security.oauth2.userinfo;

import java.util.Map;
import prac.demonote.global.security.oauth2.OAuth2Provider;

public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    @SuppressWarnings("unchecked")
    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        // Naver returns user info inside "response" object
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        this.attributes = response != null ? response : attributes;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public OAuth2Provider getProvider() {
        return OAuth2Provider.NAVER;
    }
}
