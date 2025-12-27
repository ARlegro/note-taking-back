package prac.demonote.domain.users;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import prac.demonote.domain.users.dto.UserCreateRequest;
import prac.demonote.domain.users.dto.UserResponse;
import prac.demonote.domain.users.service.UserService;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    private static final String BASE_URL = "/api/users";
    private static final MediaType JSON = MediaType.APPLICATION_JSON;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void 유효한_사용자_생성_요청() throws Exception {
        String endpoint = BASE_URL;
        UUID userId = UUID.randomUUID();
        String email = "new@example.com";
        String provider = "GITHUB";
        String providerId = "gh-123";
        UserCreateRequest request = new UserCreateRequest(email, provider, providerId);
        UserResponse response = new UserResponse(userId, email, provider, providerId);

        // given
        given(userService.createUser(any(UserCreateRequest.class))).willReturn(response);

        // when
        ResultActions result = mockMvc.perform(post(endpoint)
                .contentType(JSON)
                .content(jsonMapper.writeValueAsString(request))
                .accept(JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.provider").value(provider))
                .andExpect(jsonPath("$.providerId").value(providerId));

        ArgumentCaptor<UserCreateRequest> captor = ArgumentCaptor.forClass(UserCreateRequest.class);
        then(userService).should().createUser(captor.capture());
        assertThat(captor.getValue()).isEqualTo(request);
    }

    @Test
    void 유효한_사용자를_조회하면_200을_반환한다() throws Exception {
        String endpoint = BASE_URL + "/{userId}";
        UUID userId = UUID.randomUUID();
        String email = "user@example.com";
        String provider = "GOOGLE";
        String providerId = "provider-id";
        UserResponse response = new UserResponse(userId, email, provider, providerId);

        // given
        given(userService.getUser(userId)).willReturn(response);

        // when
        ResultActions result = mockMvc.perform(get(endpoint, userId)
                .accept(JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.provider").value(provider))
                .andExpect(jsonPath("$.providerId").value(providerId));
    }

    @Test
    void 존재하지_않는_사용자를_조회하면_404를_반환한다() throws Exception {
        String endpoint = BASE_URL + "/{userId}";
        UUID userId = UUID.randomUUID();

        // given
        given(userService.getUser(userId)).willThrow(new UserNotFoundException(userId));

        // when
        ResultActions result = mockMvc.perform(get(endpoint, userId));

        // then
        result.andExpect(status().isNotFound());
    }

    @Test
    void 유효하지_않은_ID로_조회하면_400을_반환한다() throws Exception {
        // given
        String endpoint = BASE_URL + "/{userId}";
        String invalidUserId = "invalid-uuid";

        // when
        ResultActions result = mockMvc.perform(get(endpoint, invalidUserId));

        // then
        result.andExpect(status().isBadRequest());
        then(userService).shouldHaveNoInteractions();
    }

    @Test
    void 사용자_생성_요청_본문이_없으면_400을_반환한다() throws Exception {
        // given
        String endpoint = BASE_URL;

        // when
        ResultActions result = mockMvc.perform(post(endpoint)
                .contentType(JSON)
                .accept(JSON));

        // then
        result.andExpect(status().isBadRequest());
        then(userService).shouldHaveNoInteractions();
    }

    @Test
    void 사용자_생성_요청이_JSON이_아니면_415를_반환한다() throws Exception {
        // given
        String endpoint = BASE_URL;
        MediaType plainText = MediaType.TEXT_PLAIN;
        String payload = "not-json";

        // when
        ResultActions result = mockMvc.perform(post(endpoint)
                .contentType(plainText)
                .content(payload)
                .accept(JSON));

        // then
        result.andExpect(status().isUnsupportedMediaType());
        then(userService).shouldHaveNoInteractions();
    }

    @Test
    void 사용자_생성_요청이_잘못된_JSON이면_400을_반환한다() throws Exception {
        // given
        String endpoint = BASE_URL;
        String invalidJson = "{\"email\": \"user@example.com\"";

        // when
        ResultActions result = mockMvc.perform(post(endpoint)
                .contentType(JSON)
                .content(invalidJson)
                .accept(JSON));

        // then
        result.andExpect(status().isBadRequest());
        then(userService).shouldHaveNoInteractions();
    }
}
