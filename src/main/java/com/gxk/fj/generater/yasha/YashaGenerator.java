package com.gxk.fj.generater.yasha;

import com.gxk.fj.core.Env;
import com.gxk.fj.generater.AbstractGenerator;

public class YashaGenerator extends AbstractGenerator {

  @Override
  public String getName() {
    return "yasha";
  }

  @Override
  protected boolean init(Env env) {
    return false;
  }

  @Override
  protected String inlineGen(
      Env env,
      String input
  ) {
    return null;
  }

  @Override
  protected String tplGen(
      Env env,
      String input
  ) {
    return null;
  }
}
