package prac.demonote.domain.attachment.exception;

public class InvalidAttachmentException extends RuntimeException{
  public InvalidAttachmentException(String message) {
    super(message);
  }
}
