package us.wedeliver.commons.util;

import org.junit.Assert;
import org.junit.Test;

public class DistanceUnitTest {

  @Test
  public void error() throws InterruptedException {
    double d = DistanceUnit.KILOMETERS.convert(1, DistanceUnit.MILES);
    Assert.assertTrue(d == 1.6093);
  }

}
