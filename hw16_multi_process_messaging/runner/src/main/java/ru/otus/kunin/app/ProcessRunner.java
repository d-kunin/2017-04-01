package ru.otus.kunin.app;


import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by tully.
 */
public class ProcessRunner {
  private final StringBuffer out = new StringBuffer();
  private Process process;


  public void start(String loggerName, String command) throws IOException {
    process = runProcess(loggerName, command);
  }

  public void stop() {
    process.destroy();
  }

  public String getOutput() {
    return out.toString();
  }

  private Process runProcess(String loggerName, String command) throws IOException {
    ProcessBuilder pb = new ProcessBuilder(command.split(" "));
    pb.redirectErrorStream(true);
    Process p = pb.start();

    StreamListener errors = new StreamListener(loggerName, p.getErrorStream());
    StreamListener output = new StreamListener(loggerName, p.getInputStream());

    output.start();
    errors.start();
    return p;
  }

  private class StreamListener extends Thread {
    private final org.slf4j.Logger log;

    private final InputStream is;

    private StreamListener(String loggerName, InputStream type) {
      this.is = type;
      this.log = LoggerFactory.getLogger(loggerName);
    }

    @Override
    public void run() {
      try (InputStreamReader isr = new InputStreamReader(is)) {
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
          log.info(line);
        }
      } catch (IOException e) {
        log.error(e.getMessage());
      }
    }
  }
}
