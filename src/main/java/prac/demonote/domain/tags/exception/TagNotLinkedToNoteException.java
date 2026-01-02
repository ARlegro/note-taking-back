package prac.demonote.domain.tags.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TagNotLinkedToNoteException extends RuntimeException {

  public TagNotLinkedToNoteException(String message) {
    super(message);
  }
}
