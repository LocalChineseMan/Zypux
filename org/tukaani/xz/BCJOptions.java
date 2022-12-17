package org.tukaani.xz;

abstract class BCJOptions extends FilterOptions {
  private final int alignment;
  
  int startOffset = 0;
  
  BCJOptions(int paramInt) {
    this.alignment = paramInt;
  }
  
  public void setStartOffset(int paramInt) throws UnsupportedOptionsException {
    if ((paramInt & this.alignment - 1) != 0)
      throw new UnsupportedOptionsException("Start offset must be a multiple of " + this.alignment); 
    this.startOffset = paramInt;
  }
  
  public int getStartOffset() {
    return this.startOffset;
  }
  
  public int getEncoderMemoryUsage() {
    return SimpleOutputStream.getMemoryUsage();
  }
  
  public int getDecoderMemoryUsage() {
    return SimpleInputStream.getMemoryUsage();
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      assert false;
      throw new RuntimeException();
    } 
  }
}
