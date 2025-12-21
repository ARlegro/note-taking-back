package prac.demonote;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record EchoRequestDTO(
    @NotBlank
    String message,

    @Min(value = 18, message = "18세 이상만 가입 가능합니다")
    int age) {

}
