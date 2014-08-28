package us.wedeliver.commons.util;

public class SQLUtil {

  /**
   * Encode SQL pattern for LIKE condition.
   */
  public static final String encodeLike(String value) {
    return "%" + escapeLike(value) + "%";
  }

  /**
   * Escape SQL pattern for LIKE condition.
   */
  public static final String escapeLike(String value) {
    return value.trim().replaceAll("\\\\", "\\\\\\\\").replaceAll("%", "\\\\%").replaceAll("_", "\\\\_")
        .replaceAll("\\s+", " ");
  }

}
