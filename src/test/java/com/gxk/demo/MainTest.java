package com.gxk.demo;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class MainTest {

  Main main;

  @Before
  public void setup() {
    main = new Main();
  }

  @Test
  public void test_init() throws IOException {
    String file = System.getProperty("user.home") + "/.fj/config/fj.toml";
    Path path = Paths.get(file);
    Files.delete(path);

    main.run("init");

    assertTrue(path.toFile().exists());
  }

  @Test
  public void test_config() {
    main.run("config");
  }

  @Test
  public void test_help() {
    main.run("-h");
  }

  @Test
  public void test_list() {
    main.run("list");
  }

  @Test
  public void test_new() {
    main.run("new", "demo", "test", "--fj.new.default=true");
  }
}
