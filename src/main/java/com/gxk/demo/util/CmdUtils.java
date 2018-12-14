package com.gxk.demo.util;

import java.util.Arrays;

public abstract class CmdUtils {

  public static boolean isHelpCmd(String... args) {
    if (args.length == 0) {
      return true;
    }
    return Arrays.asList("-h", "--help").contains(args[0]);
  }
}
