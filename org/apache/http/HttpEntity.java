package org.apache.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface HttpEntity {
  boolean isRepeatable();
  
  boolean isChunked();
  
  long getContentLength();
  
  Header getContentType();
  
  Header getContentEncoding();
  
  InputStream getContent() throws IOException, IllegalStateException;
  
  void writeTo(OutputStream paramOutputStream) throws IOException;
  
  boolean isStreaming();
  
  @Deprecated
  void consumeContent() throws IOException;
}
