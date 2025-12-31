package prac.demonote.global.security.oauth2.exception;

public class OAuth2ProviderException extends OAuth2AuthenticationException {

    public OAuth2ProviderException(String provider, String message) {
        super("OAuth2_002", "OAuth2 provider error [" + provider + "]: " + message);
    }

    public OAuth2ProviderException(String provider, String message, Throwable cause) {
        super("OAuth2_002", "OAuth2 provider error [" + provider + "]: " + message, cause);
    }
}
