package com.gxk.fj.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

public abstract class CmdUtils {

  private static final Logger log = LoggerFactory.getLogger(CmdUtils.class);

  public static boolean isHelpCmd(String... args) {
    if (args.length == 0) {
      return true;
    }
    return Arrays.asList("-h", "--help").contains(args[0]);
  }

  public static String execLocal() {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    try {
      new ProcessExecutor()
          .directory(new File("/Users/gxk/"))
          .command("ls")
          .destroyOnExit()
          .readOutput(true)
          .redirectOutput(os)
          .redirectError(Slf4jStream.of(log).asError())
//          .addListener(new ProcessListener() {
//            @Override
//            public void beforeStart(ProcessExecutor executor) {
//              log.debug("before start: ", executor.getCommand());
//              actionHandler.beforeExec(deployment);
//            }
//
//            @Override
//            public void afterFinish(Process process, ProcessResult result) {
//              log.debug("finish with exit: " + result.getExitValue());
//
//              if (process.exitValue() != 0) {
//                actionHandler.onFailed(deployment);
//                return;
//              }
//
//              actionHandler.afterExec(deployment);
//            }
//
//            @Override
//            public void afterStop(Process process) {
//              log.debug("stop with exit: " + process.exitValue());
//            }
//          })
          .timeout(1, TimeUnit.MINUTES)
          .execute();
      return os.toString("UTF-8");
    } catch (IOException | InterruptedException | TimeoutException e) {
      log.error("some error ", e);
    }
    return null;
  }
}
