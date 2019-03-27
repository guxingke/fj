package com.gxk.fj.util;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ExecUtilsTest {

  @Test
  public void execLocal() {
    File dir = Paths.get("/").toFile();
    List<String> paras = Arrays.asList("ls", "-l");
    String[] cmd = new String[paras.size()];
    paras.toArray(cmd);
    String output = ExecUtils.execLocal(dir, cmd);
    assertNotNull(output);
  }
}