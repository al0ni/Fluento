package com.fluento.builder.catalog;
import java.util.Map;

public final class Pronunciations {
 private Pronunciations(){}
 private static final Map<String,String> IPA=Map.ofEntries(
 Map.entry("hello","/həˈləʊ/"),Map.entry("yes","/jes/"),Map.entry("no","/nəʊ/"),Map.entry("thanks","/θæŋks/"),
 Map.entry("apple","/ˈæp.əl/"),Map.entry("bread","/bred/"),Map.entry("water","/ˈwɔː.tə/"),Map.entry("milk","/mɪlk/"),
 Map.entry("ticket","/ˈtɪk.ɪt/"),Map.entry("airport","/ˈeə.pɔːt/"),Map.entry("hotel","/həʊˈtel/"),Map.entry("train","/treɪn/"),
 Map.entry("morning","/ˈmɔː.nɪŋ/"),Map.entry("work","/wɜːk/"),Map.entry("home","/həʊm/"),Map.entry("book","/bʊk/"));
 private static final java.util.Properties EXTRA=new java.util.Properties();
 static {
  try(var stream=java.util.Objects.requireNonNull(Pronunciations.class.getResourceAsStream("/lessons/extra-ipa.properties"))){
   EXTRA.load(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8));
  }catch(java.io.IOException error){throw new ExceptionInInitializerError(error);}
 }
 public static String forWord(String id){return id==null?"":IPA.getOrDefault(id,EXTRA.getProperty(id,""));}
}
