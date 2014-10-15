package us.wedeliver.commons.util;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ShutdownSupport {
  public static final int HIGH_PRIORITY = 10;
  public static final int NORMAL_PRIORITY = 5;
  public static final int LOW_PRIORITY = 1;

  private Logger logger = LoggerFactory.getLogger(getClass());

  private SortedMap<Integer, Collection<Runnable>> hooksByPriority = new TreeMap<>(Collections.reverseOrder());

  @Inject
  public ShutdownSupport() {
  }

  public void addShutdownHook(Runnable shutdownHook, int priority) {
    logger.info("Adding shutdown hook [{}]: {}", priority, shutdownHook);
    Collection<Runnable> hooks = hooksByPriority.get(priority);
    if (hooks == null) {
      hooks = new LinkedList<>();
      hooksByPriority.put(priority, hooks);
    }
    hooks.add(shutdownHook);
  }

  public void shutdown() {
    logger.info("SHUTDWON ACALLLED ======================");
    for (Map.Entry<Integer, Collection<Runnable>> mapEntry : hooksByPriority.entrySet()) {
      Integer priority = mapEntry.getKey();
      for (Runnable shutdownHook : mapEntry.getValue()) {
        try {
          logger.info("Running shutdown hook [{}]: {}", priority, shutdownHook);
          shutdownHook.run();
        } catch (Exception e) {
          logger.error("Shutdown Hook Error: " + shutdownHook, e);
        }
      }
    }
    hooksByPriority.clear();
  }

}
