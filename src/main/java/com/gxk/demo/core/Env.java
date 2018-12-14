package com.gxk.demo.core;

import java.util.HashMap;

public class Env<String, Object> extends HashMap<String, Object> {

  private final Env<String, Object> parent;
  private final java.lang.String name;

  public Env() {
    super();
    this.parent = null;
    this.name = "sys";
  }

  public Env(String name, Env<String,Object> parent) {
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
}
