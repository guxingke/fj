package com.gxk.fj.generater.hbs.tpl;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.gxk.fj.core.Env;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class InlineTplGen implements TplGen {

  private static final Logger log = LoggerFactory.getLogger(TplGen.class);

  private final Handlebars hbs;

  public InlineTplGen(Env env) {
    hbs = HandlebarsFactory.createInlineTpl();
  }

  @Override
  public String gen(Env env, String input) {
    try {
      Template tpl = hbs.compileInline(input);
      return tpl.apply(env);
    } catch (IOException e) {
      log.error("io err", e);
    }
    return "!!!err!!!";
  }
}
