package io.netty.handler.codec.http.multipart;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;

class WrappedHttpRequest implements HttpRequest {
  private final HttpRequest request;
  
  WrappedHttpRequest(HttpRequest request) {
    this.request = request;
  }
  
  public HttpRequest setProtocolVersion(HttpVersion version) {
    this.request.setProtocolVersion(version);
    return this;
  }
  
  public HttpRequest setMethod(HttpMethod method) {
    this.request.setMethod(method);
    return this;
  }
  
  public HttpRequest setUri(String uri) {
    this.request.setUri(uri);
    return this;
  }
  
  public HttpMethod getMethod() {
    return this.request.getMethod();
  }
  
  public String getUri() {
    return this.request.getUri();
  }
  
  public HttpVersion getProtocolVersion() {
    return this.request.getProtocolVersion();
  }
  
  public HttpHeaders headers() {
    return this.request.headers();
  }
  
  public DecoderResult getDecoderResult() {
    return this.request.getDecoderResult();
  }
  
  public void setDecoderResult(DecoderResult result) {
    this.request.setDecoderResult(result);
  }
}
