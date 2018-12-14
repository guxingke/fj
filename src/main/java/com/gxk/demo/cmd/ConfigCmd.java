package com.gxk.demo.cmd;

import com.gxk.demo.core.Env;
import com.gxk.demo.core.EnvHolder;
import com.gxk.demo.logger.ILog;
import com.gxk.demo.logger.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class ConfigCmd implements CmdHandler {

  private static final Logger log = LoggerFactory.getLogger(CmdHandler.class);
  private static final ILog fj = LogFactory.getLog();

  @Override
  public void apply(String... args) {
    String homeDir = System.getProperty("user.home");
    String defaultCfgDir = homeDir + "/.fj/config";

    String fileName = "fj.toml";

    Path path = Paths.get(defaultCfgDir, fileName);
    if (!path.toFile().exists()) {
      fj.error("init first please, nothing to do.");
      return;
    }

    Env env = EnvHolder.getEnv();

    print(env);
  }

  private void print(Env env) {
    if (env.getParent() != null) {
      print(env.getParent());
    }

    fj.info("\n-----------");

    print(env, "");
  }

  void print(Map<String, Object> map, String prefix) {
    map.forEach((key, val) -> {
      String np = prefix + "." + key;
      if (prefix.isEmpty()) {
        np = key;
      }
      if (val instanceof Map) {
        print((Map<String, Object>) val, np);
      } else {
        fj.info(np + " = " + val);
      }
    });
  }
}
