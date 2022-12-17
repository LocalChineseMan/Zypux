package org.codehaus.plexus.components.io.attributes;

public class UserGroupModeFileAttributes extends FileAttributes {
  public UserGroupModeFileAttributes(Integer uid, String userName, Integer gid, String groupName, int mode, FileAttributes base) {
    super(uid, userName, gid, groupName, mode, base
        .isSymbolicLink(), base.isRegularFile(), base.isDirectory(), base.isOther(), base
        .getPermissions(), base.getSize(), base.getLastModifiedTime());
  }
  
  public String toString() {
    return String.format("%nResource Attributes:%n------------------------------%nuser: %s%ngroup: %s%nuid: %d%ngid: %d%nmode: %06o", new Object[] { (getUserName() == null) ? "" : getUserName(), 
          (getGroupName() == null) ? "" : getGroupName(), 
          Integer.valueOf((getUserId() != null) ? getUserId().intValue() : 0), 
          Integer.valueOf((getGroupId() != null) ? getGroupId().intValue() : 0), 
          Integer.valueOf(getOctalMode()) });
  }
}
