package us.wedeliver.commons.util;

import java.util.concurrent.Callable;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class GuiceTest {
  public static final Injector INJECTOR;

  static {
    INJECTOR = ExceptionUtil.unchecked(new Callable<Injector>() {

      @Override
      public Injector call() throws Exception {
        @SuppressWarnings("unchecked")
        Class<? extends Module> clazz = (Class<? extends Module>) Class.forName(PropertiesUtil
            .load("/junit.properties").getProperty("junit.guice.module"));
        Module module = clazz.newInstance();
        return Guice.createInjector(module);
      }
    });

    Runtime.getRuntime().addShutdownHook(new Thread() {

      @Override
      public void run() {
        ShutdownSupport shutdownSupport = INJECTOR.getInstance(ShutdownSupport.class);
        shutdownSupport.shutdown();
      }
    });
  }

  public <T> T find(Class<T> type) {
    return INJECTOR.getInstance(type);
  }

}
