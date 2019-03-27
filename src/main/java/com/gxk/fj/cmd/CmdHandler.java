package com.gxk.fj.cmd;

@FunctionalInterface
public interface CmdHandler {
  void apply(String... args);
}
