package com.gxk.fj.cmd;

import com.gxk.fj.logger.ILog;
import com.gxk.fj.logger.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionCmd implements CmdHandler {

  private static final Logger log = LoggerFactory.getLogger(CmdHandler.class);
  private static final ILog fj = LogFactory.getLog();

  @Override
  public void apply(String... args) {
    fj.info("0.0.1-SNAPSHOT");
  }
}
