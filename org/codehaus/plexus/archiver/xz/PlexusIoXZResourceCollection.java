package org.codehaus.plexus.archiver.xz;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import javax.inject.Named;
import org.codehaus.plexus.archiver.util.Streams;
import org.codehaus.plexus.components.io.attributes.FileAttributes;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.resources.PlexusIoCompressedFileResourceCollection;

@Named("xz")
public class PlexusIoXZResourceCollection extends PlexusIoCompressedFileResourceCollection {
  protected PlexusIoResourceAttributes getAttributes(File file) throws IOException {
    return (PlexusIoResourceAttributes)new FileAttributes(file, new HashMap<>(), new HashMap<>());
  }
  
  protected String getDefaultExtension() {
    return ".xz";
  }
  
  protected InputStream getInputStream(File file) throws IOException {
    return (InputStream)XZUnArchiver.getXZInputStream(Streams.fileInputStream(file));
  }
}
