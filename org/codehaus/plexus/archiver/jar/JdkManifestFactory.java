package org.codehaus.plexus.archiver.jar;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.util.PropertyUtils;

class JdkManifestFactory {
  public static Manifest getDefaultManifest() throws ArchiverException {
    Manifest defaultManifest = new Manifest();
    defaultManifest.getMainAttributes().putValue("Manifest-Version", "1.0");
    String createdBy = "Plexus Archiver";
    String plexusArchiverVersion = getArchiverVersion();
    if (plexusArchiverVersion != null)
      createdBy = createdBy + " " + plexusArchiverVersion; 
    defaultManifest.getMainAttributes().putValue("Created-By", createdBy);
    return defaultManifest;
  }
  
  static String getArchiverVersion() {
    try {
      Properties properties = PropertyUtils.loadProperties(JdkManifestFactory.class.getResourceAsStream("/META-INF/maven/org.codehaus.plexus/plexus-archiver/pom.properties"));
      return (properties != null) ? 
        properties.getProperty("version") : 
        null;
    } catch (IOException e) {
      throw new AssertionError(e);
    } 
  }
  
  public static void merge(Manifest target, Manifest other, boolean overwriteMain) {
    if (other != null) {
      Attributes mainAttributes = target.getMainAttributes();
      if (overwriteMain) {
        mainAttributes.clear();
        mainAttributes.putAll(other.getMainAttributes());
      } else {
        mergeAttributes(mainAttributes, other.getMainAttributes());
      } 
      for (Map.Entry<String, Attributes> o : other.getEntries().entrySet()) {
        Attributes ourSection = target.getAttributes(o.getKey());
        Attributes otherSection = o.getValue();
        if (ourSection == null) {
          if (otherSection != null)
            target.getEntries().put(o.getKey(), (Attributes)otherSection.clone()); 
          continue;
        } 
        mergeAttributes(ourSection, otherSection);
      } 
    } 
  }
  
  public static void mergeAttributes(Attributes target, Attributes section) {
    for (Object o : section.keySet()) {
      Attributes.Name key = (Attributes.Name)o;
      Object value = section.get(o);
      target.put(key, value);
    } 
  }
}
