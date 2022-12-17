package org.iq80.snappy;

import java.lang.reflect.Field;
import sun.misc.Unsafe;

class UnsafeMemory implements Memory {
  private static final Unsafe unsafe;
  
  static {
    try {
      Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
      theUnsafe.setAccessible(true);
      unsafe = (Unsafe)theUnsafe.get((Object)null);
      (new UnsafeMemory()).copyMemory(new byte[1], 0, new byte[1], 0, 1);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  private static final long BYTE_ARRAY_OFFSET = unsafe.arrayBaseOffset(byte[].class);
  
  private static final long SHORT_ARRAY_OFFSET = unsafe.arrayBaseOffset(short[].class);
  
  private static final long SHORT_ARRAY_STRIDE = unsafe.arrayIndexScale(short[].class);
  
  public boolean fastAccessSupported() {
    return true;
  }
  
  public int lookupShort(short[] data, int index) {
    assert index >= 0;
    assert index <= data.length;
    return unsafe.getShort(data, SHORT_ARRAY_OFFSET + index * SHORT_ARRAY_STRIDE) & 0xFFFF;
  }
  
  public int loadByte(byte[] data, int index) {
    assert index >= 0;
    assert index <= data.length;
    return unsafe.getByte(data, BYTE_ARRAY_OFFSET + index) & 0xFF;
  }
  
  public int loadInt(byte[] data, int index) {
    assert index >= 0;
    assert index + 4 <= data.length;
    return unsafe.getInt(data, BYTE_ARRAY_OFFSET + index);
  }
  
  public void copyLong(byte[] src, int srcIndex, byte[] dest, int destIndex) {
    assert srcIndex >= 0;
    assert srcIndex + 8 <= src.length;
    assert destIndex >= 0;
    assert destIndex + 8 <= dest.length;
    long value = unsafe.getLong(src, BYTE_ARRAY_OFFSET + srcIndex);
    unsafe.putLong(dest, BYTE_ARRAY_OFFSET + destIndex, value);
  }
  
  public long loadLong(byte[] data, int index) {
    assert index > 0;
    assert index + 4 < data.length;
    return unsafe.getLong(data, BYTE_ARRAY_OFFSET + index);
  }
  
  public void copyMemory(byte[] input, int inputIndex, byte[] output, int outputIndex, int length) {
    assert inputIndex >= 0;
    assert inputIndex + length <= input.length;
    assert outputIndex >= 0;
    assert outputIndex + length <= output.length;
    unsafe.copyMemory(input, BYTE_ARRAY_OFFSET + inputIndex, output, BYTE_ARRAY_OFFSET + outputIndex, length);
  }
}
