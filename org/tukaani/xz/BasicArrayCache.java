package org.tukaani.xz;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class BasicArrayCache extends ArrayCache {
  private static final int CACHEABLE_SIZE_MIN = 32768;
  
  private static final int STACKS_MAX = 32;
  
  private static final int ELEMENTS_PER_STACK = 512;
  
  private final CacheMap<byte[]> byteArrayCache = (CacheMap)new CacheMap<>();
  
  private final CacheMap<int[]> intArrayCache = (CacheMap)new CacheMap<>();
  
  public static BasicArrayCache getInstance() {
    return LazyHolder.INSTANCE;
  }
  
  private static <T> T getArray(CacheMap<T> paramCacheMap, int paramInt) {
    CyclicStack<Reference<T>> cyclicStack;
    if (paramInt < 32768)
      return null; 
    synchronized (paramCacheMap) {
      cyclicStack = paramCacheMap.get(Integer.valueOf(paramInt));
    } 
    if (cyclicStack == null)
      return null; 
    while (true) {
      Reference<Object> reference = (Reference)cyclicStack.pop();
      if (reference == null)
        return null; 
      T t = (T)reference.get();
      if (t != null)
        return t; 
    } 
  }
  
  private static <T> void putArray(CacheMap<T> paramCacheMap, T paramT, int paramInt) {
    CyclicStack<Reference<T>> cyclicStack;
    if (paramInt < 32768)
      return; 
    synchronized (paramCacheMap) {
      cyclicStack = paramCacheMap.get(Integer.valueOf(paramInt));
      if (cyclicStack == null) {
        cyclicStack = new CyclicStack<>();
        paramCacheMap.put(Integer.valueOf(paramInt), cyclicStack);
      } 
    } 
    cyclicStack.push(new SoftReference<>(paramT));
  }
  
  public byte[] getByteArray(int paramInt, boolean paramBoolean) {
    byte[] arrayOfByte = getArray((CacheMap)this.byteArrayCache, paramInt);
    if (arrayOfByte == null) {
      arrayOfByte = new byte[paramInt];
    } else if (paramBoolean) {
      Arrays.fill(arrayOfByte, (byte)0);
    } 
    return arrayOfByte;
  }
  
  public void putArray(byte[] paramArrayOfbyte) {
    putArray((CacheMap)this.byteArrayCache, paramArrayOfbyte, paramArrayOfbyte.length);
  }
  
  public int[] getIntArray(int paramInt, boolean paramBoolean) {
    int[] arrayOfInt = getArray((CacheMap)this.intArrayCache, paramInt);
    if (arrayOfInt == null) {
      arrayOfInt = new int[paramInt];
    } else if (paramBoolean) {
      Arrays.fill(arrayOfInt, 0);
    } 
    return arrayOfInt;
  }
  
  public void putArray(int[] paramArrayOfint) {
    putArray((CacheMap)this.intArrayCache, paramArrayOfint, paramArrayOfint.length);
  }
  
  private static class CacheMap<T> extends LinkedHashMap<Integer, CyclicStack<Reference<T>>> {
    private static final long serialVersionUID = 1L;
    
    public CacheMap() {
      super(64, 0.75F, true);
    }
    
    protected boolean removeEldestEntry(Map.Entry<Integer, BasicArrayCache.CyclicStack<Reference<T>>> param1Entry) {
      return (size() > 32);
    }
  }
  
  private static final class LazyHolder {
    static final BasicArrayCache INSTANCE = new BasicArrayCache();
  }
  
  private static class CyclicStack<T> {
    private final T[] elements = (T[])new Object[512];
    
    private int pos = 0;
    
    private CyclicStack() {}
    
    public synchronized T pop() {
      T t = this.elements[this.pos];
      this.elements[this.pos] = null;
      this.pos = this.pos - 1 & 0x1FF;
      return t;
    }
    
    public synchronized void push(T param1T) {
      this.pos = this.pos + 1 & 0x1FF;
      this.elements[this.pos] = param1T;
    }
  }
}
