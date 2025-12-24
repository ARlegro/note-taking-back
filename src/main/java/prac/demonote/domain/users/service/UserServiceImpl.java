package prac.demonote.domain.users.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import prac.demonote.domain.users.User;
import prac.demonote.domain.users.UserMapper;
import prac.demonote.domain.users.UserNotFoundException;
import prac.demonote.domain.users.UserRepository;
import prac.demonote.domain.users.dto.UserCreateRequest;
import prac.demonote.domain.users.dto.UserResponse;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public UserResponse getUser(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
    return userMapper.toResponse(user);
  }

  @Override
  public UserResponse createUser(UserCreateRequest request) {
    User user = userMapper.toEntity(request);
    User savedUser = userRepository.save(user);
    return userMapper.toResponse(savedUser);
  }
}
