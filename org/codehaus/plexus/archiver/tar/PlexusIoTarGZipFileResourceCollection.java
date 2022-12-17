package org.codehaus.plexus.archiver.tar;

import java.io.File;
import javax.inject.Named;

@Named("tar.gz")
public class PlexusIoTarGZipFileResourceCollection extends PlexusIoTarFileResourceCollection {
  protected TarFile newTarFile(File file) {
    return new GZipTarFile(file);
  }
}
