package io.netty.handler.codec.http;

public interface FullHttpRequest extends HttpRequest, FullHttpMessage {
  FullHttpRequest copy();
  
  FullHttpRequest retain(int paramInt);
  
  FullHttpRequest retain();
  
  FullHttpRequest setProtocolVersion(HttpVersion paramHttpVersion);
  
  FullHttpRequest setMethod(HttpMethod paramHttpMethod);
  
  FullHttpRequest setUri(String paramString);
}
