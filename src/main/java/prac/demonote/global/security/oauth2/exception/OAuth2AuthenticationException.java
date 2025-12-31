package prac.demonote.global.security.oauth2.exception;

import lombok.Getter;

@Getter
public class OAuth2AuthenticationException extends RuntimeException {

    private final String errorCode;

    public OAuth2AuthenticationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public OAuth2AuthenticationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
