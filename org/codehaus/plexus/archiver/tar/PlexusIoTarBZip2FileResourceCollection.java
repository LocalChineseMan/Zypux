package org.codehaus.plexus.archiver.tar;

import java.io.File;
import javax.inject.Named;

@Named("tar.bz2")
public class PlexusIoTarBZip2FileResourceCollection extends PlexusIoTarFileResourceCollection {
  protected TarFile newTarFile(File file) {
    return new BZip2TarFile(file);
  }
}
