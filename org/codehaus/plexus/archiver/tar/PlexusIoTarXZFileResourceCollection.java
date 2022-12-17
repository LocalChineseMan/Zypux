package org.codehaus.plexus.archiver.tar;

import java.io.File;
import javax.inject.Named;

@Named("tar.xz")
public class PlexusIoTarXZFileResourceCollection extends PlexusIoTarFileResourceCollection {
  protected TarFile newTarFile(File file) {
    return new XZTarFile(file);
  }
}
