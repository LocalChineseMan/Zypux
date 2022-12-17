package org.codehaus.plexus.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Properties;

public class PropertyUtils {
  public static Properties loadProperties(URL url) throws IOException {
    return loadProperties(((URL)Objects.<URL>requireNonNull(url, "url")).openStream());
  }
  
  public static Properties loadProperties(File file) throws IOException {
    return loadProperties(Files.newInputStream(((File)Objects.<File>requireNonNull(file, "file")).toPath(), new java.nio.file.OpenOption[0]));
  }
  
  public static Properties loadProperties(InputStream is) throws IOException {
    Properties properties = new Properties();
    if (is != null) {
      InputStream in = is;
      try {
        properties.load(in);
        if (in != null)
          in.close(); 
      } catch (Throwable throwable) {
        if (in != null)
          try {
            in.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } 
    return properties;
  }
}
