package com.gxk.fj.cmd.nnew;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NewOptions {

  private final List<String> activeStages;
  private final boolean useDefaultCfg;

  public NewOptions(List<String> activeStages, boolean useDefaultCfg) {
    this.activeStages = activeStages;
    this.useDefaultCfg = useDefaultCfg;
  }

  public static NewOptions build(String... args) {
    Map<String, String> ops = Arrays.stream(args)
      .filter(it -> it.startsWith("--fj.new"))
      .map(it -> {
        if (!it.contains("=")) {
          return it + "=true";
        }
        return it;
      })
      .map(it -> it.split("="))
      .collect(Collectors.toMap(it -> it[0].substring(2), it -> it[1]));

    boolean useDefault = ops.getOrDefault("fj.new.default", "false").equals("true");
    List<String> stages = Arrays.asList(ops.getOrDefault("fj.new.stage", "copy,cfg,gen").split(","));

    return new NewOptions(stages, useDefault);
  }

  public List<String> getActiveStages() {
    return activeStages;
  }

  public boolean isUseDefaultCfg() {
    return useDefaultCfg;
  }
}
