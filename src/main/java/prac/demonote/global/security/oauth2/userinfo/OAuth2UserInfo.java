package prac.demonote.global.security.oauth2.userinfo;

import prac.demonote.global.security.oauth2.OAuth2Provider;

public interface OAuth2UserInfo {
    String getProviderId();
    String getEmail();
    String getName();
    OAuth2Provider getProvider();
}
