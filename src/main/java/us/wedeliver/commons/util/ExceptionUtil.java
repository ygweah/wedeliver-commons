package us.wedeliver.commons.util;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionUtil {
  private static final Logger logger = LoggerFactory.getLogger(ExceptionUtil.class);

  public static final <V> V unchecked(Callable<V> callable) {
    try {
      return callable.call();
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static final <V> V nullify(Callable<V> callable) {
    try {
      return callable.call();
    } catch (Throwable t) {
      logger.warn("Exception silenced", t);
      return null;
    }
  }

}
