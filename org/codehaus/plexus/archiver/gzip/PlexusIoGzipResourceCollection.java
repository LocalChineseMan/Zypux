package org.codehaus.plexus.archiver.gzip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import javax.annotation.Nonnull;
import javax.inject.Named;
import org.codehaus.plexus.archiver.util.Streams;
import org.codehaus.plexus.components.io.attributes.FileAttributes;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.resources.PlexusIoCompressedFileResourceCollection;

@Named("gzip")
public class PlexusIoGzipResourceCollection extends PlexusIoCompressedFileResourceCollection {
  protected String getDefaultExtension() {
    return ".gz";
  }
  
  @Nonnull
  protected InputStream getInputStream(File file) throws IOException {
    return Streams.bufferedInputStream(new GZIPInputStream(Streams.fileInputStream(file)));
  }
  
  protected PlexusIoResourceAttributes getAttributes(File file) throws IOException {
    return (PlexusIoResourceAttributes)new FileAttributes(file, new HashMap<>(), new HashMap<>());
  }
}
