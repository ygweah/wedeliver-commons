package us.wedeliver.commons.util;

import it.sauronsoftware.cron4j.Scheduler;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.common.base.Supplier;

public class SchedulerProvider implements Provider<Scheduler> {
  private String name;
  private int priority;
  private Supplier<Scheduler> schedulerSupplier;
  private SchedulerSupport schedulerSupport;

  @Inject
  public SchedulerProvider(String name,
                           int priority,
                           Supplier<Scheduler> schedulerSupplier,
                           SchedulerSupport schedulerSupport) {
    this.name = name;
    this.priority = priority;
    this.schedulerSupplier = schedulerSupplier;
    this.schedulerSupport = schedulerSupport;
  }

  public SchedulerProvider(String name, int priority, SchedulerSupport schedulerSupport) {
    this.name = name;
    this.priority = priority;
    this.schedulerSupplier = new Supplier<Scheduler>() {

      @Override
      public Scheduler get() {
        return new Scheduler();
      }
    };
    this.schedulerSupport = schedulerSupport;
  }

  @Override
  public Scheduler get() {
    Scheduler scheduler = schedulerSupplier.get();
    schedulerSupport.registerForShutdown(name, priority, scheduler);
    return scheduler;
  }

}
