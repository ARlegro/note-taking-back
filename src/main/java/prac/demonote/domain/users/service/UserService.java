package prac.demonote.domain.users.service;

import java.util.UUID;
import prac.demonote.domain.users.dto.UserCreateRequest;
import prac.demonote.domain.users.dto.UserResponse;

public interface UserService {

  UserResponse getUser(UUID userId);

  UserResponse createUser(UserCreateRequest request);
}
