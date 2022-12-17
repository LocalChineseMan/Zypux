package org.tukaani.xz;

class DeltaEncoder extends DeltaCoder implements FilterEncoder {
  private final DeltaOptions options;
  
  private final byte[] props = new byte[1];
  
  DeltaEncoder(DeltaOptions paramDeltaOptions) {
    this.props[0] = (byte)(paramDeltaOptions.getDistance() - 1);
    this.options = (DeltaOptions)paramDeltaOptions.clone();
  }
  
  public long getFilterID() {
    return 3L;
  }
  
  public byte[] getFilterProps() {
    return this.props;
  }
  
  public boolean supportsFlushing() {
    return true;
  }
  
  public FinishableOutputStream getOutputStream(FinishableOutputStream paramFinishableOutputStream, ArrayCache paramArrayCache) {
    return this.options.getOutputStream(paramFinishableOutputStream, paramArrayCache);
  }
}
