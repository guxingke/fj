package com.gxk.demo.logger;

public abstract class LogFactory {

  public static ILog getLog() {
    return new Log();
  }

  public static ILog getLog(Class clazz) {
    return new Log();
  }
}
