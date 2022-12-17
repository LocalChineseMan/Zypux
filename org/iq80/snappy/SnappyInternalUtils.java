package org.iq80.snappy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

final class SnappyInternalUtils {
  private static final Memory memory;
  
  static {
    Memory memoryInstance = null;
    if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
      try {
        Class<? extends Memory> unsafeMemoryClass = SnappyInternalUtils.class.getClassLoader().loadClass("org.iq80.snappy.UnsafeMemory").asSubclass(Memory.class);
        Memory unsafeMemory = unsafeMemoryClass.newInstance();
        if (unsafeMemory.loadInt(new byte[4], 0) == 0)
          memoryInstance = unsafeMemory; 
      } catch (Throwable throwable) {} 
    if (memoryInstance == null)
      try {
        Class<? extends Memory> slowMemoryClass = SnappyInternalUtils.class.getClassLoader().loadClass("org.iq80.snappy.SlowMemory").asSubclass(Memory.class);
        Memory slowMemory = slowMemoryClass.newInstance();
        if (slowMemory.loadInt(new byte[4], 0) == 0) {
          memoryInstance = slowMemory;
        } else {
          throw new AssertionError("SlowMemory class is broken!");
        } 
      } catch (Throwable ignored) {
        throw new AssertionError("Could not find SlowMemory class");
      }  
    memory = memoryInstance;
  }
  
  static final boolean HAS_UNSAFE = memory.fastAccessSupported();
  
  static boolean equals(byte[] left, int leftIndex, byte[] right, int rightIndex, int length) {
    checkPositionIndexes(leftIndex, leftIndex + length, left.length);
    checkPositionIndexes(rightIndex, rightIndex + length, right.length);
    for (int i = 0; i < length; i++) {
      if (left[leftIndex + i] != right[rightIndex + i])
        return false; 
    } 
    return true;
  }
  
  public static int lookupShort(short[] data, int index) {
    return memory.lookupShort(data, index);
  }
  
  public static int loadByte(byte[] data, int index) {
    return memory.loadByte(data, index);
  }
  
  static int loadInt(byte[] data, int index) {
    return memory.loadInt(data, index);
  }
  
  static void copyLong(byte[] src, int srcIndex, byte[] dest, int destIndex) {
    memory.copyLong(src, srcIndex, dest, destIndex);
  }
  
  static long loadLong(byte[] data, int index) {
    return memory.loadLong(data, index);
  }
  
  static void copyMemory(byte[] input, int inputIndex, byte[] output, int outputIndex, int length) {
    memory.copyMemory(input, inputIndex, output, outputIndex, length);
  }
  
  static <T> T checkNotNull(T reference, String errorMessageTemplate, Object... errorMessageArgs) {
    if (reference == null)
      throw new NullPointerException(String.format(errorMessageTemplate, errorMessageArgs)); 
    return reference;
  }
  
  static void checkArgument(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
    if (!expression)
      throw new IllegalArgumentException(String.format(errorMessageTemplate, errorMessageArgs)); 
  }
  
  static void checkPositionIndexes(int start, int end, int size) {
    if (start < 0 || end < start || end > size)
      throw new IndexOutOfBoundsException(badPositionIndexes(start, end, size)); 
  }
  
  static String badPositionIndexes(int start, int end, int size) {
    if (start < 0 || start > size)
      return badPositionIndex(start, size, "start index"); 
    if (end < 0 || end > size)
      return badPositionIndex(end, size, "end index"); 
    return String.format("end index (%s) must not be less than start index (%s)", new Object[] { Integer.valueOf(end), Integer.valueOf(start) });
  }
  
  static String badPositionIndex(int index, int size, String desc) {
    if (index < 0)
      return String.format("%s (%s) must not be negative", new Object[] { desc, Integer.valueOf(index) }); 
    if (size < 0)
      throw new IllegalArgumentException("negative size: " + size); 
    return String.format("%s (%s) must not be greater than size (%s)", new Object[] { desc, Integer.valueOf(index), Integer.valueOf(size) });
  }
  
  static int readBytes(InputStream source, byte[] dest, int offset, int length) throws IOException {
    checkNotNull(source, "source is null", new Object[0]);
    checkNotNull(dest, "dest is null", new Object[0]);
    int lastRead = source.read(dest, offset, length);
    int totalRead = lastRead;
    if (lastRead < length)
      while (totalRead < length && lastRead != -1) {
        lastRead = source.read(dest, offset + totalRead, length - totalRead);
        if (lastRead != -1)
          totalRead += lastRead; 
      }  
    return totalRead;
  }
  
  static int skip(InputStream source, int skip) throws IOException {
    if (skip <= 0)
      return 0; 
    int toSkip = skip - (int)source.skip(skip);
    boolean more = true;
    while (toSkip > 0 && more) {
      int read = source.read();
      if (read == -1) {
        more = false;
        continue;
      } 
      toSkip--;
      toSkip = (int)(toSkip - source.skip(toSkip));
    } 
    int skipped = skip - toSkip;
    return skipped;
  }
}
