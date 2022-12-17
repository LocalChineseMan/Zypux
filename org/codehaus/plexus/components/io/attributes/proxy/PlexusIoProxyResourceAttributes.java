package org.codehaus.plexus.components.io.attributes.proxy;

import javax.annotation.Nullable;
import org.codehaus.plexus.components.io.attributes.PlexusIoResourceAttributes;

public class PlexusIoProxyResourceAttributes implements PlexusIoResourceAttributes {
  PlexusIoResourceAttributes target;
  
  public PlexusIoProxyResourceAttributes(PlexusIoResourceAttributes thisAttr) {
    this.target = thisAttr;
  }
  
  public boolean isOwnerReadable() {
    return this.target.isOwnerReadable();
  }
  
  public int getOctalMode() {
    return this.target.getOctalMode();
  }
  
  public String getUserName() {
    return this.target.getUserName();
  }
  
  public boolean isGroupReadable() {
    return this.target.isGroupReadable();
  }
  
  public boolean isWorldExecutable() {
    return this.target.isWorldExecutable();
  }
  
  @Nullable
  public Integer getGroupId() {
    return this.target.getGroupId();
  }
  
  public boolean isGroupWritable() {
    return this.target.isGroupWritable();
  }
  
  public Integer getUserId() {
    return this.target.getUserId();
  }
  
  public boolean isOwnerWritable() {
    return this.target.isOwnerWritable();
  }
  
  public boolean isOwnerExecutable() {
    return this.target.isOwnerExecutable();
  }
  
  public boolean isSymbolicLink() {
    return this.target.isSymbolicLink();
  }
  
  public boolean isGroupExecutable() {
    return this.target.isGroupExecutable();
  }
  
  public boolean isWorldWritable() {
    return this.target.isWorldWritable();
  }
  
  @Nullable
  public String getGroupName() {
    return this.target.getGroupName();
  }
  
  public boolean isWorldReadable() {
    return this.target.isWorldReadable();
  }
}
