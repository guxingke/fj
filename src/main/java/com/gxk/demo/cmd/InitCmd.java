package com.gxk.demo.cmd;

import com.gxk.demo.constants.Const;
import com.gxk.demo.logger.ILog;
import com.gxk.demo.logger.LogFactory;
import com.moandjiezana.toml.TomlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class InitCmd implements CmdHandler {

  private static final Logger log = LoggerFactory.getLogger(CmdHandler.class);
  private static final ILog fj = LogFactory.getLog();

  @Override
  public void apply(String... args) {
    if (args.length > 0) {
      System.out.println("init fj, gen default config for current user.");
      return;
    }

    String homeDir = System.getProperty("user.home");
    String defaultCfgDir = homeDir + "/.fj/config";

    String fileName = "fj.toml";

    Path path = Paths.get(defaultCfgDir, fileName);
    if (path.toFile().exists()) {
      System.out.println("already init. nothing to do.");
      return;
    }

    try {
      Files.createDirectories(Paths.get(defaultCfgDir));
      TomlWriter writer = new TomlWriter();
      writer.write(defaultCfg(), path.toFile());
    } catch (IOException e) {
      log.error("cmd err", e);
      System.out.println("some err, retry");
    }

    // init default scaffold
    initDefaultScaffold(homeDir);
  }

  private void initDefaultScaffold(String homeDir) {
    try {
      Path defaultPath = Paths.get(homeDir, "/.fj/scaffolds");
      Files.createDirectories(defaultPath);

      Files.createDirectories(Paths.get(defaultPath.toString(), "demo", "template"));

      Path configPath = Paths.get(defaultPath.toString(), "demo", "config.toml");
      Files.createFile(configPath);
      Path temp1Path = Paths.get(defaultPath.toString(), "demo", "template", "{{name}}.py");
      Files.createFile(temp1Path);
      Path temp2Path = Paths.get(defaultPath.toString(), "demo", "template", "gen_finish.sh");
      Files.createFile(temp2Path);

      String cs = "###gen###\n" +
        "# just support string\n" +
        "\n" +
        "name = \"demo\"\n" +
        "author = \"gxk\"\n" +
        "\n" +
        "[info]\n" +
        "version = \"0.0.1\"\n" +
        "desc = \"demo scaffold\"";
      try (BufferedWriter bw = Files.newBufferedWriter(configPath, StandardCharsets.UTF_8)) {
        bw.write(cs);
      } catch (IOException ex) {
        log.error("write file error", ex);
      }

      String t1s = "#!/usr/bin/env python\n" +
        "print 'name : {{ name }}'\n" +
        "\n" +
        "print 'author : {{ author }}'\n" +
        "print 'info : {{ info.desc }}'\n" +
        "\n";
      try (BufferedWriter bw = Files.newBufferedWriter(temp1Path, StandardCharsets.UTF_8)) {
        bw.write(t1s);
      } catch (IOException ex) {
        log.error("write file error", ex);
      }

      String t2s = "#!/usr/bin/env bash\n" +
        "\n" +
        "echo \"{{name}} finish...\"";

      try (BufferedWriter bw = Files.newBufferedWriter(temp2Path, StandardCharsets.UTF_8)) {
        bw.write(t2s);
      } catch (IOException ex) {
        log.error("write file error", ex);
      }

    } catch (IOException e) {
      log.error("io err", e);
    }
  }

  private Map<String, Map<String, String>> defaultCfg() {

    Map<String, Map<String, String>> ret = new HashMap<>();
    String userHome = System.getProperty("user.home");

    Map<String, String> folds = new HashMap<>();
    folds.put("default", userHome + "/.fj/scaffolds");
    ret.put(Const.CFG_KEY_SCAFFOLD_REPO, folds);

    return ret;
  }
}
