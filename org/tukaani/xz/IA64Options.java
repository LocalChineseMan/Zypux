package org.tukaani.xz;

import java.io.InputStream;
import org.tukaani.xz.simple.IA64;
import org.tukaani.xz.simple.SimpleFilter;

public class IA64Options extends BCJOptions {
  private static final int ALIGNMENT = 16;
  
  public IA64Options() {
    super(16);
  }
  
  public FinishableOutputStream getOutputStream(FinishableOutputStream paramFinishableOutputStream, ArrayCache paramArrayCache) {
    return new SimpleOutputStream(paramFinishableOutputStream, (SimpleFilter)new IA64(true, this.startOffset));
  }
  
  public InputStream getInputStream(InputStream paramInputStream, ArrayCache paramArrayCache) {
    return new SimpleInputStream(paramInputStream, (SimpleFilter)new IA64(false, this.startOffset));
  }
  
  FilterEncoder getFilterEncoder() {
    return new BCJEncoder(this, 6L);
  }
}
