package com.gxk.fj.gen;

import com.gxk.fj.core.Env;
import com.gxk.fj.generater.hbs.tpl.InlineTplGen;
import org.junit.Test;

import static org.junit.Assert.*;

public class InlineTplGenTest {

  @Test
  public void gen() {
    Env env = new Env();
    env.put("test", "2x2");

    String ret = new InlineTplGen(env).gen(env, "{{test}}");

    assertEquals("2x2", ret);
  }

  @Test
  public void gen2() {
    Env env = new Env();
    env.put("test", "2x2");

    String ret = new InlineTplGen(env).gen(env, "{{test1111}}");

    assertEquals("", ret);
  }
}