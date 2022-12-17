package org.codehaus.plexus.components.io.attributes;

import javax.annotation.Nullable;

public class SimpleResourceAttributes implements PlexusIoResourceAttributes {
  private Integer gid;
  
  private Integer uid;
  
  private String userName;
  
  private String groupName;
  
  private int mode = -1;
  
  private boolean isSymbolicLink;
  
  public SimpleResourceAttributes(Integer uid, String userName, Integer gid, String groupName, int mode) {
    this.uid = uid;
    this.userName = userName;
    this.gid = gid;
    this.groupName = groupName;
    this.mode = mode;
  }
  
  public SimpleResourceAttributes(Integer uid, String userName, Integer gid, String groupName, int mode, boolean isSymbolicLink) {
    this.uid = uid;
    this.userName = userName;
    this.gid = gid;
    this.groupName = groupName;
    this.mode = mode;
    this.isSymbolicLink = isSymbolicLink;
  }
  
  public static PlexusIoResourceAttributes lastResortDummyAttributesForBrokenOS() {
    return new SimpleResourceAttributes();
  }
  
  SimpleResourceAttributes() {}
  
  public int getOctalMode() {
    return this.mode;
  }
  
  @Nullable
  public Integer getGroupId() {
    return this.gid;
  }
  
  @Nullable
  public String getGroupName() {
    return this.groupName;
  }
  
  public Integer getUserId() {
    return this.uid;
  }
  
  public String getUserName() {
    return this.userName;
  }
  
  public boolean isGroupExecutable() {
    return PlexusIoResourceAttributeUtils.isGroupExecutableInOctal(this.mode);
  }
  
  public boolean isGroupReadable() {
    return PlexusIoResourceAttributeUtils.isGroupReadableInOctal(this.mode);
  }
  
  public boolean isGroupWritable() {
    return PlexusIoResourceAttributeUtils.isGroupWritableInOctal(this.mode);
  }
  
  public boolean isOwnerExecutable() {
    return PlexusIoResourceAttributeUtils.isOwnerExecutableInOctal(this.mode);
  }
  
  public boolean isOwnerReadable() {
    return PlexusIoResourceAttributeUtils.isOwnerReadableInOctal(this.mode);
  }
  
  public boolean isOwnerWritable() {
    return PlexusIoResourceAttributeUtils.isOwnerWritableInOctal(this.mode);
  }
  
  public boolean isWorldExecutable() {
    return PlexusIoResourceAttributeUtils.isWorldExecutableInOctal(this.mode);
  }
  
  public boolean isWorldReadable() {
    return PlexusIoResourceAttributeUtils.isWorldReadableInOctal(this.mode);
  }
  
  public boolean isWorldWritable() {
    return PlexusIoResourceAttributeUtils.isWorldWritableInOctal(this.mode);
  }
  
  public String getOctalModeString() {
    return Integer.toString(this.mode, 8);
  }
  
  public PlexusIoResourceAttributes setOctalMode(int mode) {
    this.mode = mode;
    return this;
  }
  
  public PlexusIoResourceAttributes setGroupId(Integer gid) {
    this.gid = gid;
    return this;
  }
  
  public PlexusIoResourceAttributes setGroupName(String name) {
    this.groupName = name;
    return this;
  }
  
  public PlexusIoResourceAttributes setUserId(Integer uid) {
    this.uid = uid;
    return this;
  }
  
  public PlexusIoResourceAttributes setUserName(String name) {
    this.userName = name;
    return this;
  }
  
  public PlexusIoResourceAttributes setOctalModeString(String mode) {
    setOctalMode(Integer.parseInt(mode, 8));
    return this;
  }
  
  public String toString() {
    return String.format("%nResource Attributes:%n------------------------------%nuser: %s%ngroup: %s%nuid: %d%ngid: %d%nmode: %06o", new Object[] { (this.userName == null) ? "" : this.userName, 
          (this.groupName == null) ? "" : this.groupName, 
          Integer.valueOf((this.uid != null) ? this.uid.intValue() : 0), 
          Integer.valueOf((this.gid != null) ? this.gid.intValue() : 0), 
          Integer.valueOf(this.mode) });
  }
  
  public void setSymbolicLink(boolean isSymbolicLink) {
    this.isSymbolicLink = isSymbolicLink;
  }
  
  public boolean isSymbolicLink() {
    return this.isSymbolicLink;
  }
}
