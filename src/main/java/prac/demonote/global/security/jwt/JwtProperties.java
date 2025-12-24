package prac.demonote.global.security.jwt;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "jwt")
@Validated
public record JwtProperties(

    @NotBlank(message = "SecretKey는 필수입니다")
    String secretKey,

    long accessTokenExpiration,
    long refreshTokenExpiration) {

}
