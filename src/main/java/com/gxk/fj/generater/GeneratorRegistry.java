package com.gxk.fj.generater;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GeneratorRegistry {

  private static Map<String, Generator> generators = new HashMap<>();

  public static void reg(String name, Generator handler) {
    generators.putIfAbsent(name, handler);
  }

  public static void reg(List<String> names, Generator handler) {
    names.forEach(it -> reg(it, handler));
  }

  public static Generator get(String name) {
    return generators.get(name);
  }
}
