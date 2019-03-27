package com.gxk.fj;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.gxk.fj.core.Env;
import org.junit.Assert;
import org.junit.Test;


public class TplTest {

  @Test
  public void test() throws Exception {
    Handlebars hbs = new Handlebars();

    String source = "Handlebars.registerHelper('hey', function (context) {\n" +
      "        return 'Hi ' + context;\n" +
      "      });";

    hbs.registerHelpers("helper_test.js", source);

    Template template = hbs.compileInline("hello {{name}} {{hey name}}");
    Env env = new Env();
    env.put("name", "test");

    Env e2 = new Env("test", env);
    String apply = template.apply(e2);

    Assert.assertEquals("hello test", apply);
  }

  @Test
  public void test_helper() throws Exception {
    Handlebars hbs = new Handlebars();

    String source = "Handlebars.registerHelper('hey', function (context) {\n" +
      "        return 'Hi ' + context;\n" +
      "      });";

    hbs.registerHelpers("helper_test.js", source);

    Template template = hbs.compileInline("hello {{name}} {{hey name}}");
    Env env = new Env();
    env.put("name", "test");

    Env e2 = new Env("test", env);
    String apply = template.apply(e2);

    Assert.assertEquals("hello test Hi test", apply);
  }
}
