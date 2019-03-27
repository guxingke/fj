package com.gxk.fj.cmd;

import com.gxk.fj.constants.Const;
import com.gxk.fj.core.Env;
import com.gxk.fj.core.EnvHolder;
import com.gxk.fj.logger.ILog;
import com.gxk.fj.logger.LogFactory;

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
    if (args.length == 1 && Arrays.asList("-h", "--help").contains(args[0])) {
      fj.error("fj ls [repo]");
      return;
    }

    boolean showAll = true;
    String repo = null;
    if (args.length > 0) {
      repo = args[0];
      showAll = false;
    }

    // get global config
    Env env = EnvHolder.getEnv();
    Object val = env.get(Const.CFG_KEY_SCAFFOLD_REPO);
    if (val == null || !(val instanceof Map)) {
      fj.error("init first please.");
      return;
    }

    Map<String, String> sc = (Map<String, String>) val;

    if (!showAll) {
      if (!sc.containsKey(repo)) {
        fj.error("not found repo, " + repo);
        return;
      }

      String path = sc.get(repo);
      ListCmd.accept(repo, path);
      return;
    }
    sc.forEach(ListCmd::accept);
  }

  private static void accept(String key, String vp) {
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
  }
}
