package org.codehaus.plexus.archiver.manager;

import java.io.File;
import javax.annotation.Nonnull;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.components.io.resources.PlexusIoResourceCollection;

public interface ArchiverManager {
  @Nonnull
  Archiver getArchiver(@Nonnull String paramString) throws NoSuchArchiverException;
  
  @Nonnull
  Archiver getArchiver(@Nonnull File paramFile) throws NoSuchArchiverException;
  
  @Nonnull
  UnArchiver getUnArchiver(@Nonnull String paramString) throws NoSuchArchiverException;
  
  @Nonnull
  UnArchiver getUnArchiver(@Nonnull File paramFile) throws NoSuchArchiverException;
  
  @Nonnull
  PlexusIoResourceCollection getResourceCollection(@Nonnull File paramFile) throws NoSuchArchiverException;
  
  @Nonnull
  PlexusIoResourceCollection getResourceCollection(String paramString) throws NoSuchArchiverException;
}
