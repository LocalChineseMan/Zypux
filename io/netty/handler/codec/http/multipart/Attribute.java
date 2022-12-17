package io.netty.handler.codec.http.multipart;

import java.io.IOException;

public interface Attribute extends HttpData {
  String getValue() throws IOException;
  
  void setValue(String paramString) throws IOException;
  
  Attribute copy();
  
  Attribute duplicate();
  
  Attribute retain();
  
  Attribute retain(int paramInt);
}
