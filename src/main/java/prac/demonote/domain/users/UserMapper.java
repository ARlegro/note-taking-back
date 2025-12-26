package prac.demonote.domain.users;

import org.mapstruct.Mapper;
import prac.demonote.domain.users.dto.UserCreateRequest;
import prac.demonote.domain.users.dto.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserCreateRequest request);

    UserResponse toResponse(User user);
}
