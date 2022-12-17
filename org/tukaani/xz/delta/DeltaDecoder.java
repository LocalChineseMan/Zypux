package org.tukaani.xz.delta;

public class DeltaDecoder extends DeltaCoder {
  public DeltaDecoder(int paramInt) {
    super(paramInt);
  }
  
  public void decode(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++) {
      paramArrayOfbyte[j] = (byte)(paramArrayOfbyte[j] + this.history[this.distance + this.pos & 0xFF]);
      this.history[this.pos-- & 0xFF] = paramArrayOfbyte[j];
    } 
  }
}
