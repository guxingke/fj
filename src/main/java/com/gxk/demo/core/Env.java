package com.gxk.demo.core;

import com.gxk.demo.logger.ILog;
import com.gxk.demo.logger.LogFactory;

import java.util.HashMap;
import java.util.Map;

public class Env {

  private static final ILog fj = LogFactory.getLog();

  private final Env parent;
  private final String name;
  private final Map<String, Object> kv;

  public Env() {
    super();
    this.parent = null;
    this.name = "sys";
    this.kv = new HashMap<>();
  }

  public Env(String name, Env parent) {
    this.parent = parent;
    this.name = parent.getName() + "." + name;
    this.kv = new HashMap<>();
  }

  public boolean containsKey(String key) {
    if (this.parent == null) {
      return this.kv.containsKey(key);
    }

    return this.parent.containsKey(key);
  }

  public Object get(String key) {
    Object val = this.kv.get(key);
    if (val == null && this.parent != null) {
      val = this.parent.get(key);
    }
    return val;
  }

  public void put(String key, Object val) {
    this.kv.put(key, val);
  }

  public Object getOrDefault(String key, Object defaultValue) {
    Object val = this.get(key);
    if (val == null) {
      return defaultValue;
    }
    return val;
  }

  public Env getParent() {
    return parent;
  }

  public String getName() {
    return name;
  }

  public Env findEnvByName(String name) {
    if (this.name.endsWith("." + name)) {
      return this;
    }
    if (this.parent == null) {
      return null;
    }
    return this.parent.findEnvByName(name);
  }

  // recursive for default
  public void print() {
    if (this.getParent() != null) {
      this.getParent().print();
    }

    fj.info("\n-----------");
    fj.info(this.name);
    fj.info("-----------");

    print(this.kv, "");
  }

  public void print(boolean recursive) {
    if (recursive) {
      this.print();
      return;
    }

    print(this.kv, "");
  }

  void print(Map<String, Object> map, java.lang.String prefix) {
    map.forEach((key, val) -> {
      java.lang.String np = prefix + "." + key;
      if (prefix.isEmpty()) {
        np = key;
      }
      if (val instanceof Map) {
        print((Map<String, Object>) val, np);
      } else {
        fj.info(np + " = " + val);
      }
    });
  }
}
