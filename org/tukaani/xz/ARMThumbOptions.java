package org.tukaani.xz;

import java.io.InputStream;
import org.tukaani.xz.simple.ARMThumb;
import org.tukaani.xz.simple.SimpleFilter;

public class ARMThumbOptions extends BCJOptions {
  private static final int ALIGNMENT = 2;
  
  public ARMThumbOptions() {
    super(2);
  }
  
  public FinishableOutputStream getOutputStream(FinishableOutputStream paramFinishableOutputStream, ArrayCache paramArrayCache) {
    return new SimpleOutputStream(paramFinishableOutputStream, (SimpleFilter)new ARMThumb(true, this.startOffset));
  }
  
  public InputStream getInputStream(InputStream paramInputStream, ArrayCache paramArrayCache) {
    return new SimpleInputStream(paramInputStream, (SimpleFilter)new ARMThumb(false, this.startOffset));
  }
  
  FilterEncoder getFilterEncoder() {
    return new BCJEncoder(this, 8L);
  }
}
