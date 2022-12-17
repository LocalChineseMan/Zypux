package org.codehaus.plexus.archiver.util;

import java.io.File;
import java.io.IOException;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.components.io.attributes.AttributeUtils;
import org.codehaus.plexus.util.Os;
import org.slf4j.Logger;

public final class ArchiveEntryUtils {
  @Deprecated
  public static void chmod(File file, int mode, Logger logger, boolean useJvmChmod) throws ArchiverException {
    chmod(file, mode);
  }
  
  @Deprecated
  public static void chmod(File file, int mode, Logger logger) throws ArchiverException {
    chmod(file, mode);
  }
  
  public static void chmod(File file, int mode) throws ArchiverException {
    if (!Os.isFamily("unix"))
      return; 
    try {
      AttributeUtils.chmod(file, mode);
    } catch (IOException e) {
      throw new ArchiverException("Failed setting file attributes", e);
    } 
  }
}
