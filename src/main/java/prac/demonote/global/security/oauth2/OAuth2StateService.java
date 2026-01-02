package prac.demonote.global.security.oauth2;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import prac.demonote.global.security.oauth2.exception.InvalidOAuth2StateException;

@Service
@RequiredArgsConstructor
public class OAuth2StateService {

    private final StringRedisTemplate redisTemplate;

    private static final String STATE_PREFIX = "oauth2:state:";
    private static final Duration STATE_TTL = Duration.ofMinutes(10);

    public String generateState(String provider) {
        String state = UUID.randomUUID().toString();
        String key = STATE_PREFIX + state;
        redisTemplate.opsForValue().set(key, provider, STATE_TTL);
        return state;
    }

    public String validateAndConsume(String state) {
        String key = STATE_PREFIX + state;
        String provider = redisTemplate.opsForValue().getAndDelete(key);

        if (provider == null) {
            throw new InvalidOAuth2StateException();
        }

        return provider;
    }
}
