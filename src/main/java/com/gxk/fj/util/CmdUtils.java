package com.gxk.fj.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public abstract class CmdUtils {

  private static final Logger log = LoggerFactory.getLogger(CmdUtils.class);

  public static boolean isHelpCmd(String... args) {
    if (args.length == 0) {
      return true;
    }
    return Arrays.asList("-h", "--help").contains(args[0]);
  }
}
