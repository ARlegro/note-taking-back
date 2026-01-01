package prac.demonote.domain.tags.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidTagNameException extends RuntimeException {

  public InvalidTagNameException(String message) {
    super(message);
  }
}
