package org.tukaani.xz;

public class MemoryLimitException extends XZIOException {
  private static final long serialVersionUID = 3L;
  
  private final int memoryNeeded;
  
  private final int memoryLimit;
  
  public MemoryLimitException(int paramInt1, int paramInt2) {
    super("" + paramInt1 + " KiB of memory would be needed; limit was " + paramInt2 + " KiB");
    this.memoryNeeded = paramInt1;
    this.memoryLimit = paramInt2;
  }
  
  public int getMemoryNeeded() {
    return this.memoryNeeded;
  }
  
  public int getMemoryLimit() {
    return this.memoryLimit;
  }
}
