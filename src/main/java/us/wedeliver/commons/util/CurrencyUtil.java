package us.wedeliver.commons.util;

import java.math.BigDecimal;

public class CurrencyUtil {

  public static final BigDecimal round(BigDecimal amount) {
    return amount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
  }

}
