package org.codehaus.plexus.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Files;
import org.codehaus.plexus.util.xml.XmlStreamReader;

public class ReaderFactory {
  public static final java.lang.String ISO_8859_1 = "ISO-8859-1";
  
  public static final java.lang.String US_ASCII = "US-ASCII";
  
  public static final java.lang.String UTF_16 = "UTF-16";
  
  public static final java.lang.String UTF_16BE = "UTF-16BE";
  
  public static final java.lang.String UTF_16LE = "UTF-16LE";
  
  public static final java.lang.String UTF_8 = "UTF-8";
  
  public static final java.lang.String FILE_ENCODING = System.getProperty("file.encoding");
  
  public static XmlStreamReader newXmlReader(InputStream in) throws IOException {
    return new XmlStreamReader(in);
  }
  
  public static XmlStreamReader newXmlReader(File file) throws IOException {
    return new XmlStreamReader(file);
  }
  
  public static XmlStreamReader newXmlReader(URL url) throws IOException {
    return new XmlStreamReader(url);
  }
  
  public static Reader newPlatformReader(InputStream in) {
    return new InputStreamReader(in);
  }
  
  public static Reader newPlatformReader(File file) throws IOException {
    return Files.newBufferedReader(file.toPath());
  }
  
  public static Reader newPlatformReader(URL url) throws IOException {
    return new InputStreamReader(url.openStream());
  }
  
  public static Reader newReader(InputStream in, java.lang.String encoding) throws UnsupportedEncodingException {
    return new InputStreamReader(in, encoding);
  }
  
  public static Reader newReader(File file, java.lang.String encoding) throws IOException {
    return new InputStreamReader(Files.newInputStream(file.toPath(), new java.nio.file.OpenOption[0]), encoding);
  }
  
  public static Reader newReader(URL url, java.lang.String encoding) throws IOException {
    return new InputStreamReader(url.openStream(), encoding);
  }
}
