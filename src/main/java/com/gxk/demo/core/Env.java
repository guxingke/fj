package com.gxk.demo.core;

import com.gxk.demo.logger.ILog;
import com.gxk.demo.logger.LogFactory;

import java.util.HashMap;
import java.util.Map;

public class Env<String, Object> extends HashMap<String, Object> {

  private static final ILog fj = LogFactory.getLog();

  private final Env<String, Object> parent;
  private final java.lang.String name;

  public Env() {
    super();
    this.parent = null;
    this.name = "sys";
  }

  public Env(String name, Env<String, Object> parent) {
    this.parent = parent;
    this.name = parent.getName() + "." + name;
  }

  @Override
  public boolean containsKey(java.lang.Object key) {
    if (this.parent == null) {
      return super.containsKey(key);
    }

    return super.containsKey(key) || this.parent.containsKey(key);
  }

  @Override
  public Object get(java.lang.Object key) {
    Object val = super.get(key);
    if (val == null && this.parent != null) {
      val = this.parent.get(key);
    }
    return val;
  }

  @Override
  public Object getOrDefault(java.lang.Object key, Object defaultValue) {
    Object val = this.get(key);
    if (val == null) {
      return defaultValue;
    }
    return val;
  }

  public Env<String, Object> getParent() {
    return parent;
  }

  public String getName() {
    return (String) name;
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

    print(((HashMap) this), "");
  }

  public void print(boolean recursive) {
    if (recursive) {
      this.print();
      return;
    }

    print(((HashMap) this), "");
  }

  void print(Map<java.lang.String, java.lang.Object> map, java.lang.String prefix) {
    map.forEach((key, val) -> {
      java.lang.String np = prefix + "." + key;
      if (prefix.isEmpty()) {
        np = key;
      }
      if (val instanceof Map) {
        print((Map<java.lang.String, java.lang.Object>) val, np);
      } else {
        fj.info(np + " = " + val);
      }
    });
  }
}
