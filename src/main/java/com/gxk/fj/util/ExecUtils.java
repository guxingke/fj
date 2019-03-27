package com.gxk.fj.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class ExecUtils {

  private static final Logger log = LoggerFactory.getLogger(ExecUtils.class);

  public static String execLocal(File dir, String... cmd) {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      new ProcessExecutor()
        .directory(dir)
        .command(cmd)
        .destroyOnExit()
        .readOutput(true)
        .redirectOutput(os)
        .redirectError(Slf4jStream.of(log).asError())
        .timeout(1, TimeUnit.MINUTES)
        .execute();
      return os.toString("UTF-8");
    } catch (IOException | InterruptedException | TimeoutException e) {
      log.error("some error ", e);
    }
    return null;
  }
}
