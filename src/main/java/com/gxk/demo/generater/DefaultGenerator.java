package com.gxk.demo.generater;

import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.gxk.demo.constants.Const;
import com.gxk.demo.core.Env;
import com.gxk.demo.generater.tpl.TplGen;
import com.gxk.demo.generater.tpl.TplGenFactory;
import com.gxk.demo.logger.ILog;
import com.gxk.demo.logger.LogFactory;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class DefaultGenerator implements Generator {

  private static final Logger log = LoggerFactory.getLogger(DefaultGenerator.class);
  private static final ILog fj = LogFactory.getLog();

  @Override
  public boolean gen(Env env) {
    String targetDir = (String) env.get(Const.FJ_NEW_KEY_TARGET_PATH);
    if (!Files.exists(Paths.get(targetDir, "fj.toml"))) {
      fj.error("target dir have not fj.toml");
      return false;
    }

    Path sourcePath = Paths.get(targetDir, ".fj", "template");
    Path destinationPath = Paths.get(targetDir);
    if (!Files.exists(sourcePath) || !Files.exists(Paths.get(targetDir, ".fj", "config.toml"))) {
      fj.error("target dir have not .fj dir or have not .fj/config.toml");
      return false;
    }

    TplGen inline = TplGenFactory.createInlineTplGen(env);

    try {
      Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
          throws IOException {
          Path realPath = destinationPath.resolve(sourcePath.relativize(dir));
          Path transferPath = Paths.get(inline.gen(env, realPath.toString()));
          if (Files.notExists(transferPath)) {
            Files.createDirectories(transferPath);
          }
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          // dir
          Path dirSourcePath = destinationPath.resolve(sourcePath.relativize(file.getParent()));
          Path dirPath = Paths.get(inline.gen(env, dirSourcePath.toString()));
          // file
          String fileName = inline.gen(env, file.getFileName().toString());
          Path path = dirPath.resolve(fileName+".fj");

          FileUtils.copyFile(file.toFile(), path.toFile());

          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      log.error("io err", e);
    }

    log.debug("gen dir and tpl file done");

    // generator by tpl
    FileTemplateLoader loader = new FileTemplateLoader(destinationPath.toFile(), ".fj");
    TplGen dirBaseTplGen = TplGenFactory.createDirBaseTplGen(env, loader);

    try {
      Files.walkFileTree(destinationPath, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
          throws IOException {
          if (dir.endsWith(".fj")) {
            return FileVisitResult.SKIP_SUBTREE;
          }
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          if (!file.toFile().getName().endsWith(".fj")) {
            return FileVisitResult.CONTINUE;
          }
          Path relativize = destinationPath.relativize(file);
          String name = relativize.toString();

          String outName = name.substring(0, name.length() - 3);
          String output = dirBaseTplGen.gen(env, outName);
          FileUtils.write(Paths.get(destinationPath.toString(), outName).toFile(), output, StandardCharsets.UTF_8, false);

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
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
          throws IOException {
          if (dir.endsWith(".fj")) {
            return FileVisitResult.SKIP_SUBTREE;
          }
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
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
}
