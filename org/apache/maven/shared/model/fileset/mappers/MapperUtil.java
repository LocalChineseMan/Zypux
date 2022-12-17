package org.apache.maven.shared.model.fileset.mappers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.maven.shared.model.fileset.Mapper;

public final class MapperUtil {
  private static final String MAPPER_PROPERTIES = "mappers.properties";
  
  private static Properties implementations;
  
  private static void initializeBuiltIns() {
    if (implementations == null) {
      Properties props = new Properties();
      ClassLoader cloader = Thread.currentThread().getContextClassLoader();
      try {
        InputStream stream = cloader.getResourceAsStream("mappers.properties");
        try {
          if (stream == null)
            throw new IllegalStateException("Cannot find classpath resource: mappers.properties"); 
          props.load(stream);
          implementations = props;
          if (stream != null)
            stream.close(); 
        } catch (Throwable throwable) {
          if (stream != null)
            try {
              stream.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
      } catch (IOException e) {
        throw new IllegalStateException("Cannot find classpath resource: mappers.properties");
      } 
    } 
  }
  
  public static FileNameMapper getFileNameMapper(Mapper mapper) throws MapperException {
    if (mapper == null)
      return null; 
    initializeBuiltIns();
    String type = mapper.getType();
    String classname = mapper.getClassname();
    if (type == null && classname == null)
      throw new MapperException("nested mapper or one of the attributes type or classname is required"); 
    if (type != null && classname != null)
      throw new MapperException("must not specify both type and classname attribute"); 
    if (type != null)
      classname = implementations.getProperty(type); 
    try {
      FileNameMapper m = (FileNameMapper)Thread.currentThread().getContextClassLoader().loadClass(classname).newInstance();
      m.setFrom(mapper.getFrom());
      m.setTo(mapper.getTo());
      return m;
    } catch (ClassNotFoundException e) {
      throw new MapperException("Cannot find mapper implementation: " + classname, e);
    } catch (InstantiationException|IllegalAccessException e) {
      throw new MapperException("Cannot load mapper implementation: " + classname, e);
    } 
  }
}
