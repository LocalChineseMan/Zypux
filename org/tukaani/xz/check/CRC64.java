package org.tukaani.xz.check;

public class CRC64 extends Check {
  private static final long[][] TABLE = new long[4][256];
  
  private long crc = -1L;
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    int i = paramInt1 + paramInt2;
    int j = paramInt1;
    int k = i - 3;
    while (j < k) {
      int m = (int)this.crc;
      this.crc = TABLE[3][m & 0xFF ^ paramArrayOfbyte[j] & 0xFF] ^ TABLE[2][m >>> 8 & 0xFF ^ paramArrayOfbyte[j + 1] & 0xFF] ^ this.crc >>> 32L ^ TABLE[1][m >>> 16 & 0xFF ^ paramArrayOfbyte[j + 2] & 0xFF] ^ TABLE[0][m >>> 24 & 0xFF ^ paramArrayOfbyte[j + 3] & 0xFF];
      j += 4;
    } 
    while (j < i)
      this.crc = TABLE[0][paramArrayOfbyte[j++] & 0xFF ^ (int)this.crc & 0xFF] ^ this.crc >>> 8L; 
  }
  
  public byte[] finish() {
    long l = this.crc ^ 0xFFFFFFFFFFFFFFFFL;
    this.crc = -1L;
    byte[] arrayOfByte = new byte[8];
    for (byte b = 0; b < arrayOfByte.length; b++)
      arrayOfByte[b] = (byte)(int)(l >> b * 8); 
    return arrayOfByte;
  }
  
  static {
    for (byte b = 0; b < 4; b++) {
      for (byte b1 = 0; b1 < 'Ā'; b1++) {
        long l = (b == 0) ? b1 : TABLE[b - 1][b1];
        for (byte b2 = 0; b2 < 8; b2++) {
          if ((l & 0x1L) == 1L) {
            l = l >>> 1L ^ 0xC96C5795D7870F42L;
          } else {
            l >>>= 1L;
          } 
        } 
        TABLE[b][b1] = l;
      } 
    } 
  }
}
