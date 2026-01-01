package prac.demonote.domain.tags.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import prac.demonote.domain.tags.exception.InvalidTagNameException;

class TagNameNormalizerTest {

  @Nested
  @DisplayName("정규화 성공 케이스")
  class NormalizeSuccess {

    @Test
    void 대문자를_소문자로_변환한다() {
      // given
      String input = "HELLO";

      // when
      String result = TagNameNormalizer.normalize(input);

      // then
      assertThat(result).isEqualTo("hello");
    }

    @Test
    void 공백을_제거한다() {
      // given
      String input = "hello world";

      // when
      String result = TagNameNormalizer.normalize(input);

      // then
      assertThat(result).isEqualTo("helloworld");
    }

    @Test
    void 특수문자를_제거한다() {
      // given
      String input = "hello@#$world";

      // when
      String result = TagNameNormalizer.normalize(input);

      // then
      assertThat(result).isEqualTo("helloworld");
    }

    @Test
    void 한글_태그이름을_정규화한다() {
      // given
      String input = "프로그래밍 공부";

      // when
      String result = TagNameNormalizer.normalize(input);

      // then
      assertThat(result).isEqualTo("프로그래밍공부");
    }

    @Test
    void 영문_숫자_한글_혼합_태그를_정규화한다() {
      // given
      String input = "Java21 공부!";

      // when
      String result = TagNameNormalizer.normalize(input);

      // then
      assertThat(result).isEqualTo("java21공부");
    }

    @Test
    void 앞뒤_공백을_제거한다() {
      // given
      String input = "  hello  ";

      // when
      String result = TagNameNormalizer.normalize(input);

      // then
      assertThat(result).isEqualTo("hello");
    }

    @Test
    void 숫자만_있는_태그도_허용한다() {
      // given
      String input = "2024";

      // when
      String result = TagNameNormalizer.normalize(input);

      // then
      assertThat(result).isEqualTo("2024");
    }
  }

  @Nested
  @DisplayName("정규화 실패 케이스")
  class NormalizeFailure {

    @Test
    void null_입력시_예외를_발생시킨다() {
      // given
      String input = null;

      // when & then
      assertThatThrownBy(() -> TagNameNormalizer.normalize(input))
          .isInstanceOf(InvalidTagNameException.class);
    }

    @Test
    void 빈문자열_입력시_예외를_발생시킨다() {
      // given
      String input = "";

      // when & then
      assertThatThrownBy(() -> TagNameNormalizer.normalize(input))
          .isInstanceOf(InvalidTagNameException.class);
    }

    @Test
    void 공백만_있는_입력시_예외를_발생시킨다() {
      // given
      String input = "   ";

      // when & then
      assertThatThrownBy(() -> TagNameNormalizer.normalize(input))
          .isInstanceOf(InvalidTagNameException.class);
    }

    @Test
    void 정규화_후_빈문자열이면_예외를_발생시킨다() {
      // given
      String input = "@#$%^&*()";

      // when & then
      assertThatThrownBy(() -> TagNameNormalizer.normalize(input))
          .isInstanceOf(InvalidTagNameException.class);
    }

    @Test
    void 태그이름이_50자_초과시_예외를_발생시킨다() {
      // given
      String input = "a".repeat(51);

      // when & then
      assertThatThrownBy(() -> TagNameNormalizer.normalize(input))
          .isInstanceOf(InvalidTagNameException.class);
    }
  }
}
