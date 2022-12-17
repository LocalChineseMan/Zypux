package org.codehaus.plexus.archiver.bzip2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import javax.annotation.Nonnull;
import javax.annotation.WillNotClose;
import javax.inject.Named;
import org.codehaus.plexus.archiver.util.Streams;
import org.codehaus.plexus.components.io.attributes.FileAttributes;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.resources.PlexusIoCompressedFileResourceCollection;

@Named("bzip2")
public class PlexusIoBzip2ResourceCollection extends PlexusIoCompressedFileResourceCollection {
  @Nonnull
  @WillNotClose
  protected InputStream getInputStream(File file) throws IOException {
    return (InputStream)BZip2UnArchiver.getBZip2InputStream(Streams.fileInputStream(file));
  }
  
  protected PlexusIoResourceAttributes getAttributes(File file) throws IOException {
    return (PlexusIoResourceAttributes)new FileAttributes(file, new HashMap<>(), new HashMap<>());
  }
  
  protected String getDefaultExtension() {
    return ".bz2";
  }
}
