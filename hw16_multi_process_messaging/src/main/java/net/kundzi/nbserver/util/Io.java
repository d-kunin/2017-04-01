package net.kundzi.nbserver.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class Io {

  private final static Logger LOG = LoggerFactory.getLogger(Io.class);

  public static void closeExecutorService(ExecutorService executorService) {
    try {
      executorService.shutdown();
      final boolean goodTermination = executorService.awaitTermination(5000, TimeUnit.MILLISECONDS);
      LOG.info("Termination {} for executor {}", goodTermination, executorService);
      if (!goodTermination) {
        final List<Runnable> runnables = executorService.shutdownNow();
        LOG.info("Could not shutdown {}", runnables);
        executorService.awaitTermination(5000, TimeUnit.MILLISECONDS);
      }
    } catch (InterruptedException e) {
      LOG.error("Failed to shutdown ExecutorService {}", executorService.toString(), e);
    }
  }
}
