package org.iq80.snappy;

interface Memory {
  boolean fastAccessSupported();
  
  int lookupShort(short[] paramArrayOfshort, int paramInt);
  
  int loadByte(byte[] paramArrayOfbyte, int paramInt);
  
  int loadInt(byte[] paramArrayOfbyte, int paramInt);
  
  void copyLong(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2);
  
  long loadLong(byte[] paramArrayOfbyte, int paramInt);
  
  void copyMemory(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2, int paramInt3);
}
