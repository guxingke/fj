package com.gxk.fj.generater.hbs.tpl;

import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.gxk.fj.core.Env;

public abstract class TplGenFactory {

  public static TplGen createInlineTplGen(Env env) {
    return new InlineTplGen(env);
  }

  public static TplGen createDirBaseTplGen(Env env, FileTemplateLoader loader) {
    return new DirBaseTplGen(env, loader);
  }
}
