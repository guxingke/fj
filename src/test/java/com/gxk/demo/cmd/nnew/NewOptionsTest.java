package com.gxk.demo.cmd.nnew;

import org.junit.Test;

import static org.junit.Assert.*;

public class NewOptionsTest {

  @Test
  public void build() {
    String[] args = new String[] {
      "--fj.new.default=true",
      "--fj.new.stage=copy,cfg,gen"
    };
    NewOptions options = NewOptions.build(args);

    assertEquals(true, options.isUseDefaultCfg());
  }
}