package com.fluento.facade;

import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PackageStructureTest {
 private static final Set<String> PATTERNS=Set.of("factory","builder","strategy","observer","decorator","facade");
 private static final Path ROOT=Path.of("src/main/java/com/fluento");
 @Test void onlyMainIsOutsideTheSixPatternPackages() throws Exception {
  Set<String> roots=new HashSet<>();
  try(var paths=Files.walk(ROOT)){
   for(Path file:paths.filter(p->p.toString().endsWith(".java")).toList()){
    Path relative=ROOT.relativize(file);
    if(relative.getNameCount()==1)assertEquals("Main.java",relative.toString());
    else roots.add(relative.getName(0).toString());
   }
  }
  assertEquals(PATTERNS,roots);assertTrue(Files.isRegularFile(ROOT.resolve("Main.java")));
 }
 @Test void packageDeclarationsMatchTheirDirectories() throws Exception {
  Path javaRoot=Path.of("src/main/java");
  try(var paths=Files.walk(javaRoot)){
   for(Path file:paths.filter(p->p.toString().endsWith(".java")).toList()){
    var matcher=Pattern.compile("package\\s+([\\w.]+);").matcher(Files.readString(file));
    assertTrue(matcher.find(),file.toString());
    assertEquals(javaRoot.relativize(file.getParent()).toString().replace('\\','.').replace('/','.'),matcher.group(1));
   }
  }
 }
}
