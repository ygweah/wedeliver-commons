package us.wedeliver.commons.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractQueueRunner<T> implements Runnable {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private BlockingQueue<T> queue;
  private long shutdownTimeoutSeconds;

  public AbstractQueueRunner(BlockingQueue<T> queue, long shutdownTimeoutSeconds) {
    this.queue = queue;
    this.shutdownTimeoutSeconds = shutdownTimeoutSeconds;
  }

  @Override
  public void run() {
    logger.debug("Start");
    T t = null;
    try {
      while (!Thread.interrupted()) {
        logger.debug("Getting from queue ...");
        t = queue.take();

        logger.debug("Processing data ...");
        processSilently(t);
      }
    } catch (InterruptedException e) {
      logger.warn("Interrupted, terminating ...", e);
    }

    try {
      while ((t = queue.poll(shutdownTimeoutSeconds, TimeUnit.SECONDS)) != null) {
        logger.debug("Processing data before stop");
        processSilently(t);
      }
    } catch (InterruptedException e) {
      logger.warn("Interrupted, terminate", e);
    }
    logger.debug("Stop");
  }

  private void processSilently(T t) {
    try {
      process(t);
    } catch (Exception e) {
      logger.error("Processing error", e);
    }
  }

  protected abstract void process(T t) throws Exception;

  protected <E> void putWithoutInterruption(BlockingQueue<E> output, E element) {
    while (true) {
      try {
        output.put(element);
        break;
      } catch (InterruptedException e) {
        logger.warn("Interrupted, ignore", e);
        Thread.currentThread().interrupt();
      }
    }
  }

}
