package org.tukaani.xz;

import java.io.InputStream;
import org.tukaani.xz.simple.ARM;
import org.tukaani.xz.simple.SimpleFilter;

public class ARMOptions extends BCJOptions {
  private static final int ALIGNMENT = 4;
  
  public ARMOptions() {
    super(4);
  }
  
  public FinishableOutputStream getOutputStream(FinishableOutputStream paramFinishableOutputStream, ArrayCache paramArrayCache) {
    return new SimpleOutputStream(paramFinishableOutputStream, (SimpleFilter)new ARM(true, this.startOffset));
  }
  
  public InputStream getInputStream(InputStream paramInputStream, ArrayCache paramArrayCache) {
    return new SimpleInputStream(paramInputStream, (SimpleFilter)new ARM(false, this.startOffset));
  }
  
  FilterEncoder getFilterEncoder() {
    return new BCJEncoder(this, 7L);
  }
}
