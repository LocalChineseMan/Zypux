package org.codehaus.plexus.util.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class CachingOutputStream extends OutputStream {
  private final Path path;
  
  private FileChannel channel;
  
  private ByteBuffer readBuffer;
  
  private ByteBuffer writeBuffer;
  
  private boolean modified;
  
  public CachingOutputStream(File path) throws IOException {
    this(((File)Objects.<File>requireNonNull(path)).toPath());
  }
  
  public CachingOutputStream(Path path) throws IOException {
    this(path, 32768);
  }
  
  public CachingOutputStream(Path path, int bufferSize) throws IOException {
    this.path = Objects.<Path>requireNonNull(path);
    this.channel = FileChannel.open(path, new OpenOption[] { StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE });
    this.readBuffer = ByteBuffer.allocate(bufferSize);
    this.writeBuffer = ByteBuffer.allocate(bufferSize);
  }
  
  public void write(int b) throws IOException {
    if (this.writeBuffer.remaining() < 1) {
      this.writeBuffer.flip();
      flushBuffer(this.writeBuffer);
      this.writeBuffer.clear();
    } 
    this.writeBuffer.put((byte)b);
  }
  
  public void write(byte[] b) throws IOException {
    write(b, 0, b.length);
  }
  
  public void write(byte[] b, int off, int len) throws IOException {
    if (this.writeBuffer.remaining() < len) {
      this.writeBuffer.flip();
      flushBuffer(this.writeBuffer);
      this.writeBuffer.clear();
    } 
    int capacity = this.writeBuffer.capacity();
    while (len >= capacity) {
      flushBuffer(ByteBuffer.wrap(b, off, capacity));
      off += capacity;
      len -= capacity;
    } 
    if (len > 0)
      this.writeBuffer.put(b, off, len); 
  }
  
  public void flush() throws IOException {
    this.writeBuffer.flip();
    flushBuffer(this.writeBuffer);
    this.writeBuffer.clear();
    super.flush();
  }
  
  private void flushBuffer(ByteBuffer writeBuffer) throws IOException {
    if (this.modified) {
      this.channel.write(writeBuffer);
    } else {
      ByteBuffer readBuffer;
      int len = writeBuffer.remaining();
      if (this.readBuffer.capacity() >= len) {
        readBuffer = this.readBuffer;
        readBuffer.clear();
      } else {
        readBuffer = ByteBuffer.allocate(len);
      } 
      while (len > 0) {
        int read = this.channel.read(readBuffer);
        if (read <= 0) {
          this.modified = true;
          this.channel.position(this.channel.position() - readBuffer.position());
          this.channel.write(writeBuffer);
          return;
        } 
        len -= read;
      } 
      readBuffer.flip();
      if (readBuffer.compareTo(writeBuffer) != 0) {
        this.modified = true;
        this.channel.position(this.channel.position() - readBuffer.remaining());
        this.channel.write(writeBuffer);
      } 
    } 
  }
  
  public void close() throws IOException {
    if (this.channel.isOpen()) {
      flush();
      long position = this.channel.position();
      if (position != this.channel.size()) {
        this.modified = true;
        this.channel.truncate(position);
      } 
      this.channel.close();
    } 
  }
  
  public boolean isModified() {
    return this.modified;
  }
}
