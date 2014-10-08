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
public class ShutdownUtil {
  private Logger logger = LoggerFactory.getLogger(getClass());

  private SortedMap<Integer, Collection<Runnable>> hooksByPriority = new TreeMap<>(Collections.reverseOrder());

  @Inject
  public ShutdownUtil() {
  }

  public void addShutdownHook(Runnable shutdownHook, int priority) {
    if (logger.isInfoEnabled())
      logger.info("Adding shutdown hook [{}]: {}", priority, shutdownHook);

    Collection<Runnable> hooks = hooksByPriority.get(priority);
    if (hooks == null) {
      hooks = new LinkedList<>();
      hooksByPriority.put(priority, hooks);
    }
    hooks.add(shutdownHook);
  }

  public void shutdown() {
    for (Map.Entry<Integer, Collection<Runnable>> mapEntry : hooksByPriority.entrySet()) {
      Integer priority = mapEntry.getKey();
      for (Runnable shutdownHook : mapEntry.getValue())
        try {
          if (logger.isInfoEnabled())
            logger.info("Running shutdown hook [{}]: {}", priority, shutdownHook);

          shutdownHook.run();
        } catch (Exception e) {
          logger.error("Shutdown Hook Error: " + shutdownHook, e);
        }
    }
  }

}
