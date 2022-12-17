package org.tukaani.xz.delta;

public class DeltaEncoder extends DeltaCoder {
  public DeltaEncoder(int paramInt) {
    super(paramInt);
  }
  
  public void encode(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2) {
    for (byte b = 0; b < paramInt2; b++) {
      byte b1 = this.history[this.distance + this.pos & 0xFF];
      this.history[this.pos-- & 0xFF] = paramArrayOfbyte1[paramInt1 + b];
      paramArrayOfbyte2[b] = (byte)(paramArrayOfbyte1[paramInt1 + b] - b1);
    } 
  }
}
