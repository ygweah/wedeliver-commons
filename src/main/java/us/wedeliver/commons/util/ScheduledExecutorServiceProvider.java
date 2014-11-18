package us.wedeliver.commons.util;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.common.base.Supplier;

public class ScheduledExecutorServiceProvider implements Provider<ScheduledExecutorService> {
  private String name;
  private int priority;
  private Supplier<ScheduledExecutorService> executorSupplier;
  private ExecutorServiceSupport executorServiceSupport;

  @Inject
  public ScheduledExecutorServiceProvider(String name,
                                          int priority,
                                          Supplier<ScheduledExecutorService> executorSupplier,
                                          ExecutorServiceSupport executorServiceSupport) {
    this.name = name;
    this.priority = priority;
    this.executorSupplier = executorSupplier;
    this.executorServiceSupport = executorServiceSupport;
  }

  public ScheduledExecutorServiceProvider(String name,
                                          int priority,
                                          final int corePoolSize,
                                          ExecutorServiceSupport executorServiceSupport) {
    this.name = name;
    this.priority = priority;
    this.executorSupplier = new Supplier<ScheduledExecutorService>() {

      @Override
      public ScheduledExecutorService get() {
        return new ScheduledThreadPoolExecutor(corePoolSize);
      }
    };
    this.executorServiceSupport = executorServiceSupport;
  }

  @Override
  public ScheduledExecutorService get() {
    ScheduledExecutorService executor = executorSupplier.get();
    executorServiceSupport.registerForShutdown(name, priority, executor);
    return executor;
  }

}
