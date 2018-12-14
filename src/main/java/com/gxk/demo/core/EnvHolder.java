package com.gxk.demo.core;

import java.util.HashMap;

public abstract class EnvHolder extends HashMap<String, Object> {
  private static Env env;

  public static Env getEnv() {
    return env;
  }

  public static void setEnv(Env env) {
    EnvHolder.env = env;
  }
}
