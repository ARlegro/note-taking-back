package prac.demonote.global.security.oauth2;

import java.util.Arrays;

public enum OAuth2Provider {
    GOOGLE("google"),
    KAKAO("kakao"),
    APPLE("apple"),
    NAVER("naver");

    private final String registrationId;

    OAuth2Provider(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public static OAuth2Provider fromString(String provider) {
        return Arrays.stream(values())
            .filter(p -> p.registrationId.equalsIgnoreCase(provider))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown provider: " + provider));
    }
}
