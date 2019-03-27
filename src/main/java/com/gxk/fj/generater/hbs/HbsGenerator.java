package com.gxk.fj.generater.hbs;

import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.gxk.fj.core.Env;
import com.gxk.fj.generater.AbstractGenerator;
import com.gxk.fj.generater.hbs.tpl.TplGen;
import com.gxk.fj.generater.hbs.tpl.TplGenFactory;

public class HbsGenerator extends AbstractGenerator {

  private TplGen inlineGen;
  private TplGen tplGen;

  @Override
  public String getName() {
    return "hbs";
  }

  @Override
  protected boolean init(Env env) {
    this.inlineGen = TplGenFactory.createInlineTplGen(env);
    // generator by tpl
    FileTemplateLoader loader = new FileTemplateLoader(destinationPath.toFile(), ".fj");
    this.tplGen = TplGenFactory.createDirBaseTplGen(env, loader);
    return true;
  }

  @Override
  protected String inlineGen(
      Env env,
      String input
  ) {
    return this.inlineGen.gen(env, input);
  }

  @Override
  protected String tplGen(
      Env env,
      String input
  ) {
    return this.tplGen.gen(env, input);
  }
}
