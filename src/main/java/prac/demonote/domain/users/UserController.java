package prac.demonote.domain.users;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prac.demonote.domain.users.dto.UserCreateRequest;
import prac.demonote.domain.users.dto.UserResponse;
import prac.demonote.domain.users.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/{userId}")
  public ResponseEntity<UserResponse> getUser(@PathVariable UUID userId) {
    UserResponse response = userService.getUser(userId);
    return ResponseEntity.ok(response);
  }

  @PostMapping
  public ResponseEntity<UserResponse> createUser(@RequestBody UserCreateRequest request) {
    UserResponse response = userService.createUser(request);
    return ResponseEntity.ok(response);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<Void> handleUserNotFound() {
    return ResponseEntity.notFound().build();
  }
}
