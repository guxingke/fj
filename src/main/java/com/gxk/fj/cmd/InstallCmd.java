package com.gxk.fj.cmd;

import com.gxk.fj.constants.Const;
import com.gxk.fj.core.Env;
import com.gxk.fj.core.EnvHolder;
import com.gxk.fj.logger.ILog;
import com.gxk.fj.logger.LogFactory;
import com.gxk.fj.util.CmdUtils;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class InstallCmd implements CmdHandler {

  private static final Logger log = LoggerFactory.getLogger(CmdHandler.class);
  private static final ILog fj = LogFactory.getLog();

  @Override
  public void apply(String... args) {
    if (CmdUtils.isHelpCmd(args)) {
      fj.info("fj i <scaffolds dir> [alias]");
      return;
    }

    if (args.length < 1) {
      fj.info("fj i <scaffolds dir> [alias]");
      return;
    }

    if (EnvHolder.getEnv().getOrDefault(Const.FJ_KEY_INIT, "false").equals("false")) {
      fj.error("init first please, nothing to do.");
      return;
    }

    Env env = EnvHolder.getEnv();

    Path scaffoldPath = null;
    String scaffoldName = null;
    String dir = args[0];

    if (!dir.startsWith("/")) {
      scaffoldPath = Paths.get(((String) env.get(Const.KEY_USER_DIR)), dir);
    } else {
      scaffoldPath = Paths.get(dir);
    }

    if (!Files.exists(scaffoldPath)) {
      fj.error("fatal: not found the dir, " + scaffoldPath.toString());
      return;
    }

    scaffoldName = scaffoldPath.toFile().getName();
    if (args.length > 1) {
      scaffoldName = args[1];
    }

    // location [alias]
    Map<String, String> repo = (Map<String, String>) env.get(Const.CFG_KEY_SCAFFOLD_REPO);

    boolean exists = repo.containsKey(scaffoldName);
    if (exists) {
      log.error("fatal: the alias exists, " + scaffoldName);
      return;
    }

    Path sysCfgPath = Paths.get((String) env.get(Const.FJ_KEY_CFG_PATH));
    Map<String, Object> sysConfig = new Toml().read(sysCfgPath.toFile()).toMap();

    repo.put(scaffoldName, scaffoldPath.toString());
    sysConfig.put(Const.CFG_KEY_SCAFFOLD_REPO, repo);

    TomlWriter writer = new TomlWriter();
    try {
      writer.write(sysConfig, sysCfgPath.toFile());
    } catch (IOException e) {
      log.error("io err", e);
    }
  }
}
