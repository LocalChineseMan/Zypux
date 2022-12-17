package org.codehaus.plexus.archiver.zip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.nio.file.Files;
import org.apache.commons.io.output.ThresholdingOutputStream;

class OffloadingOutputStream extends ThresholdingOutputStream {
  private ByteArrayOutputStream memoryOutputStream;
  
  private OutputStream currentOutputStream;
  
  private File outputFile = null;
  
  private final String prefix;
  
  private final String suffix;
  
  private final File directory;
  
  private boolean closed = false;
  
  public OffloadingOutputStream(int threshold, String prefix, String suffix, File directory) {
    this(threshold, null, prefix, suffix, directory);
    if (prefix == null)
      throw new IllegalArgumentException("Temporary file prefix is missing"); 
  }
  
  private OffloadingOutputStream(int threshold, File outputFile, String prefix, String suffix, File directory) {
    super(threshold);
    this.outputFile = outputFile;
    this.memoryOutputStream = new ByteArrayOutputStream(threshold / 10);
    this.currentOutputStream = this.memoryOutputStream;
    this.prefix = prefix;
    this.suffix = suffix;
    this.directory = directory;
  }
  
  protected OutputStream getStream() throws IOException {
    return this.currentOutputStream;
  }
  
  protected void thresholdReached() throws IOException {
    if (this.prefix != null)
      this.outputFile = File.createTempFile(this.prefix, this.suffix, this.directory); 
    this.currentOutputStream = Files.newOutputStream(this.outputFile.toPath(), new java.nio.file.OpenOption[0]);
  }
  
  public InputStream getInputStream() throws IOException {
    InputStream memoryAsInput = this.memoryOutputStream.toInputStream();
    if (this.outputFile == null)
      return memoryAsInput; 
    return new SequenceInputStream(memoryAsInput, Files.newInputStream(this.outputFile.toPath(), new java.nio.file.OpenOption[0]));
  }
  
  public byte[] getData() {
    if (this.memoryOutputStream != null)
      return this.memoryOutputStream.toByteArray(); 
    return null;
  }
  
  public File getFile() {
    return this.outputFile;
  }
  
  public void close() throws IOException {
    super.close();
    this.closed = true;
    this.currentOutputStream.close();
  }
}
