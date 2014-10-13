package us.wedeliver.commons.util;

import java.util.Properties;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

public class GuiceTest {
  private static final Logger logger = LoggerFactory.getLogger(GuiceTest.class);

  public static Injector injector;

  static {
    injector = ExceptionUtil.unchecked(new Callable<Injector>() {

      @Override
      public Injector call() throws Exception {
        Properties properties = PropertiesUtil.load("/junit.properties");
        logger.info("Test properties loaded: {}", properties);

        String moduleNames = properties.getProperty("junit.guice.modules");
        String propertiesNames = properties.getProperty("junit.guice.properties");
        String overrideModuleNames = properties.getProperty("junit.guice.override_modules");
        String overridePropertiesNames = properties.getProperty("junit.guice.override_properties");

        return GuiceUtil.createInjector(moduleNames, propertiesNames, overrideModuleNames, overridePropertiesNames);
      }
    });

    Runtime.getRuntime().addShutdownHook(new Thread() {

      @Override
      public void run() {
        ShutdownSupport shutdownSupport = injector.getInstance(ShutdownSupport.class);
        shutdownSupport.shutdown();
      }
    });
  }

  public <T> T find(Class<T> type) {
    return injector.getInstance(type);
  }

}
