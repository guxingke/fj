package com.gxk.fj.generater.yasha;

import com.gxk.fj.core.Env;
import com.gxk.fj.generater.AbstractGenerator;
import com.gxk.fj.util.ExecUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class YashaGenerator extends AbstractGenerator {

  @Override
  public String getName() {
    return "yasha";
  }

  @Override
  protected boolean init(Env env) {
    // generator env to yaml

    return true;
  }

  @Override
  protected String inlineGen(
    Env env,
    String input
  ) {
    Path inputPath = Paths.get("/tmp/fj.input");
    Path outPath = Paths.get("/tmp/fj.output");
    try {
      File inputFile = inputPath.toFile();
      if (!inputFile.exists()) {
        Files.createFile(inputPath);
      }
      if (!outPath.toFile().exists()) {
        Files.createFile(outPath);
      }
      Files.write(inputPath, input.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }

    String tpl = "yasha -v .fj/fj.yaml -o /tmp/fj.output /tmp/fj.input";
    String cmd = String.format(tpl, input);

    String[] args = cmd.split(" ");

    ExecUtils.execLocal(destinationPath.toFile(), args);
    try {
      byte[] bytes = Files.readAllBytes(outPath);
      return new String(bytes);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected String tplGen(
    Env env,
    String input
  ) {
    String test = input + ".fj";
    Path outPath = Paths.get("/tmp/fj_" + test.hashCode());
    try {
      if (!outPath.toFile().exists()) {
        Files.createFile(outPath);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    String tpl = "yasha -v .fj/fj.yaml -o /tmp/fj_%s %s";
    String cmd = String.format(tpl, test.hashCode(), test);

    String[] args = cmd.split(" ");

    ExecUtils.execLocal(destinationPath.toFile(), args);
    try {
      byte[] bytes = Files.readAllBytes(outPath);
      return new String(bytes);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
