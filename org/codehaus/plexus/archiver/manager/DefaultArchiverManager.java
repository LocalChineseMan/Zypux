package org.codehaus.plexus.archiver.manager;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.components.io.resources.PlexusIoResourceCollection;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

@Singleton
@Named
public class DefaultArchiverManager implements ArchiverManager {
  private final Map<String, Provider<Archiver>> archivers;
  
  private final Map<String, Provider<UnArchiver>> unArchivers;
  
  private final Map<String, Provider<PlexusIoResourceCollection>> plexusIoResourceCollections;
  
  @Inject
  public DefaultArchiverManager(Map<String, Provider<Archiver>> archivers, Map<String, Provider<UnArchiver>> unArchivers, Map<String, Provider<PlexusIoResourceCollection>> plexusIoResourceCollections) {
    this.archivers = Objects.<Map<String, Provider<Archiver>>>requireNonNull(archivers);
    this.unArchivers = Objects.<Map<String, Provider<UnArchiver>>>requireNonNull(unArchivers);
    this.plexusIoResourceCollections = Objects.<Map<String, Provider<PlexusIoResourceCollection>>>requireNonNull(plexusIoResourceCollections);
  }
  
  @Nonnull
  public Archiver getArchiver(@Nonnull String archiverName) throws NoSuchArchiverException {
    Objects.requireNonNull(archiverName);
    Provider<Archiver> archiver = this.archivers.get(archiverName);
    if (archiver == null)
      throw new NoSuchArchiverException(archiverName); 
    return (Archiver)archiver.get();
  }
  
  @Nonnull
  public UnArchiver getUnArchiver(@Nonnull String unArchiverName) throws NoSuchArchiverException {
    Objects.requireNonNull(unArchiverName);
    Provider<UnArchiver> unArchiver = this.unArchivers.get(unArchiverName);
    if (unArchiver == null)
      throw new NoSuchArchiverException(unArchiverName); 
    return (UnArchiver)unArchiver.get();
  }
  
  @Nonnull
  public PlexusIoResourceCollection getResourceCollection(String resourceCollectionName) throws NoSuchArchiverException {
    Objects.requireNonNull(resourceCollectionName);
    Provider<PlexusIoResourceCollection> resourceCollection = this.plexusIoResourceCollections.get(resourceCollectionName);
    if (resourceCollection == null)
      throw new NoSuchArchiverException(resourceCollectionName); 
    return (PlexusIoResourceCollection)resourceCollection.get();
  }
  
  @Nonnull
  private static String getFileExtention(@Nonnull File file) {
    String path = file.getAbsolutePath();
    String archiveExt = FileUtils.getExtension(path).toLowerCase(Locale.ENGLISH);
    if ("gz".equals(archiveExt) || "bz2"
      .equals(archiveExt) || "xz"
      .equals(archiveExt) || "snappy"
      .equals(archiveExt)) {
      String[] tokens = StringUtils.split(path, ".");
      if (tokens.length > 2 && "tar".equals(tokens[tokens.length - 2].toLowerCase(Locale.ENGLISH)))
        archiveExt = "tar." + archiveExt; 
    } 
    return archiveExt;
  }
  
  @Nonnull
  public Archiver getArchiver(@Nonnull File file) throws NoSuchArchiverException {
    return getArchiver(getFileExtention(file));
  }
  
  @Nonnull
  public UnArchiver getUnArchiver(@Nonnull File file) throws NoSuchArchiverException {
    return getUnArchiver(getFileExtention(file));
  }
  
  @Nonnull
  public PlexusIoResourceCollection getResourceCollection(@Nonnull File file) throws NoSuchArchiverException {
    return getResourceCollection(getFileExtention(file));
  }
}
