package prac.demonote.domain.attachment.exception;

public class UnauthorizedAttachmentAccessException extends RuntimeException {

  public UnauthorizedAttachmentAccessException(String message) {
    super(message);
  }
}
