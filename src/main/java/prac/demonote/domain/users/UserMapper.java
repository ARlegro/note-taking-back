package prac.demonote.domain.users;

import org.mapstruct.Mapper;
import prac.demonote.domain.users.dto.UserRequest;
import prac.demonote.domain.users.dto.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRequest request);

    UserResponse toResponse(User user);
}
