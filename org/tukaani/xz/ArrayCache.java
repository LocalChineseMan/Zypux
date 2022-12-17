package org.tukaani.xz;

public class ArrayCache {
  private static final ArrayCache dummyCache = new ArrayCache();
  
  private static volatile ArrayCache defaultCache = dummyCache;
  
  public static ArrayCache getDummyCache() {
    return dummyCache;
  }
  
  public static ArrayCache getDefaultCache() {
    return defaultCache;
  }
  
  public static void setDefaultCache(ArrayCache paramArrayCache) {
    if (paramArrayCache == null)
      throw new NullPointerException(); 
    defaultCache = paramArrayCache;
  }
  
  public byte[] getByteArray(int paramInt, boolean paramBoolean) {
    return new byte[paramInt];
  }
  
  public void putArray(byte[] paramArrayOfbyte) {}
  
  public int[] getIntArray(int paramInt, boolean paramBoolean) {
    return new int[paramInt];
  }
  
  public void putArray(int[] paramArrayOfint) {}
}
