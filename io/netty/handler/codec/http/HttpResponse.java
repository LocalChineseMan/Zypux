package io.netty.handler.codec.http;

public interface HttpResponse extends HttpMessage {
  HttpResponseStatus getStatus();
  
  HttpResponse setStatus(HttpResponseStatus paramHttpResponseStatus);
  
  HttpResponse setProtocolVersion(HttpVersion paramHttpVersion);
}
