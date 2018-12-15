package com.gxk.demo.cmd;

import com.gxk.demo.logger.ILog;
import com.gxk.demo.logger.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatusCmd implements CmdHandler {

  private static final Logger log = LoggerFactory.getLogger(CmdHandler.class);
  private static final ILog fj = LogFactory.getLog();

  @Override
  public void apply(String... args) {
    fj.info("coming soon ...");
  }
}
