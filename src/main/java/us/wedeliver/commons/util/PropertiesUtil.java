package us.wedeliver.commons.util;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {
  private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

  public static final Properties load(String resource) {
    Properties properties = new Properties();
    try {
      logger.debug("Loading properties from " + resource);
      properties.load(PropertiesUtil.class.getResourceAsStream(resource));
    } catch (IOException e) {
      throw new RuntimeException("Error loading properties from " + resource, e);
    }
    return properties;
  }

}
