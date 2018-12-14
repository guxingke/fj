package com.gxk.demo.cmd;

import com.gxk.demo.constants.Const;
import com.gxk.demo.core.Env;
import com.gxk.demo.core.EnvHolder;
import com.gxk.demo.logger.ILog;
import com.gxk.demo.logger.LogFactory;
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
    boolean help = args.length > 0 && Arrays.asList("-h", "--help").contains(args[0]);
    if (help) {
      System.out.println("help ....");
    }

    // get global config
    Env env = EnvHolder.getEnv();
    Object val = env.get(Const.CFG_KEY_SCAFFOLD_REPO);
    if (val == null || !(val instanceof Map)) {
      System.out.println("init first please.");
      return;
    }

    Map<String, String> sc = (Map<String, String>) val;
    sc.forEach((key, vp) -> {
      System.out.println("Scaffolds Group: " + key);

      Path path = Paths.get(vp);
      File file = path.toFile();
      if (!file.exists()) {
        System.out.println("Nothing.");
        return;
      }

      try {
        Files.list(path).map(it -> it.toFile().getName())
          .forEach(it -> {
            System.out.println("Scaffold: " + it);
          });
      } catch (IOException e) {
        log.error("io err", e);
      }

      System.out.println("\n-----------------\n");
    });
  }
}
