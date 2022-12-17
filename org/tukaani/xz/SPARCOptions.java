package org.tukaani.xz;

import java.io.InputStream;
import org.tukaani.xz.simple.SPARC;
import org.tukaani.xz.simple.SimpleFilter;

public class SPARCOptions extends BCJOptions {
  private static final int ALIGNMENT = 4;
  
  public SPARCOptions() {
    super(4);
  }
  
  public FinishableOutputStream getOutputStream(FinishableOutputStream paramFinishableOutputStream, ArrayCache paramArrayCache) {
    return new SimpleOutputStream(paramFinishableOutputStream, (SimpleFilter)new SPARC(true, this.startOffset));
  }
  
  public InputStream getInputStream(InputStream paramInputStream, ArrayCache paramArrayCache) {
    return new SimpleInputStream(paramInputStream, (SimpleFilter)new SPARC(false, this.startOffset));
  }
  
  FilterEncoder getFilterEncoder() {
    return new BCJEncoder(this, 9L);
  }
}
