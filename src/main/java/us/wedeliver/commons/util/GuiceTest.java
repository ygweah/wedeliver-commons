package us.wedeliver.commons.util;

import java.util.Properties;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

public class GuiceTest {
  private static final Logger logger = LoggerFactory.getLogger(GuiceTest.class);

  public static Injector injector;

  static {
    injector = ExceptionUtil.unchecked(new Callable<Injector>() {

      @Override
      public Injector call() throws Exception {
        Properties properties = PropertiesUtil.load("/junit.properties");
        logger.info("Properties loaded: {}", properties);

        String moduleNames = properties.getProperty("junit.guice.modules");
        String overrideModuleNames = properties.getProperty("junit.guice.override_modules");
        Module module = GuiceUtil.createModule(moduleNames, overrideModuleNames);
        PropertiesModule propertiesModule = new PropertiesModule("/guice.properties");
        return Guice.createInjector(Modules.combine(module, propertiesModule));
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
