package prac.demonote.global.security;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import prac.demonote.domain.users.User;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

  private final UUID userId;
  private final String email;
  private final Role role;

  public static CustomUserDetails from(User user){
    return new CustomUserDetails(
         user.getId(),
        user.getEmail(),
        Role.ROLE_USER// 일단은 ADMIN 필요 없으니
    );
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(role.name()));
  }

  @Override
  public @Nullable String getPassword() {
    return email;
  }

  @Override
  public String getUsername() {
    return String.valueOf(userId);
  }
}
