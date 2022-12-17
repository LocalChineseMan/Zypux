package org.iq80.snappy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionInputStream;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.io.compress.Compressor;
import org.apache.hadoop.io.compress.Decompressor;

public class HadoopSnappyCodec implements CompressionCodec {
  public CompressionOutputStream createOutputStream(OutputStream outputStream) throws IOException {
    return new SnappyCompressionOutputStream(outputStream);
  }
  
  public CompressionOutputStream createOutputStream(OutputStream outputStream, Compressor compressor) throws IOException {
    throw new UnsupportedOperationException("Snappy Compressor is not supported");
  }
  
  public Class<? extends Compressor> getCompressorType() {
    throw new UnsupportedOperationException("Snappy Compressor is not supported");
  }
  
  public Compressor createCompressor() {
    throw new UnsupportedOperationException("Snappy Compressor is not supported");
  }
  
  public CompressionInputStream createInputStream(InputStream inputStream) throws IOException {
    return new SnappyCompressionInputStream(inputStream);
  }
  
  public CompressionInputStream createInputStream(InputStream inputStream, Decompressor decompressor) throws IOException {
    throw new UnsupportedOperationException("Snappy Decompressor is not supported");
  }
  
  public Class<? extends Decompressor> getDecompressorType() {
    throw new UnsupportedOperationException("Snappy Decompressor is not supported");
  }
  
  public Decompressor createDecompressor() {
    throw new UnsupportedOperationException("Snappy Decompressor is not supported");
  }
  
  public String getDefaultExtension() {
    return ".snappy";
  }
  
  private static class SnappyCompressionOutputStream extends CompressionOutputStream {
    public SnappyCompressionOutputStream(OutputStream outputStream) throws IOException {
      super(new SnappyOutputStream(outputStream));
    }
    
    public void write(byte[] b, int off, int len) throws IOException {
      this.out.write(b, off, len);
    }
    
    public void finish() throws IOException {
      this.out.flush();
    }
    
    public void resetState() throws IOException {
      this.out.flush();
    }
    
    public void write(int b) throws IOException {
      this.out.write(b);
    }
  }
  
  private static class SnappyCompressionInputStream extends CompressionInputStream {
    public SnappyCompressionInputStream(InputStream inputStream) throws IOException {
      super(new SnappyInputStream(inputStream));
    }
    
    public int read(byte[] b, int off, int len) throws IOException {
      return this.in.read(b, off, len);
    }
    
    public void resetState() throws IOException {
      throw new UnsupportedOperationException("resetState not supported for Snappy");
    }
    
    public int read() throws IOException {
      return this.in.read();
    }
  }
}
