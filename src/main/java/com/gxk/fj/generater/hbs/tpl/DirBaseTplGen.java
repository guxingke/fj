package com.gxk.fj.generater.hbs.tpl;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.gxk.fj.core.Env;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DirBaseTplGen implements TplGen {

  private static final Logger log = LoggerFactory.getLogger(TplGen.class);

  private final Handlebars hbs;

  public DirBaseTplGen(Env env, FileTemplateLoader loader) {
    hbs = HandlebarsFactory.createTplWithLoader(loader);
  }

  @Override
  public String gen(Env env, String input) {
    try {
      Template tpl = hbs.compile(input);
      return tpl.apply(env);
    } catch (IOException e) {
      log.error("io err", e);
    }
    return "!!!err!!!";
  }
}
