package io.netty.handler.codec.http;

public interface HttpMessage extends HttpObject {
  HttpVersion getProtocolVersion();
  
  HttpMessage setProtocolVersion(HttpVersion paramHttpVersion);
  
  HttpHeaders headers();
}
