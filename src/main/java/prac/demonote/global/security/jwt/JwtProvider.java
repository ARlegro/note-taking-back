package prac.demonote.global.security.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtProvider {

  private final JwtProperties jwtProperties;
  private SecretKey secretKey;

  @PostConstruct
  private void init() {
    this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.secretKey()));
  }

  // 이메일 빼도 되고
  public String createAccessToken(UUID userId, String email) {
    return Jwts.builder()
        .subject(String.valueOf(userId))
        .claim("email", email)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration()))
        .signWith(secretKey)
        .compact();
  }

  public String createRefreshToken(UUID userId, String email) {
    return Jwts.builder()
        .subject(String.valueOf(userId))
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration()))
        .signWith(secretKey)
        .compact();
  }

  private Claims parseToken(String token){
    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public boolean isValidToken(String token) {
    try {
      parseToken(token);
      return true;
    } catch(JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  public UUID getUserIdFromToken(String token) {
    return UUID.fromString(parseToken(token).getSubject());
  }
}

