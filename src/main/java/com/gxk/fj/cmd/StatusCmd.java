package com.gxk.fj.cmd;

import com.gxk.fj.core.Env;
import com.gxk.fj.core.EnvHolder;
import com.gxk.fj.logger.ILog;
import com.gxk.fj.logger.LogFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatusCmd implements CmdHandler {

  private static final Logger log = LoggerFactory.getLogger(CmdHandler.class);
  private static final ILog fj = LogFactory.getLog();

  @Override
  public void apply(String... args) {
    Env env = EnvHolder.getEnv();

    Env userEnv = env.findEnvByName("user");
    if (userEnv == null) {
      fj.error("fatal: not a fj project.");
      return;
    }
    userEnv.print(false);
  }
}
