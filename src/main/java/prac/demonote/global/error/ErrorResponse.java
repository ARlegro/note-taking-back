package prac.demonote.global.error;


import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

  private final String code;
  private final String message;
  private final LocalDateTime timestamp;
  private final Map<String, String> errors;

  private ErrorResponse(String code, String message, Map<String, String> errors) {
    this.code = code;
    this.message = message;
    this.timestamp = LocalDateTime.now();
    this.errors = errors;
  }

  public static ErrorResponse of(String code, String message) {
    return new ErrorResponse(code, message, null);
  }

  public static ErrorResponse of(String code, String message, Map<String, String> errors) {
    return new ErrorResponse(code, message, errors);
  }
}

