package org.apache.logging.log4j.core.appender;

import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.util.Constants;

public abstract class Builder<B extends AbstractOutputStreamAppender.Builder<B>> extends AbstractAppender.Builder<B> {
  @PluginBuilderAttribute
  private boolean bufferedIo = true;
  
  @PluginBuilderAttribute
  private int bufferSize = Constants.ENCODER_BYTE_BUFFER_SIZE;
  
  @PluginBuilderAttribute
  private boolean immediateFlush = true;
  
  public int getBufferSize() {
    return this.bufferSize;
  }
  
  public boolean isBufferedIo() {
    return this.bufferedIo;
  }
  
  public boolean isImmediateFlush() {
    return this.immediateFlush;
  }
  
  public B setImmediateFlush(boolean immediateFlush) {
    this.immediateFlush = immediateFlush;
    return (B)asBuilder();
  }
  
  public B setBufferedIo(boolean bufferedIo) {
    this.bufferedIo = bufferedIo;
    return (B)asBuilder();
  }
  
  public B setBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
    return (B)asBuilder();
  }
  
  @Deprecated
  public B withImmediateFlush(boolean immediateFlush) {
    this.immediateFlush = immediateFlush;
    return (B)asBuilder();
  }
  
  @Deprecated
  public B withBufferedIo(boolean bufferedIo) {
    this.bufferedIo = bufferedIo;
    return (B)asBuilder();
  }
  
  @Deprecated
  public B withBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
    return (B)asBuilder();
  }
}
