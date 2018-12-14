package com.gxk.demo.cmd;

@FunctionalInterface
public interface CmdHandler {
  void apply(String... args);
}
