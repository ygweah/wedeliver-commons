package us.wedeliver.commons.util;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ExecutorServiceSupport {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private ShutdownSupport shutdownSupport;

  @Inject
  public ExecutorServiceSupport(ShutdownSupport shutdownSupport) {
    this.shutdownSupport = shutdownSupport;
  }

  public void registerForShutdown(final String name,
                                  final int priority,
                                  final long shutdownTimeoutSeconds,
                                  final ExecutorService executorService) {
    registerExecutorService(name, priority, shutdownTimeoutSeconds, new WeakReference<>(executorService));
  }

  private void registerExecutorService(final String name,
                                       final int priority,
                                       final long shutdownTimeoutSeconds,
                                       final WeakReference<ExecutorService> executorServiceRef) {
    shutdownSupport.addShutdownHook(new Runnable() {

      @Override
      public String toString() {
        return name;
      }

      @Override
      public void run() {
        final ExecutorService executorService = executorServiceRef.get();
        if (executorService != null && !executorService.isShutdown()) {
          executorService.shutdown();
          ExceptionUtil.unchecked(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
              executorService.shutdownNow();
              if (!executorService.awaitTermination(shutdownTimeoutSeconds, TimeUnit.SECONDS))
                logger.error("Timeout {} seconds awaiting termination: {}", shutdownTimeoutSeconds, name);
              return null;
            }
          });
        }
      }
    }, priority);
  }

}
