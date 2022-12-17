package org.codehaus.plexus.components.io.resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import org.apache.commons.io.output.DeferredFileOutputStream;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.functions.ContentSupplier;
import org.codehaus.plexus.components.io.functions.InputStreamTransformer;
import org.codehaus.plexus.components.io.functions.SymlinkDestinationSupplier;

public class PlexusIoSymlinkResource extends PlexusIoFileResource implements SymlinkDestinationSupplier {
  private final String symLinkDestination;
  
  private final PlexusIoFileResource targetResource;
  
  PlexusIoSymlinkResource(@Nonnull File symlinkfile, String name, @Nonnull PlexusIoResourceAttributes attrs) throws IOException {
    this(symlinkfile, name, attrs, symlinkfile.toPath());
  }
  
  PlexusIoSymlinkResource(@Nonnull File symlinkfile, String name, @Nonnull PlexusIoResourceAttributes attrs, Path linkPath) throws IOException {
    this(symlinkfile, name, attrs, linkPath, Files.readSymbolicLink(linkPath));
  }
  
  private PlexusIoSymlinkResource(@Nonnull File symlinkfile, String name, @Nonnull PlexusIoResourceAttributes attrs, Path path, Path linkPath) throws IOException {
    this(symlinkfile, name, attrs, linkPath.toString(), 
        (PlexusIoFileResource)ResourceFactory.createResource(path.resolveSibling(linkPath).toFile()));
  }
  
  private PlexusIoSymlinkResource(@Nonnull File symlinkfile, String name, @Nonnull PlexusIoResourceAttributes attrs, String symLinkDestination, PlexusIoFileResource targetResource) throws IOException {
    super(symlinkfile, name, attrs, targetResource.getFileAttributes(), (ContentSupplier)null, (InputStreamTransformer)null);
    this.symLinkDestination = symLinkDestination;
    this.targetResource = targetResource;
  }
  
  public String getSymlinkDestination() throws IOException {
    return this.targetResource.getName();
  }
  
  public PlexusIoResource getTarget() {
    return this.targetResource;
  }
  
  public PlexusIoResource getLink() throws IOException {
    return new PlexusIoFileResource(getFile(), getName(), getAttributes());
  }
  
  public long getSize() {
    DeferredFileOutputStream dfos = getDfos();
    if (dfos == null)
      return this.targetResource.getSize(); 
    if (dfos.isInMemory())
      return dfos.getByteCount(); 
    return dfos.getFile().length();
  }
  
  public boolean isDirectory() {
    return this.targetResource.isDirectory();
  }
  
  public boolean isExisting() {
    return this.targetResource.isExisting();
  }
  
  public boolean isFile() {
    return this.targetResource.isFile();
  }
  
  public long getLastModified() {
    return this.targetResource.getLastModified();
  }
  
  @Nonnull
  public PlexusIoResourceAttributes getAttributes() {
    return super.getAttributes();
  }
}
