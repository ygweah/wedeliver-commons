package us.wedeliver.commons.util;

import java.math.BigDecimal;

public class BaseX {

  private String baseChars;
  private long baseValue;

  public BaseX(String baseChars) {
    this.baseChars = baseChars;
  }

  public BaseX(String baseChars, int minDigits) {
    if (minDigits < 2)
      throw new IllegalArgumentException("Min digits must not be less than 2");

    this.baseChars = baseChars;
    this.baseValue = new BigDecimal(baseChars.length()).pow(minDigits - 1).longValue();
  }

  public String convert(Number number) {
    long remainder = number.longValue() + baseValue;
    if (remainder < 0)
      throw new IllegalArgumentException("Number must not be negative");

    StringBuilder sb = new StringBuilder();
    do {
      int idx = (int) remainder % baseChars.length();
      sb.insert(0, baseChars.charAt(idx));
    } while ((remainder /= baseChars.length()) > 0);
    return sb.toString();
  }

}
