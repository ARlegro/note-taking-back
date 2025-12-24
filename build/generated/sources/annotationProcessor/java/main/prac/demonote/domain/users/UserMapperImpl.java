package prac.demonote.domain.users;

import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import prac.demonote.domain.users.dto.UserCreateRequest;
import prac.demonote.domain.users.dto.UserResponse;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-24T15:47:15+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.2.1.jar, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(UserCreateRequest request) {
        if ( request == null ) {
            return null;
        }

        User user = new User();

        return user;
    }

    @Override
    public UserResponse toResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UUID id = null;
        String email = null;
        String provider = null;
        String providerId = null;

        id = user.getId();
        email = user.getEmail();
        provider = user.getProvider();
        providerId = user.getProviderId();

        UserResponse userResponse = new UserResponse( id, email, provider, providerId );

        return userResponse;
    }
}
