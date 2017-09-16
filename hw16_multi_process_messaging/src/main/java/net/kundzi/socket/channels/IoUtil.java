package net.kundzi.socket.channels;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class IoUtil {

  private final static Logger LOG = LoggerFactory.getLogger(IoUtil.class);

  public static void closeExecutorService(ExecutorService executorService) {
    try {
      executorService.shutdown();
      final boolean goodTermination = executorService.awaitTermination(5000, TimeUnit.MILLISECONDS);
      LOG.info("Termination {} for executor {}", goodTermination, executorService);
    } catch (InterruptedException e) {
      LOG.error("Failed to shutdown ExecutorService {}", executorService.toString(), e);
    }
  }
}
