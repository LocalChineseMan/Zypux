package org.codehaus.plexus.components.io.resources;

import java.io.File;
import java.io.IOException;
import org.codehaus.plexus.components.io.attributes.FileAttributes;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributeUtils;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;
import org.codehaus.plexus.components.io.functions.ContentSupplier;
import org.codehaus.plexus.components.io.functions.InputStreamTransformer;

public class ResourceFactory {
  public static PlexusIoResource createResource(File f) throws IOException {
    return createResource(f, PlexusIoFileResource.getName(f), null, null, PlexusIoResourceAttributeUtils.getFileAttributes(f));
  }
  
  public static PlexusIoResource createResource(File f, String name) throws IOException {
    return createResource(f, name, null, null, PlexusIoResourceAttributeUtils.getFileAttributes(f));
  }
  
  public static PlexusIoResource createResource(File f, String name, ContentSupplier contentSupplier, PlexusIoResourceAttributes attributes) throws IOException {
    return createResource(f, name, contentSupplier, null, attributes);
  }
  
  public static PlexusIoResource createResource(File f, InputStreamTransformer inputStreamTransformer) throws IOException {
    return createResource(f, PlexusIoFileResource.getName(f), null, inputStreamTransformer, PlexusIoResourceAttributeUtils.getFileAttributes(f));
  }
  
  public static PlexusIoResource createResource(File f, String name, ContentSupplier contentSupplier, InputStreamTransformer inputStreamTransformer) throws IOException {
    return createResource(f, name, contentSupplier, inputStreamTransformer, PlexusIoResourceAttributeUtils.getFileAttributes(f));
  }
  
  public static PlexusIoResource createResource(File f, String name, ContentSupplier contentSupplier, InputStreamTransformer inputStreamTransformer, PlexusIoResourceAttributes attributes) throws IOException {
    boolean symbolicLink = attributes.isSymbolicLink();
    return symbolicLink ? new PlexusIoSymlinkResource(f, name, attributes) : 
      new PlexusIoFileResource(f, name, attributes, new FileAttributes(f, true), contentSupplier, inputStreamTransformer);
  }
}
