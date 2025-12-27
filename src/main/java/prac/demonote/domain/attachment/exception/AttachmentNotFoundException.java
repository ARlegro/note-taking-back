package prac.demonote.domain.attachment.exception;

public class AttachmentNotFoundException extends RuntimeException {

  public AttachmentNotFoundException(String message) {
    super(message);
  }
}
