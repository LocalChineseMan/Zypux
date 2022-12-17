package org.tukaani.xz;

import java.io.IOException;
import java.io.InputStream;

public abstract class FilterOptions implements Cloneable {
  public static int getEncoderMemoryUsage(FilterOptions[] paramArrayOfFilterOptions) {
    int i = 0;
    for (byte b = 0; b < paramArrayOfFilterOptions.length; b++)
      i += paramArrayOfFilterOptions[b].getEncoderMemoryUsage(); 
    return i;
  }
  
  public static int getDecoderMemoryUsage(FilterOptions[] paramArrayOfFilterOptions) {
    int i = 0;
    for (byte b = 0; b < paramArrayOfFilterOptions.length; b++)
      i += paramArrayOfFilterOptions[b].getDecoderMemoryUsage(); 
    return i;
  }
  
  public abstract int getEncoderMemoryUsage();
  
  public FinishableOutputStream getOutputStream(FinishableOutputStream paramFinishableOutputStream) {
    return getOutputStream(paramFinishableOutputStream, ArrayCache.getDefaultCache());
  }
  
  public abstract FinishableOutputStream getOutputStream(FinishableOutputStream paramFinishableOutputStream, ArrayCache paramArrayCache);
  
  public abstract int getDecoderMemoryUsage();
  
  public InputStream getInputStream(InputStream paramInputStream) throws IOException {
    return getInputStream(paramInputStream, ArrayCache.getDefaultCache());
  }
  
  public abstract InputStream getInputStream(InputStream paramInputStream, ArrayCache paramArrayCache) throws IOException;
  
  abstract FilterEncoder getFilterEncoder();
}
