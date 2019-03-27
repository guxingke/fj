package com.gxk.fj.generater;

import com.gxk.fj.core.Env;

public interface Generator {
  String getName();
  boolean gen(Env env);
}
