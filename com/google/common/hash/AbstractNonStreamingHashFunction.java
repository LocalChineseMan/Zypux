package com.google.common.hash;

import java.io.IOException;

final class BufferingHasher extends AbstractHasher {
  final AbstractNonStreamingHashFunction.ExposedByteArrayOutputStream stream;
  
  static final int BOTTOM_BYTE = 255;
  
  BufferingHasher(int expectedInputSize) {
    this.stream = new AbstractNonStreamingHashFunction.ExposedByteArrayOutputStream(expectedInputSize);
  }
  
  public Hasher putByte(byte b) {
    this.stream.write(b);
    return this;
  }
  
  public Hasher putBytes(byte[] bytes) {
    try {
      this.stream.write(bytes);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } 
    return this;
  }
  
  public Hasher putBytes(byte[] bytes, int off, int len) {
    this.stream.write(bytes, off, len);
    return this;
  }
  
  public Hasher putShort(short s) {
    this.stream.write(s & 0xFF);
    this.stream.write(s >>> 8 & 0xFF);
    return this;
  }
  
  public Hasher putInt(int i) {
    this.stream.write(i & 0xFF);
    this.stream.write(i >>> 8 & 0xFF);
    this.stream.write(i >>> 16 & 0xFF);
    this.stream.write(i >>> 24 & 0xFF);
    return this;
  }
  
  public Hasher putLong(long l) {
    for (int i = 0; i < 64; i += 8)
      this.stream.write((byte)(int)(l >>> i & 0xFFL)); 
    return this;
  }
  
  public Hasher putChar(char c) {
    this.stream.write(c & 0xFF);
    this.stream.write(c >>> 8 & 0xFF);
    return this;
  }
  
  public <T> Hasher putObject(T instance, Funnel<? super T> funnel) {
    funnel.funnel(instance, this);
    return this;
  }
  
  public HashCode hash() {
    return AbstractNonStreamingHashFunction.this.hashBytes(this.stream.byteArray(), 0, this.stream.length());
  }
  
  private static final class AbstractNonStreamingHashFunction {}
  
  private final class AbstractNonStreamingHashFunction {}
}
