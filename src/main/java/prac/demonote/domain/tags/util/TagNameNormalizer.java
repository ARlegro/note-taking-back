package prac.demonote.domain.tags.util;

import java.util.regex.Pattern;
import prac.demonote.domain.tags.exception.InvalidTagNameException;

public class TagNameNormalizer {

  private static final int MAX_LENGTH = 50;
  private static final Pattern VALID_CHARS_PATTERN = Pattern.compile("[^a-z0-9가-힣]");

  private TagNameNormalizer() {
  }

  public static String normalize(String tagName) {
    if (tagName == null || tagName.isBlank()) {
      throw new InvalidTagNameException("태그 이름은 비어있을 수 없습니다.");
    }

    String normalized = tagName.trim().toLowerCase();
    normalized = VALID_CHARS_PATTERN.matcher(normalized).replaceAll("");

    if (normalized.isEmpty()) {
      throw new InvalidTagNameException("유효한 문자가 없는 태그 이름입니다.");
    }

    if (normalized.length() > MAX_LENGTH) {
      throw new InvalidTagNameException("태그 이름은 " + MAX_LENGTH + "자를 초과할 수 없습니다.");
    }

    return normalized;
  }
}
