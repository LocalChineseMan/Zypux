package org.tukaani.xz;

import java.util.ArrayList;
import java.util.List;

public class ResettableArrayCache extends ArrayCache {
  private final ArrayCache arrayCache;
  
  private final List<byte[]> byteArrays;
  
  private final List<int[]> intArrays;
  
  public ResettableArrayCache(ArrayCache paramArrayCache) {
    this.arrayCache = paramArrayCache;
    if (paramArrayCache == ArrayCache.getDummyCache()) {
      this.byteArrays = null;
      this.intArrays = null;
    } else {
      this.byteArrays = (List)new ArrayList<>();
      this.intArrays = (List)new ArrayList<>();
    } 
  }
  
  public byte[] getByteArray(int paramInt, boolean paramBoolean) {
    byte[] arrayOfByte = this.arrayCache.getByteArray(paramInt, paramBoolean);
    if (this.byteArrays != null)
      synchronized (this.byteArrays) {
        this.byteArrays.add(arrayOfByte);
      }  
    return arrayOfByte;
  }
  
  public void putArray(byte[] paramArrayOfbyte) {
    if (this.byteArrays != null) {
      synchronized (this.byteArrays) {
        int i = this.byteArrays.lastIndexOf(paramArrayOfbyte);
        if (i != -1)
          this.byteArrays.remove(i); 
      } 
      this.arrayCache.putArray(paramArrayOfbyte);
    } 
  }
  
  public int[] getIntArray(int paramInt, boolean paramBoolean) {
    int[] arrayOfInt = this.arrayCache.getIntArray(paramInt, paramBoolean);
    if (this.intArrays != null)
      synchronized (this.intArrays) {
        this.intArrays.add(arrayOfInt);
      }  
    return arrayOfInt;
  }
  
  public void putArray(int[] paramArrayOfint) {
    if (this.intArrays != null) {
      synchronized (this.intArrays) {
        int i = this.intArrays.lastIndexOf(paramArrayOfint);
        if (i != -1)
          this.intArrays.remove(i); 
      } 
      this.arrayCache.putArray(paramArrayOfint);
    } 
  }
  
  public void reset() {
    if (this.byteArrays != null) {
      synchronized (this.byteArrays) {
        for (int i = this.byteArrays.size() - 1; i >= 0; i--)
          this.arrayCache.putArray(this.byteArrays.get(i)); 
        this.byteArrays.clear();
      } 
      synchronized (this.intArrays) {
        for (int i = this.intArrays.size() - 1; i >= 0; i--)
          this.arrayCache.putArray(this.intArrays.get(i)); 
        this.intArrays.clear();
      } 
    } 
  }
}
