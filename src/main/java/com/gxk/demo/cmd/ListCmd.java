package com.gxk.demo.cmd;

import com.gxk.demo.constants.Const;
import com.gxk.demo.core.Env;
import com.gxk.demo.core.EnvHolder;
import com.gxk.demo.logger.ILog;
import com.gxk.demo.logger.LogFactory;
import com.gxk.demo.util.CmdUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

public class ListCmd implements CmdHandler {

  private static final Logger log = LoggerFactory.getLogger(CmdHandler.class);
  private static final ILog fj = LogFactory.getLog();

  @Override
  public void apply(String... args) {
    if (CmdUtils.isHelpCmd(args)) {
      fj.info("help ....");
    }

    // get global config
    Env env = EnvHolder.getEnv();
    Object val = env.get(Const.CFG_KEY_SCAFFOLD_REPO);
    if (val == null || !(val instanceof Map)) {
      fj.error("init first please.");
      return;
    }

    Map<String, String> sc = (Map<String, String>) val;
    sc.forEach((key, vp) -> {
      fj.error("Scaffolds Group: " + key);

      Path path = Paths.get(vp);
      File file = path.toFile();
      if (!file.exists()) {
        fj.info("Nothing.");
        return;
      }

      try {
        Files.list(path).map(it -> it.toFile().getName())
          .forEach(it -> {
            fj.info("Scaffold: " + it);
          });
      } catch (IOException e) {
        log.error("io err", e);
      }

      fj.info("\n-----------------\n");
    });
  }
}
