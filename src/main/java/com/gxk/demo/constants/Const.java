package com.gxk.demo.constants;

public abstract class Const {
  public static final String USER_HOME = System.getProperty("user.home");
  public static final String USER_DIR = System.getProperty("user.dir");
  public static final String FJ_DIR = USER_HOME + "/.fj";
  public static final String FJ_CFG_PATH = FJ_DIR + "/config/fj.toml";


  public static final String KEY_USER_HOME = "user.home";
  public static final String KEY_USER_DIR = "user.dir";

  // cfg
  public static final String CFG_KEY_SCAFFOLD_REPO = "scaffold_repo";

  // dynamic key
  public static final String FJ_KEY_SCAFFOLDS = "scaffolds";


  public static final String FJ_NEW_KEY_SCAFFOLD_NAME = "fj.new.scaffold.name";
  public static final String FJ_NEW_KEY_SCAFFOLD_PATH = "fj.new.scaffold.path";

  public static final String FJ_NEW_KEY_TARGET_PATH = "fj.new.target.path";
  public static final String FJ_NEW_KEY_GENERATOR = "fj.new.generator";
  public static final String FJ_NEW_VAL_DEFAULT_GENERATOR = "default";

}
