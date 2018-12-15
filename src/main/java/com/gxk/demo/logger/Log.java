package com.gxk.demo.logger;

public class Log implements ILog {

  // default for info
  public static int level = 1;

  @Override
  public void error(String msg) {
    System.err.println(msg);
  }

  @Override
  public void info(String msg) {
    if (level > 0) {
      System.out.println(msg);
    }
  }

  @Override
  public void debug(String msg) {
    if (level > 1) {
      System.out.println(msg);
    }
  }
}
