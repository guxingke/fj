package com.gxk.demo;

import com.gxk.demo.cmd.CmdHandler;
import com.gxk.demo.cmd.CmdRegistry;
import com.gxk.demo.cmd.ConfigCmd;
import com.gxk.demo.cmd.HelpCmd;
import com.gxk.demo.cmd.InitCmd;
import com.gxk.demo.cmd.ListCmd;
import com.gxk.demo.cmd.NewCmd;
import com.gxk.demo.cmd.StatusCmd;
import com.gxk.demo.constants.Const;
import com.gxk.demo.core.Env;
import com.gxk.demo.core.EnvHolder;
import com.gxk.demo.generater.DefaultGenerator;
import com.gxk.demo.generater.GeneratorRegistry;
import com.moandjiezana.toml.Toml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    registry.reg(Arrays.asList("i", "install"), (args) -> Arrays.stream(args).forEach(System.out::println));
    registry.reg(Arrays.asList("ui", "uninstall"), (args) -> Arrays.stream(args).forEach(System.out::println));
    registry.reg(Arrays.asList("ls", "list"), new ListCmd());
    registry.reg(Arrays.asList("n", "new"), new NewCmd());
    registry.reg(Arrays.asList("st", "status"), new StatusCmd());
    registry.reg(Arrays.asList("m", "module"), new HelpCmd());

    GeneratorRegistry.reg("default", new DefaultGenerator());

    // init cfg
    Env<String, Object> sysCfg = new Env<>();
    sysCfg.put("user.home", Const.USER_HOME);
    sysCfg.put("user.dir", Const.USER_DIR);
    sysCfg.put("fj.config.path", Const.FJ_CFG_PATH);

    EnvHolder.setEnv(sysCfg);

    // fj config
    Env<String, Object> fjCfg = new Env<>("fj", sysCfg);
    Path path = Paths.get(Const.FJ_CFG_PATH);
    if (path.toFile().exists()) {
      Map<String, Object> map = new Toml().read(path.toFile()).toMap();
      map.forEach(fjCfg::put);

      EnvHolder.setEnv(fjCfg);
    }

    // fj dynamic cfg
    Env<String, Object> dyCfg = new Env<>("dy", fjCfg);

    if (fjCfg.containsKey(Const.CFG_KEY_SCAFFOLD_REPO)) {
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
  }

  void run(String action, String... args) {
    CmdHandler handler = registry.get(action);

    if (handler == null) {
      System.out.println("illegal cmd.");

      handler = registry.get("-h");
    }
    handler.apply(args);
  }

  public static void main(String[] args) {
    if (args.length == 0) {
      args = new String[] {"-h"};
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
