package prac.demonote.global.security;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import prac.demonote.domain.users.User;
import prac.demonote.domain.users.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {


  private final UserRepository userRepository;
  // 필요 시 레디스 캐시(나중에 고도화)

  @Override
  public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

    // todo : 캐시에서 찾기

    User user = userRepository.findById(UUID.fromString(userId))
        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));

    CustomUserDetails userDetails = CustomUserDetails.from(user);
    // todo : 캐시에 저장
    return userDetails;
  }
}
