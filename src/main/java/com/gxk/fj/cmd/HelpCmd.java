package com.gxk.fj.cmd;

import com.gxk.fj.logger.ILog;
import com.gxk.fj.logger.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HelpCmd implements CmdHandler {

  private static final Logger log = LoggerFactory.getLogger(CmdHandler.class);
  private static final ILog fj = LogFactory.getLog();

  @Override
  public void apply(String... args) {
    InputStream is = this.getClass().getClassLoader().getResourceAsStream("help.txt");
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
      StringBuilder out = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        out.append(line).append("\n");
      }
      fj.info(out.toString());
    } catch (Exception e) {
      log.error("io err", e);
    }
  }
}
