package io.netty.handler.codec.http;

public interface HttpRequest extends HttpMessage {
  HttpMethod getMethod();
  
  HttpRequest setMethod(HttpMethod paramHttpMethod);
  
  String getUri();
  
  HttpRequest setUri(String paramString);
  
  HttpRequest setProtocolVersion(HttpVersion paramHttpVersion);
}
