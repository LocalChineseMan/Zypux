package org.apache.maven.archiver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.Archiver;

public class PomPropertiesUtil {
  private Properties loadPropertiesFile(File file) throws IOException {
    Properties fileProps = new Properties();
    InputStream istream = Files.newInputStream(file.toPath(), new java.nio.file.OpenOption[0]);
    try {
      fileProps.load(istream);
      Properties properties = fileProps;
      if (istream != null)
        istream.close(); 
      return properties;
    } catch (Throwable throwable) {
      if (istream != null)
        try {
          istream.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  private boolean sameContents(Properties props, File file) throws IOException {
    if (!file.isFile())
      return false; 
    Properties fileProps = loadPropertiesFile(file);
    return fileProps.equals(props);
  }
  
  private void createPropertiesFile(Properties properties, File outputFile, boolean forceCreation) throws IOException {
    File outputDir = outputFile.getParentFile();
    if (outputDir != null && !outputDir.isDirectory() && !outputDir.mkdirs())
      throw new IOException("Failed to create directory: " + outputDir); 
    if (!forceCreation && sameContents(properties, outputFile))
      return; 
    PrintWriter pw = new PrintWriter(outputFile, "ISO-8859-1");
    try {
      StringWriter sw = new StringWriter();
      try {
        properties.store(sw, (String)null);
        List<String> lines = new ArrayList<>();
        BufferedReader r = new BufferedReader(new StringReader(sw.toString()));
        try {
          String line;
          while ((line = r.readLine()) != null) {
            if (!line.startsWith("#"))
              lines.add(line); 
          } 
          r.close();
        } catch (Throwable throwable) {
          try {
            r.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          } 
          throw throwable;
        } 
        Collections.sort(lines);
        for (String l : lines)
          pw.println(l); 
        sw.close();
      } catch (Throwable throwable) {
        try {
          sw.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
      pw.close();
    } catch (Throwable throwable) {
      try {
        pw.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public void createPomProperties(MavenSession session, MavenProject project, Archiver archiver, File customPomPropertiesFile, File pomPropertiesFile, boolean forceCreation) throws IOException {
    Properties p;
    String groupId = project.getGroupId();
    String artifactId = project.getArtifactId();
    String version = project.getVersion();
    if (customPomPropertiesFile != null) {
      p = loadPropertiesFile(customPomPropertiesFile);
    } else {
      p = new Properties();
    } 
    p.setProperty("groupId", groupId);
    p.setProperty("artifactId", artifactId);
    p.setProperty("version", version);
    createPropertiesFile(p, pomPropertiesFile, forceCreation);
    archiver.addFile(pomPropertiesFile, "META-INF/maven/" + groupId + "/" + artifactId + "/pom.properties");
  }
}
