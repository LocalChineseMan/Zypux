package io.netty.handler.codec.http;

public interface FullHttpResponse extends HttpResponse, FullHttpMessage {
  FullHttpResponse copy();
  
  FullHttpResponse retain(int paramInt);
  
  FullHttpResponse retain();
  
  FullHttpResponse setProtocolVersion(HttpVersion paramHttpVersion);
  
  FullHttpResponse setStatus(HttpResponseStatus paramHttpResponseStatus);
}
