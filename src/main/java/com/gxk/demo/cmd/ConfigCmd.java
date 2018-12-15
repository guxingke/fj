package com.gxk.demo.cmd;

import com.gxk.demo.constants.Const;
import com.gxk.demo.core.EnvHolder;
import com.gxk.demo.logger.ILog;
import com.gxk.demo.logger.LogFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigCmd implements CmdHandler {

  private static final Logger log = LoggerFactory.getLogger(CmdHandler.class);
  private static final ILog fj = LogFactory.getLog();

  @Override
  public void apply(String... args) {

    if (EnvHolder.getEnv().getOrDefault(Const.FJ_KEY_INIT, "false").equals("false")) {
      fj.error("init first please, nothing to do.");
      return;
    }

    EnvHolder.getEnv().print();
  }
}
