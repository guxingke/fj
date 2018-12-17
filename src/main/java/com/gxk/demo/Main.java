package com.gxk.demo;

import com.gxk.demo.cmd.CmdHandler;
import com.gxk.demo.cmd.CmdRegistry;
import com.gxk.demo.cmd.ConfigCmd;
import com.gxk.demo.cmd.HelpCmd;
import com.gxk.demo.cmd.InitCmd;
import com.gxk.demo.cmd.InstallCmd;
import com.gxk.demo.cmd.ListCmd;
import com.gxk.demo.cmd.ModuleNewCmd;
import com.gxk.demo.cmd.NewCmd;
import com.gxk.demo.cmd.StatusCmd;
import com.gxk.demo.cmd.UninstallCmd;
import com.gxk.demo.constants.Const;
import com.gxk.demo.core.Env;
import com.gxk.demo.core.EnvHolder;
import com.gxk.demo.generater.DefaultGenerator;
import com.gxk.demo.generater.GeneratorRegistry;
import com.gxk.demo.logger.Log;
import com.moandjiezana.toml.Toml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
  private static final Logger log = LoggerFactory.getLogger(Main.class);

  private CmdRegistry registry = new CmdRegistry();

  public Main() {
    // register handler
    registry.reg(Arrays.asList("-h", "--help"), new HelpCmd());
    registry.reg(Arrays.asList("-v", "--version"), (args) -> Arrays.stream(args).forEach(System.out::println));
    registry.reg("init", new InitCmd());
    registry.reg("config", new ConfigCmd());
    registry.reg(Arrays.asList("i", "install"), new InstallCmd());
    registry.reg(Arrays.asList("ui", "uninstall"), new UninstallCmd());
    registry.reg(Arrays.asList("ls", "list"), new ListCmd());
    registry.reg(Arrays.asList("n", "new"), new NewCmd());
    registry.reg(Arrays.asList("st", "status"), new StatusCmd());
    registry.reg(Arrays.asList("m", "module"), new ModuleNewCmd());

    GeneratorRegistry.reg("default", new DefaultGenerator());

    // init cfg
    Env<String, Object> sysCfg = new Env<>();
    sysCfg.put(Const.KEY_USER_HOME, System.getProperty("user.home"));
    sysCfg.put(Const.KEY_USER_DIR, System.getProperty("user.dir"));
    sysCfg.put(Const.FJ_KEY_CFG_PATH, System.getProperty("user.home") + "/" + Const.FJ_CFG_PATH);

    if (Files.exists(Paths.get(sysCfg.get(Const.FJ_KEY_CFG_PATH).toString()))) {
      sysCfg.put(Const.FJ_KEY_INIT, "true");
    }

    EnvHolder.setEnv(sysCfg);

    // fj config
    Env<String, Object> fjCfg = new Env<>("fj", sysCfg);
    Path path = Paths.get((String) sysCfg.get(Const.FJ_KEY_CFG_PATH));

    if (path.toFile().exists()) {
      Map<String, Object> map = new Toml().read(path.toFile()).toMap();
      map.forEach(fjCfg::put);

      EnvHolder.setEnv(fjCfg);
    }

    if (fjCfg.containsKey(Const.CFG_KEY_SCAFFOLD_REPO)) {
      // fj dynamic cfg
      Env<String, Object> dyCfg = new Env<>("dy", fjCfg);
      Map<String, String> repo = (Map<String, String>) fjCfg.get(Const.CFG_KEY_SCAFFOLD_REPO);

      Map<String, String> scaffolds = new HashMap<>();
      repo.forEach((key, val) -> {
        Path tp = Paths.get(val);
        if (tp.toFile().isDirectory()) {
          try {
            Files.list(tp).forEach(it -> {
              scaffolds.put(key + "." + it.toFile().getName(), it.toString());
            });
          } catch (IOException e) {
            log.error("parse cfg err", e);
          }
        }
      });

      dyCfg.put(Const.FJ_KEY_SCAFFOLDS, scaffolds);

      EnvHolder.setEnv(dyCfg);
    }

    // dir sensitivity
    File userFj = Paths.get(EnvHolder.getEnv().get(Const.KEY_USER_DIR).toString(), "fj.toml").toFile();
    File userScaffold = Paths.get(EnvHolder.getEnv().get(Const.KEY_USER_DIR).toString(), ".fj").toFile();

    if (userFj.exists() && userFj.isFile() && userFj.canRead() && userScaffold.exists() && userScaffold.isDirectory()) {
      Env<String, Object> env = new Env<>("user", EnvHolder.getEnv());
      new Toml().read(userFj).toMap().forEach(env::put);

      //
      try {
        Files.list(userScaffold.toPath())
          .filter(it -> it.toFile().getName().startsWith("template_"))
          .forEach(it -> {
            String absPath = it.toString();
            String moduleName = it.toFile().getName().substring(9);
            env.put("module." + moduleName, absPath);
          });
      } catch (IOException e) {
        log.error("io err", e);
      }

      EnvHolder.setEnv(env);
    }
  }

  void run(String action, String... args) {
    CmdHandler handler = registry.get(action);

    if (handler == null) {
      System.out.println("illegal cmd.");

      handler = registry.get("-h");
    }

    boolean isDebugLevel = Arrays.stream(args).anyMatch(it -> it.startsWith("--debug"));
    if (isDebugLevel) {
      Log.level = 2;
    }

    handler.apply(args);
  }

  public static void main(String[] args) {
    if (args.length == 0) {
      args = new String[]{"-h"};
    }
    List<String> ags = Arrays.asList(args);
    String action = ags.get(0);

    List<String> cmdArgs = ags.stream().skip(1).collect(Collectors.toList());
    if (cmdArgs.isEmpty()) {
      new Main().run(action);
      return;
    }

    String[] cpArgs = new String[cmdArgs.size()];
    cmdArgs.toArray(cpArgs);

    new Main().run(action, cpArgs);
  }
}
