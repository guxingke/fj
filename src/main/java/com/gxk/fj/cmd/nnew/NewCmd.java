package com.gxk.fj.cmd.nnew;

import com.gxk.fj.cmd.CmdHandler;
import com.gxk.fj.constants.Const;
import com.gxk.fj.core.Env;
import com.gxk.fj.core.EnvHolder;
import com.gxk.fj.generater.Generator;
import com.gxk.fj.generater.GeneratorRegistry;
import com.gxk.fj.logger.ILog;
import com.gxk.fj.logger.LogFactory;
import com.gxk.fj.util.CmdUtils;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class NewCmd implements CmdHandler {
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

    NewOptions options = NewOptions.build(args);

    // cp scaffold to target
    if (!options.getActiveStages().contains("copy")) {
      fj.error("copy of stage not set.");
      return;
    }

    if (!copyScaffold(args)) {
      return;
    }
    fj.debug("new copy stage done\n");

    if (!options.getActiveStages().contains("cfg")) {
      fj.error("cfg of stage not set.");
      return;
    }

    if (!prepareTargetConfig(args)) {
      return;
    }

    // gen target fj.toml
    if (!genTargetConfig(args)) {
      return;
    }

    // init done
    fj.debug("new cfg stage done\n");

    // do gen
    if (!options.getActiveStages().contains("gen")) {
      fj.error("gen of stage not set.");
      return;
    }
    if (!doGen(args)) {
      return;
    }

    fj.debug("new gen stage done\n");
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

  private boolean copyScaffold(String... args) {
    Env env = EnvHolder.getEnv();
    String targetPath = (String) env.get(Const.FJ_NEW_KEY_TARGET_PATH);
    String sourcePath = (String) env.get(Const.FJ_NEW_KEY_SCAFFOLD_PATH);

    Path prjScaffolds = Paths.get(targetPath, ".fj", "scaffold");

    System.out.println(prjScaffolds.toString());
    try {
      FileUtils.copyDirectory(Paths.get(sourcePath).toFile(), prjScaffolds.toFile());
    } catch (IOException e) {
      log.error("io err", e);
      return false;
    }

    return true;
  }

  private boolean genTargetConfig(String... args) {
    Env env = EnvHolder.getEnv().findEnvByName("scaffold");
    if (env == null) {
      log.error("not found scaffold env, nothing todo.");
      return false;
    }
    String targetPath = (String) env.get(Const.FJ_NEW_KEY_TARGET_PATH);
    // create dir and file
    Path targetDir = Paths.get(targetPath);
    Path targetCfgPath = Paths.get(targetPath, ".fj/fj.toml");
    try {
      if (Files.notExists(targetDir)) {
        Files.createDirectories(targetDir);
      }
      if (Files.notExists(targetCfgPath)) {
        Files.createFile(targetCfgPath);
      }
    } catch (IOException e) {
      log.error("io err", e);
      return false;
    }

    TomlWriter writer = new TomlWriter();
    try {
      writer.write(env, targetCfgPath.toFile());
    } catch (IOException e) {
      log.error("io err", e);
      return false;
    }

    return true;
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
    Map<String, String> scaffolds = (Map<String, String>) env.get(Const.FJ_KEY_SCAFFOLDS);
    if (scaffolds.isEmpty()) {
      fj.error("no available scaffolds");
      return false;
    }

    String scaffoldName = args[0];
    // repo.scaffold
    if (scaffoldName.contains(".") && !scaffolds.containsKey(scaffoldName)) {
      fj.error("not found the scaffold, check input please.");
      return false;
    }

    List<String> matched = scaffolds.keySet().stream()
      .map(it -> it.split("\\."))
      .filter(it -> Objects.equals(it[1], scaffoldName))
      .map(it -> it[0] + "." + it[1])
      .collect(Collectors.toList());

    if (matched.isEmpty()) {
      fj.error("not found the scaffold, check input please.");
      return false;
    }

    if (matched.size() > 1) {
      fj.info("it hit more than one scaffold, use full scaffold name, e.g default.demo .");
      matched.forEach(fj::error);
      return false;
    }

    String sName = matched.get(0);
    String sPath = scaffolds.get(sName);

    fj.debug(sName);
    fj.debug(sPath);
    env.put(Const.FJ_NEW_KEY_SCAFFOLD_NAME, sName);
    env.put(Const.FJ_NEW_KEY_SCAFFOLD_PATH, sPath);

    // target
    String dest = args[1];
    String destPath = dest;
    if (!dest.startsWith("/")) {
      destPath = env.get(Const.KEY_USER_DIR) + "/" + dest;
    }

    fj.debug(destPath);
    env.put(Const.FJ_NEW_KEY_TARGET_PATH, destPath);

    // gen cfg
    env.put(Const.FJ_GEN_KEY_SOURCE_NAME, sName);
    env.put(Const.FJ_GEN_KEY_SOURCE_PATH, destPath + "/.fj/scaffold/template");
    env.put(Const.FJ_GEN_KEY_TARGET_PATH, destPath);

    return true;
  }

  private boolean prepareTargetConfig(final String... args) {
    Env env = EnvHolder.getEnv();
    String sPath = ((String) env.get(Const.FJ_NEW_KEY_SCAFFOLD_PATH));
    String destPath = ((String) env.get(Const.FJ_NEW_KEY_TARGET_PATH));

    // use default cfg
    boolean useDefault = NewOptions.build(args).isUseDefaultCfg();

    // reader scaffold config , and gen apply config
    File file = Paths.get(sPath, "config.toml").toFile();
    if (!file.exists() || !file.isFile()) {
      fj.error("not found scaffold config, nothing to do .");
      return false;
    }

    Env<String, Object> scCfg = new Env<>("scaffold", env);

    Map<String, Object> scaffoldCfg = new Toml().read(file).toMap();
    if (scaffoldCfg.isEmpty()) {
      fj.error("empty scaffold config, nothing to do .");
      return false;
    }

    // all template should be provide name.
    String[] strs = destPath.split("/");
    if (strs.length < 1) {
      fj.error("invalid target path");
      return false;
    }

    String name = strs[strs.length - 1];
    scaffoldCfg.put("name", name);
    if (!useDefault) {
      // check all option value
      Map<String, Object> options = new HashMap<>();
      Scanner scanner = new Scanner(System.in);
      scaffoldCfg.forEach((key, val) -> {
        if (val instanceof String) {
          fj.info(String.format("%s : enter to use default [%s]:", key, val));
          String option = scanner.nextLine();
          if (!option.isEmpty()) {
            options.put(key, option);
          } else {
            options.put(key, val);
          }
        } else {
          options.put(key, val);
        }
      });
      options.forEach(scCfg::put);
    } else {
      scaffoldCfg.forEach(scCfg::put);
    }
    EnvHolder.setEnv(scCfg);

    return true;
  }
}
