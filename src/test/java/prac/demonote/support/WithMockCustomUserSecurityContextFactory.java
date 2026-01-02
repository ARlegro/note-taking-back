package prac.demonote.support;

import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import prac.demonote.global.security.CustomUserDetails;
import prac.demonote.global.security.Role;

public class WithMockCustomUserSecurityContextFactory
    implements WithSecurityContextFactory<WithMockCustomUser> {

  @Override
  public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();

    CustomUserDetails principal = new CustomUserDetails(
        UUID.fromString(annotation.userId()),
        annotation.email(),
        Role.valueOf(annotation.role())
    );

    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

    context.setAuthentication(auth);
    return context;
  }
}
