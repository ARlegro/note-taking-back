package prac.demonote.global.security.oauth2.exception;

public class InvalidOAuth2StateException extends OAuth2AuthenticationException {

    public InvalidOAuth2StateException() {
        super("OAuth2_001", "Invalid or expired OAuth2 state");
    }
}
