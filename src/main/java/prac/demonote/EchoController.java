package prac.demonote;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("echo")
public class EchoController {

  @PostMapping("/message")
  public ResponseEntity<EchoResponseDTO> echo(@RequestBody EchoRequestDTO message) {
    log.info("Echo Message = {}", message.message());
    return ResponseEntity.ok(new EchoResponseDTO("성공데스"));
  }

  @GetMapping("/message")
  public ResponseEntity<EchoResponseDTO> echo() {
    log.info("Echo Message Get");
    return ResponseEntity.ok(new EchoResponseDTO("GET 성공데스"));
  }
}
