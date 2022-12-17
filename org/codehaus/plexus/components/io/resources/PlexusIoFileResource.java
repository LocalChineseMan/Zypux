package org.codehaus.plexus.components.io.resources;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.attribute.FileTime;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.DeferredFileOutputStream;
import org.codehaus.plexus.components.io.attributes.AttributeUtils;
import org.codehaus.plexus.components.io.attributes.FileAttributes;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.functions.ContentSupplier;
import org.codehaus.plexus.components.io.functions.FileSupplier;
import org.codehaus.plexus.components.io.functions.InputStreamTransformer;
import org.codehaus.plexus.components.io.functions.ResourceAttributeSupplier;

public class PlexusIoFileResource extends AbstractPlexusIoResource implements ResourceAttributeSupplier, FileSupplier {
  @Nonnull
  private final File file;
  
  @Nonnull
  private final PlexusIoResourceAttributes attributes;
  
  @Nonnull
  private final FileAttributes fileAttributes;
  
  private final ContentSupplier contentSupplier;
  
  private final DeferredFileOutputStream dfos;
  
  protected PlexusIoFileResource(@Nonnull File file, @Nonnull String name, @Nonnull PlexusIoResourceAttributes attrs) throws IOException {
    this(file, name, attrs, null, null);
  }
  
  PlexusIoFileResource(@Nonnull File file, @Nonnull String name, @Nonnull PlexusIoResourceAttributes attrs, ContentSupplier contentSupplier, InputStreamTransformer streamTransformer) throws IOException {
    this(file, name, attrs, new FileAttributes(file, true), contentSupplier, streamTransformer);
  }
  
  PlexusIoFileResource(@Nonnull File file, @Nonnull String name, @Nonnull PlexusIoResourceAttributes attrs, @Nonnull FileAttributes fileAttributes, ContentSupplier contentSupplier, InputStreamTransformer streamTransformer) throws IOException {
    super(name, fileAttributes.getLastModifiedTime().toMillis(), fileAttributes.getSize(), fileAttributes
        .isRegularFile(), fileAttributes.isDirectory(), (fileAttributes
        .isRegularFile() || fileAttributes.isDirectory() || fileAttributes.isSymbolicLink() || fileAttributes.isOther()));
    this.file = file;
    this.attributes = Objects.<PlexusIoResourceAttributes>requireNonNull(attrs, "attributes is null for file " + file.getName());
    this.fileAttributes = Objects.<FileAttributes>requireNonNull(fileAttributes, "fileAttributes is null for file " + file.getName());
    this.contentSupplier = (contentSupplier != null) ? contentSupplier : getRootContentSupplier(file);
    boolean hasTransformer = (streamTransformer != null && streamTransformer != identityTransformer);
    InputStreamTransformer transToUse = (streamTransformer != null) ? streamTransformer : identityTransformer;
    this.dfos = (hasTransformer && file.isFile()) ? asDeferredStream(this.contentSupplier, transToUse, this) : null;
  }
  
  private static DeferredFileOutputStream asDeferredStream(@Nonnull ContentSupplier supplier, @Nonnull InputStreamTransformer transToUse, PlexusIoResource resource) throws IOException {
    DeferredFileOutputStream dfos = new DeferredFileOutputStream(5000000, "p-archiver", null, null);
    InputStream inputStream = supplier.getContents();
    InputStream transformed = transToUse.transform(resource, inputStream);
    IOUtils.copy(transformed, (OutputStream)dfos);
    IOUtils.closeQuietly(inputStream);
    IOUtils.closeQuietly(transformed);
    return dfos;
  }
  
  private static ContentSupplier getRootContentSupplier(final File file) {
    return new ContentSupplier() {
        public InputStream getContents() throws IOException {
          return new FileInputStream(file);
        }
      };
  }
  
  public static String getName(File file) {
    return file.getPath().replace('\\', '/');
  }
  
  @Nonnull
  public File getFile() {
    return this.file;
  }
  
  @Nonnull
  public InputStream getContents() throws IOException {
    if (this.dfos == null)
      return this.contentSupplier.getContents(); 
    if (this.dfos.isInMemory())
      return new ByteArrayInputStream(this.dfos.getData()); 
    return new FileInputStream(this.dfos.getFile()) {
        public void close() throws IOException {
          super.close();
          PlexusIoFileResource.this.dfos.getFile().delete();
        }
      };
  }
  
  @Nonnull
  public URL getURL() throws IOException {
    return getFile().toURI().toURL();
  }
  
  public long getSize() {
    if (this.dfos == null)
      return this.fileAttributes.getSize(); 
    if (this.dfos.isInMemory())
      return this.dfos.getByteCount(); 
    return this.dfos.getFile().length();
  }
  
  public boolean isDirectory() {
    return this.fileAttributes.isDirectory();
  }
  
  public boolean isExisting() {
    if (this.attributes instanceof FileAttributes)
      return true; 
    return getFile().exists();
  }
  
  public boolean isFile() {
    return this.fileAttributes.isRegularFile();
  }
  
  @Nonnull
  public PlexusIoResourceAttributes getAttributes() {
    return this.attributes;
  }
  
  @Nonnull
  public FileAttributes getFileAttributes() {
    return this.fileAttributes;
  }
  
  public long getLastModified() {
    FileTime lastModified = this.fileAttributes.getLastModifiedTime();
    if (lastModified != null)
      return lastModified.toMillis(); 
    return AttributeUtils.getLastModified(getFile());
  }
  
  public boolean isSymbolicLink() {
    return getAttributes().isSymbolicLink();
  }
  
  protected DeferredFileOutputStream getDfos() {
    return this.dfos;
  }
  
  private static final InputStreamTransformer identityTransformer = AbstractPlexusIoResourceCollection.identityTransformer;
}
