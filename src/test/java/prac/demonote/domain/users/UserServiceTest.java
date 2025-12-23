package prac.demonote.domain.users;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import prac.demonote.domain.users.dto.UserRequest;
import prac.demonote.domain.users.dto.UserResponse;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void 사용자_조회하면_응답을_반환한다() {
        UUID userId = UUID.randomUUID();
        String email = "user@example.com";
        String provider = "GOOGLE";
        String providerId = "provider-id";
        User user = new User(email, provider, providerId);
        ReflectionTestUtils.setField(user, "id", userId);
        UserResponse response = new UserResponse(userId, email, provider, providerId);

        // given
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userMapper.toResponse(user)).willReturn(response);

        // when
        UserResponse result = userService.getUser(userId);

        // then
        assertThat(result).isEqualTo(response);
    }

    @Test
    void 사용자_생성하면_저장된_정보를_반환한다() {
        UUID userId = UUID.randomUUID();
        String email = "new@example.com";
        String provider = "GITHUB";
        String providerId = "gh-123";
        UserRequest request = new UserRequest(email, provider, providerId);
        User toSave = new User(email, provider, providerId);
        User saved = new User(email, provider, providerId);
        ReflectionTestUtils.setField(saved, "id", userId);
        UserResponse response = new UserResponse(userId, email, provider, providerId);

        // given
        given(userMapper.toEntity(request)).willReturn(toSave);
        given(userRepository.save(toSave)).willReturn(saved);
        given(userMapper.toResponse(saved)).willReturn(response);

        // when
        UserResponse result = userService.createUser(request);

        // then
        assertThat(result).isEqualTo(response);
    }

    @Test
    void 존재하지_않는_사용자를_조회하면_예외를_던진다() {
        UUID userId = UUID.randomUUID();

        // given
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> userService.getUser(userId))
                .isInstanceOf(UserNotFoundException.class);

        // then
        verifyNoInteractions(userMapper);
    }
}
