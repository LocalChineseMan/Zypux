package org.iq80.snappy;

class SlowMemory implements Memory {
  public boolean fastAccessSupported() {
    return false;
  }
  
  public int lookupShort(short[] data, int index) {
    return data[index] & 0xFFFF;
  }
  
  public int loadByte(byte[] data, int index) {
    return data[index] & 0xFF;
  }
  
  public int loadInt(byte[] data, int index) {
    return data[index] & 0xFF | (data[index + 1] & 0xFF) << 8 | (data[index + 2] & 0xFF) << 16 | (data[index + 3] & 0xFF) << 24;
  }
  
  public void copyLong(byte[] src, int srcIndex, byte[] dest, int destIndex) {
    for (int i = 0; i < 8; i++)
      dest[destIndex + i] = src[srcIndex + i]; 
  }
  
  public long loadLong(byte[] data, int index) {
    return data[index] & 0xFFL | (data[index + 1] & 0xFFL) << 8L | (data[index + 2] & 0xFFL) << 16L | (data[index + 3] & 0xFFL) << 24L | (data[index + 4] & 0xFFL) << 32L | (data[index + 5] & 0xFFL) << 40L | (data[index + 6] & 0xFFL) << 48L | (data[index + 7] & 0xFFL) << 56L;
  }
  
  public void copyMemory(byte[] input, int inputIndex, byte[] output, int outputIndex, int length) {
    System.arraycopy(input, inputIndex, output, outputIndex, length);
  }
}
