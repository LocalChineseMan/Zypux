package org.tukaani.xz;

class BCJEncoder extends BCJCoder implements FilterEncoder {
  private final BCJOptions options;
  
  private final long filterID;
  
  private final byte[] props;
  
  BCJEncoder(BCJOptions paramBCJOptions, long paramLong) {
    assert isBCJFilterID(paramLong);
    int i = paramBCJOptions.getStartOffset();
    if (i == 0) {
      this.props = new byte[0];
    } else {
      this.props = new byte[4];
      for (byte b = 0; b < 4; b++)
        this.props[b] = (byte)(i >>> b * 8); 
    } 
    this.filterID = paramLong;
    this.options = (BCJOptions)paramBCJOptions.clone();
  }
  
  public long getFilterID() {
    return this.filterID;
  }
  
  public byte[] getFilterProps() {
    return this.props;
  }
  
  public boolean supportsFlushing() {
    return false;
  }
  
  public FinishableOutputStream getOutputStream(FinishableOutputStream paramFinishableOutputStream, ArrayCache paramArrayCache) {
    return this.options.getOutputStream(paramFinishableOutputStream, paramArrayCache);
  }
}
