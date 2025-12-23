package prac.demonote.domain.users;

import prac.demonote.domain.users.dto.UserCreateRequest;
import prac.demonote.domain.users.dto.UserResponse;

import java.util.UUID;

public interface UserService {
    UserResponse getUser(UUID userId);

    UserResponse createUser(UserCreateRequest request);
}
