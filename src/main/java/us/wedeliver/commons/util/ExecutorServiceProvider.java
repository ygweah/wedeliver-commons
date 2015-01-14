package us.wedeliver.commons.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.common.base.Supplier;

public class ExecutorServiceProvider implements Provider<ExecutorService> {
  private String name;
  private int priority;
  private long shutdownTimeoutSeconds;
  private Supplier<ExecutorService> executorServiceSupplier;
  private ExecutorServiceSupport executorServiceSupport;

  @Inject
  public ExecutorServiceProvider(String name,
                                 int priority,
                                 long shutdownTimeoutSeconds,
                                 Supplier<ExecutorService> executorServiceSupplier,
                                 ExecutorServiceSupport executorServiceSupport) {
    this.name = name;
    this.priority = priority;
    this.shutdownTimeoutSeconds = shutdownTimeoutSeconds;
    this.executorServiceSupplier = executorServiceSupplier;
    this.executorServiceSupport = executorServiceSupport;
  }

  public ExecutorServiceProvider(String name,
                                 int priority,
                                 long shutdownTimeoutSeconds,
                                 final int nThreads,
                                 ExecutorServiceSupport executorServiceSupport) {
    this.name = name;
    this.priority = priority;
    this.shutdownTimeoutSeconds = shutdownTimeoutSeconds;
    this.executorServiceSupplier = new Supplier<ExecutorService>() {
      @Override
      public ExecutorService get() {
        return Executors.newFixedThreadPool(nThreads);
      }
    };
    this.executorServiceSupport = executorServiceSupport;
  }

  public ExecutorServiceProvider(String name,
                                 int priority,
                                 long shutdownTimeoutSeconds,
                                 ExecutorServiceSupport executorServiceSupport) {
    this.name = name;
    this.priority = priority;
    this.shutdownTimeoutSeconds = shutdownTimeoutSeconds;
    this.executorServiceSupplier = new Supplier<ExecutorService>() {
      @Override
      public ExecutorService get() {
        return Executors.newCachedThreadPool();
      }
    };
    this.executorServiceSupport = executorServiceSupport;
  }

  @Override
  public ExecutorService get() {
    ExecutorService executorService = executorServiceSupplier.get();
    executorServiceSupport.registerForShutdown(name, priority, shutdownTimeoutSeconds, executorService);
    return executorService;
  }

}
