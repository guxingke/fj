package com.gxk.demo.logger;

public class Log implements ILog {

  @Override
  public void error(String msg) {
    System.err.println(msg);
  }

  @Override
  public void info(String msg) {
    System.out.println(msg);
  }

  @Override
  public void debug(String msg) {
    System.out.println(msg);
  }
}
