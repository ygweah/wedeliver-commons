package us.wedeliver.commons.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ExecutorServiceSupport {

  private ShutdownSupport shutdownSupport;

  @Inject
  public ExecutorServiceSupport(ShutdownSupport shutdownSupport) {
    this.shutdownSupport = shutdownSupport;
  }

  public ExecutorService registerForShutdown(final String name,
                                             final int priority,
                                             final ExecutorService executorService) {
    shutdownSupport.addShutdownHook(new Runnable() {

      @Override
      public String toString() {
        return name;
      }

      @Override
      public void run() {
        if (!executorService.isShutdown()) {
          executorService.shutdown();
          ExceptionUtil.unchecked(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
              executorService.awaitTermination(1L, TimeUnit.DAYS);
              return null;
            }
          });
        }
      }
    }, priority);
    return executorService;
  }

}
