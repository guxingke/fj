package com.gxk.fj.generater;

import com.gxk.fj.constants.Const;
import com.gxk.fj.core.Env;
import com.gxk.fj.generater.hbs.HbsGenerator;
import com.gxk.fj.logger.ILog;
import com.gxk.fj.logger.LogFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractGenerator implements Generator {

  private static final Logger log = LoggerFactory.getLogger(HbsGenerator.class);
  private static final ILog fj = LogFactory.getLog();

  protected Path sourcePath;
  protected Path destinationPath;

  @Override
  public boolean gen(Env env) {
    String targetDir = (String) env.get(Const.FJ_GEN_KEY_TARGET_PATH);
    if (!Files.exists(Paths.get(targetDir, ".fj", "fj.toml")) || !Files
        .exists(Paths.get(targetDir, ".fj", "scaffold", "config.toml"))) {
      fj.error("fatal: target dir have not .fj dir or have not .fj/fj.toml");
      return false;
    }

    String sourceDir = (String) env.get(Const.FJ_GEN_KEY_SOURCE_PATH);
    if (sourceDir == null || sourceDir.isEmpty() || !Files.exists(Paths.get(sourceDir))) {
      fj.error("fatal: illegal source dir, dir: " + sourceDir);
      return false;
    }

    this.sourcePath = Paths.get(sourceDir);
    this.destinationPath = Paths.get(targetDir);

    boolean init = init(env);
    if (!init) {
      fj.error("fatal: init generator error");
      return false;
    }

    try {
      Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult preVisitDirectory(
            Path dir,
            BasicFileAttributes attrs
        ) throws IOException {
          Path realPath = destinationPath.resolve(sourcePath.relativize(dir));
          Path transferPath = Paths.get(inlineGen(env, realPath.toString()));
          if (Files.notExists(transferPath)) {
            Files.createDirectories(transferPath);
          }
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(
            Path file,
            BasicFileAttributes attrs
        ) throws IOException {
          // dir
          Path dirSourcePath = destinationPath.resolve(sourcePath.relativize(file.getParent()));
          Path dirPath = Paths.get(inlineGen(env, dirSourcePath.toString()));
          // file
          String fileName = inlineGen(env, file.getFileName().toString());
          Path path = dirPath.resolve(fileName + ".fj");

          FileUtils.copyFile(file.toFile(), path.toFile());

          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      log.error("io err", e);
    }

    log.debug("gen dir and tpl file done");

    try {
      Files.walkFileTree(destinationPath, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult preVisitDirectory(
            Path dir,
            BasicFileAttributes attrs
        ) throws IOException {
          if (dir.endsWith(".fj")) {
            return FileVisitResult.SKIP_SUBTREE;
          }
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(
            Path file,
            BasicFileAttributes attrs
        ) throws IOException {
          if (!file.toFile().getName().endsWith(".fj")) {
            return FileVisitResult.CONTINUE;
          }
          Path relativize = destinationPath.relativize(file);
          String name = relativize.toString();

          String outName = name.substring(0, name.length() - 3);
          String output = tplGen(env, outName);
          FileUtils.write(Paths.get(destinationPath.toString(), outName).toFile(), output,
              StandardCharsets.UTF_8, false
          );

          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      log.error("io err", e);
    }

    fj.debug("gen output done\n");

    try {
      Files.walkFileTree(destinationPath, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult preVisitDirectory(
            Path dir,
            BasicFileAttributes attrs
        ) throws IOException {
          if (dir.endsWith(".fj")) {
            return FileVisitResult.SKIP_SUBTREE;
          }
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(
            Path file,
            BasicFileAttributes attrs
        ) throws IOException {
          if (!file.toFile().getName().endsWith(".fj")) {
            return FileVisitResult.CONTINUE;
          }

          Files.delete(file);

          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      log.error("io err", e);
    }

    return true;
  }

  protected abstract boolean init(Env env);

  protected abstract String inlineGen(
      Env env,
      String input
  );

  protected abstract String tplGen(
      Env env,
      String input
  );
}
