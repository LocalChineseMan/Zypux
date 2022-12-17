package org.codehaus.plexus.archiver.tar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import org.codehaus.plexus.archiver.util.Streams;

public class GZipTarFile extends TarFile {
  public GZipTarFile(File file) {
    super(file);
  }
  
  protected InputStream getInputStream(File file) throws IOException {
    return Streams.bufferedInputStream(new GZIPInputStream(super.getInputStream(file)));
  }
}
