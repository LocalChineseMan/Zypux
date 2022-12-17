package org.codehaus.plexus.components.io.resources;

import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributeUtils;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;

public abstract class AbstractPlexusIoResourceCollectionWithAttributes extends AbstractPlexusIoResourceCollection {
  private PlexusIoResourceAttributes defaultFileAttributes;
  
  private PlexusIoResourceAttributes defaultDirAttributes;
  
  private PlexusIoResourceAttributes overrideFileAttributes;
  
  private PlexusIoResourceAttributes overrideDirAttributes;
  
  protected PlexusIoResourceAttributes getDefaultFileAttributes() {
    return this.defaultFileAttributes;
  }
  
  protected void setDefaultFileAttributes(PlexusIoResourceAttributes defaultFileAttributes) {
    this.defaultFileAttributes = defaultFileAttributes;
  }
  
  protected PlexusIoResourceAttributes getDefaultDirAttributes() {
    return this.defaultDirAttributes;
  }
  
  protected void setDefaultDirAttributes(PlexusIoResourceAttributes defaultDirAttributes) {
    this.defaultDirAttributes = defaultDirAttributes;
  }
  
  protected PlexusIoResourceAttributes getOverrideFileAttributes() {
    return this.overrideFileAttributes;
  }
  
  protected void setOverrideFileAttributes(PlexusIoResourceAttributes overrideFileAttributes) {
    this.overrideFileAttributes = overrideFileAttributes;
  }
  
  protected PlexusIoResourceAttributes getOverrideDirAttributes() {
    return this.overrideDirAttributes;
  }
  
  protected void setOverrideDirAttributes(PlexusIoResourceAttributes overrideDirAttributes) {
    this.overrideDirAttributes = overrideDirAttributes;
  }
  
  protected PlexusIoResourceAttributes mergeAttributes(PlexusIoResourceAttributes currentAttrs, boolean isDirectory) {
    if (isDirectory) {
      currentAttrs = PlexusIoResourceAttributeUtils.mergeAttributes(getOverrideDirAttributes(), currentAttrs, 
          getDefaultDirAttributes());
    } else {
      currentAttrs = PlexusIoResourceAttributeUtils.mergeAttributes(getOverrideFileAttributes(), currentAttrs, 
          getDefaultFileAttributes());
    } 
    return currentAttrs;
  }
}
