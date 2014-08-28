package us.wedeliver.commons.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTest {
  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void error() throws InterruptedException {
    logger.error("Unit Test Failure", new RuntimeException("Intentional"));
    Thread.sleep(10000L);
  }

}
