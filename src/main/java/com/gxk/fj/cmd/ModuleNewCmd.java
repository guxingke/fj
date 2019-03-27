package com.gxk.fj.cmd;

import com.gxk.fj.constants.Const;
import com.gxk.fj.core.Env;
import com.gxk.fj.core.EnvHolder;
import com.gxk.fj.generater.Generator;
import com.gxk.fj.generater.GeneratorRegistry;
import com.gxk.fj.logger.ILog;
import com.gxk.fj.logger.LogFactory;
import com.gxk.fj.util.CmdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModuleNewCmd implements CmdHandler {
  private static final Logger log = LoggerFactory.getLogger(CmdHandler.class);
  private static final ILog fj = LogFactory.getLog();

  @Override
  public void apply(final String... args) {

    if (!checkArgs(args)) {
      return;
    }

    if (!processArgs(args)) {
      return;
    }

    // init done
    fj.debug("new module init stage done\n");

    // do gen
    if (!doGen(args)) {
      return;
    }

    fj.debug("new module gen stage done\n");
  }

  private boolean doGen(String... args) {
    String genName = (String) EnvHolder.getEnv().getOrDefault(Const.FJ_NEW_KEY_GENERATOR, Const.FJ_NEW_VAL_DEFAULT_GENERATOR);
    Generator generator = GeneratorRegistry.get(genName);
    if (generator == null) {
      fj.error("no generator provider named " + genName);
      return false;
    }

    return generator.gen(EnvHolder.getEnv());
  }

  private boolean checkArgs(final String... args) {
    if (CmdUtils.isHelpCmd(args)) {
      fj.info("some help .....");
      return false;
    }

    if (args.length < 2) {
      fj.error("illegal args, check help msg please.");
      return false;
    }
    return true;
  }

  private boolean processArgs(final String... args) {
    Env env = EnvHolder.getEnv();

    String scaffoldName = args[0];
    String sName = scaffoldName;
    if (!scaffoldName.contains(".")) {
      sName = "module." + scaffoldName;
    }

    if (!env.containsKey(sName)) {
      fj.error("not found the scaffold, check input please.");
      return false;
    }

    String sPath = ((String) env.get(sName));

    fj.debug(sName);
    fj.debug(sPath);
    env.put("fj.new.scaffold.name", sName);
    env.put("fj.new.scaffold.path", sPath);

    // target
    String destPath = ((String) env.get(Const.KEY_USER_DIR));
    fj.debug(destPath);
    env.put(Const.FJ_NEW_KEY_TARGET_PATH, destPath);

    env.put("name", args[1]);
    // gen cfg
    env.put(Const.FJ_GEN_KEY_SOURCE_NAME, sName);
    env.put(Const.FJ_GEN_KEY_SOURCE_PATH, sPath);
    env.put(Const.FJ_GEN_KEY_TARGET_NAME, args[1]);
    env.put(Const.FJ_GEN_KEY_TARGET_PATH, destPath);

    return true;
  }
}
