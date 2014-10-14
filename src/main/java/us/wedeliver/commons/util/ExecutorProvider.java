package us.wedeliver.commons.util;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.common.base.Supplier;

public class ExecutorProvider implements Provider<Executor> {
  private String name;
  private int priority;
  private Supplier<ExecutorService> executorServiceSupplier;
  private ExecutorServiceSupport executorServiceSupport;

  @Inject
  public ExecutorProvider(String name,
                          int priority,
                          Supplier<ExecutorService> executorServiceSupplier,
                          ExecutorServiceSupport executorServiceSupport) {
    this.name = name;
    this.priority = priority;
    this.executorServiceSupplier = executorServiceSupplier;
    this.executorServiceSupport = executorServiceSupport;
  }

  public ExecutorProvider(String name, int priority, final int nThreads, ExecutorServiceSupport executorServiceSupport) {
    this.name = name;
    this.priority = priority;
    this.executorServiceSupplier = new Supplier<ExecutorService>() {
      @Override
      public ExecutorService get() {
        return Executors.newFixedThreadPool(nThreads);
      }
    };
    this.executorServiceSupport = executorServiceSupport;
  }

  public ExecutorProvider(String name, int priority, ExecutorServiceSupport executorServiceSupport) {
    this.name = name;
    this.priority = priority;
    this.executorServiceSupplier = new Supplier<ExecutorService>() {
      @Override
      public ExecutorService get() {
        return Executors.newCachedThreadPool();
      }
    };
    this.executorServiceSupport = executorServiceSupport;
  }

  @Override
  public Executor get() {
    ExecutorService executorService = executorServiceSupplier.get();
    executorServiceSupport.registerForShutdown(name, priority, executorService);
    return executorService;
  }

}
