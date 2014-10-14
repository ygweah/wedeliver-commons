package us.wedeliver.commons.util;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.common.base.Supplier;

public class ScheduledThreadPoolExecutorProvider implements Provider<ScheduledThreadPoolExecutor> {
  private String name;
  private int priority;
  private Supplier<ScheduledThreadPoolExecutor> executorSupplier;
  private ExecutorServiceSupport executorServiceSupport;

  @Inject
  public ScheduledThreadPoolExecutorProvider(String name,
                                             int priority,
                                             Supplier<ScheduledThreadPoolExecutor> executorSupplier,
                                             ExecutorServiceSupport executorServiceSupport) {
    this.name = name;
    this.priority = priority;
    this.executorSupplier = executorSupplier;
    this.executorServiceSupport = executorServiceSupport;
  }

  @Inject
  public ScheduledThreadPoolExecutorProvider(String name,
                                             int priority,
                                             final int corePoolSize,
                                             ExecutorServiceSupport executorServiceSupport) {
    this.name = name;
    this.priority = priority;
    this.executorSupplier = new Supplier<ScheduledThreadPoolExecutor>() {

      @Override
      public ScheduledThreadPoolExecutor get() {
        return new ScheduledThreadPoolExecutor(corePoolSize);
      }
    };
    this.executorServiceSupport = executorServiceSupport;
  }

  @Override
  public ScheduledThreadPoolExecutor get() {
    ScheduledThreadPoolExecutor executor = executorSupplier.get();
    executorServiceSupport.registerForShutdown(name, priority, executor);
    return executor;
  }

}
