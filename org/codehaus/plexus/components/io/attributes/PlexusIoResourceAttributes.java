package org.codehaus.plexus.components.io.attributes;

import javax.annotation.Nullable;

public interface PlexusIoResourceAttributes {
  public static final int UNKNOWN_OCTAL_MODE = -1;
  
  boolean isOwnerReadable();
  
  boolean isOwnerWritable();
  
  boolean isOwnerExecutable();
  
  boolean isGroupReadable();
  
  boolean isGroupWritable();
  
  boolean isGroupExecutable();
  
  boolean isWorldReadable();
  
  boolean isWorldWritable();
  
  boolean isWorldExecutable();
  
  Integer getUserId();
  
  @Nullable
  Integer getGroupId();
  
  @Nullable
  String getUserName();
  
  @Nullable
  String getGroupName();
  
  int getOctalMode();
  
  boolean isSymbolicLink();
}
