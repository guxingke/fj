package com.gxk.demo.cmd;

import com.gxk.demo.constants.Const;
import com.gxk.demo.core.Env;
import com.gxk.demo.core.EnvHolder;
import com.gxk.demo.logger.ILog;
import com.gxk.demo.logger.LogFactory;
import com.gxk.demo.util.CmdUtils;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class UninstallCmd implements CmdHandler {

  private static final Logger log = LoggerFactory.getLogger(CmdHandler.class);
  private static final ILog fj = LogFactory.getLog();

  @Override
  public void apply(String... args) {
    if (CmdUtils.isHelpCmd(args)) {
      fj.info("fj ui <alias>");
      return;
    }

    if (args.length < 1) {
      fj.info("fj ui <alias>");
      return;
    }

    if (EnvHolder.getEnv().getOrDefault(Const.FJ_KEY_INIT, "false").equals("false")) {
      fj.error("init first please, nothing to do.");
      return;
    }

    Env env = EnvHolder.getEnv();

    // location [alias]
    Map<String, String> repo = (Map<String, String>) env.get(Const.CFG_KEY_SCAFFOLD_REPO);

    String scaffoldName = args[0];
    boolean exists = repo.containsKey(scaffoldName);
    if (!exists) {
      log.error("fatal: the alias not exists, " + scaffoldName);
      return;
    }

    Path sysCfgPath = Paths.get((String) env.get(Const.FJ_KEY_CFG_PATH));
    Map<String, Object> sysConfig = new Toml().read(sysCfgPath.toFile()).toMap();

    repo.remove(scaffoldName);

    sysConfig.put(Const.CFG_KEY_SCAFFOLD_REPO, repo);

    TomlWriter writer = new TomlWriter();
    try {
      writer.write(sysConfig, sysCfgPath.toFile());
    } catch (IOException e) {
      log.error("io err", e);
    }
  }
}
