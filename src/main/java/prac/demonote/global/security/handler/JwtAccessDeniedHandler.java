package prac.demonote.global.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import prac.demonote.global.error.ErrorResponse;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper;


  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {

    log.warn("접근 권한이 없습니다. URI : {}", request.getRequestURI());
    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    ErrorResponse errorResponse = ErrorResponse.of("Autho_002", "접근 권한이 없습니다");
    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    //Auth_001	인증 실패 (로그인 안 됨 / 토큰 없음)
    //Auth_002	인가 실패 (권한 없음)
    //Auth_003	토큰 만료
    //Auth_004	토큰 위변조
  }
}
