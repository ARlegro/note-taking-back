package prac.demonote.domain.users.service;

import java.util.UUID;
import prac.demonote.domain.users.User;
import prac.demonote.domain.users.dto.UserCreateRequest;
import prac.demonote.domain.users.dto.UserResponse;
import prac.demonote.global.security.oauth2.userinfo.OAuth2UserInfo;

public interface UserService {

  UserResponse getUser(UUID userId);

  UserResponse createUser(UserCreateRequest request);

  User findOrCreateOAuthUser(OAuth2UserInfo userInfo);
}
