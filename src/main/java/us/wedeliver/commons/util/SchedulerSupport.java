package us.wedeliver.commons.util;

import it.sauronsoftware.cron4j.Scheduler;

import java.lang.ref.WeakReference;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SchedulerSupport {

  private ShutdownSupport shutdownSupport;

  @Inject
  public SchedulerSupport(ShutdownSupport shutdownSupport) {
    this.shutdownSupport = shutdownSupport;
  }

  public void registerForShutdown(final String name, final int priority, final Scheduler scheduler) {
    registerExecutorService(name, priority, new WeakReference<>(scheduler));
  }

  private void registerExecutorService(final String name,
                                       final int priority,
                                       final WeakReference<Scheduler> schedulerRef) {
    shutdownSupport.addShutdownHook(new Runnable() {

      @Override
      public String toString() {
        return name;
      }

      @Override
      public void run() {
        final Scheduler scheduler = schedulerRef.get();
        if (scheduler != null && scheduler.isStarted())
          scheduler.stop();
      }
    }, priority);
  }

}
