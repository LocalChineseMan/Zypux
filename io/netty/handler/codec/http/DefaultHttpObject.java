package io.netty.handler.codec.http;

import io.netty.handler.codec.DecoderResult;

public class DefaultHttpObject implements HttpObject {
  private DecoderResult decoderResult = DecoderResult.SUCCESS;
  
  public DecoderResult getDecoderResult() {
    return this.decoderResult;
  }
  
  public void setDecoderResult(DecoderResult decoderResult) {
    if (decoderResult == null)
      throw new NullPointerException("decoderResult"); 
    this.decoderResult = decoderResult;
  }
}
