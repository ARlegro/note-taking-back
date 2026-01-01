package prac.demonote.support;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
  String userId() default "550e8400-e29b-41d4-a716-446655440000";
  String email() default "test@example.com";
  String role() default "ROLE_USER";
}
