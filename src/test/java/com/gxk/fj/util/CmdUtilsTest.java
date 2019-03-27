package com.gxk.fj.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class CmdUtilsTest {

  @Test
  public void execLocal() {
    String output = CmdUtils.execLocal();
    System.out.println(output);
  }
}