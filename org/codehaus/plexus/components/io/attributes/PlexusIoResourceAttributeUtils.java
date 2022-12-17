package org.codehaus.plexus.components.io.attributes;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.codehaus.plexus.util.FileUtils;

public final class PlexusIoResourceAttributeUtils {
  public static PlexusIoResourceAttributes mergeAttributes(PlexusIoResourceAttributes override, PlexusIoResourceAttributes base, PlexusIoResourceAttributes def) {
    if (override == null)
      return base; 
    if (base == null)
      return new SimpleResourceAttributes((
          override.getUserId() != null && override.getUserId().intValue() != -1) ? 
          override.getUserId() : (
          (def != null && def.getUserId() != null && def.getUserId().intValue() != -1) ? 
          def.getUserId() : 
          null), 
          (override.getUserName() != null) ? 
          override.getUserName() : (
          (def != null) ? 
          def.getUserName() : 
          null), 
          (override.getGroupId() != null && override.getGroupId().intValue() != -1) ? 
          override.getGroupId() : (
          (def != null && def.getGroupId() != null && def.getGroupId().intValue() != -1) ? 
          def.getGroupId() : 
          null), 
          (override.getGroupName() != null) ? 
          override.getGroupName() : (
          (def != null) ? 
          def.getGroupName() : 
          null), override
          .getOctalMode()); 
    Integer uid = (override.getUserId() != null && override.getUserId().intValue() != -1) ? override.getUserId() : ((base.getUserId() != null && base.getUserId().intValue() != -1) ? base.getUserId() : ((def.getUserId() != null && def.getUserId().intValue() != -1) ? def.getUserId() : null));
    String uname = (override.getUserName() != null) ? override.getUserName() : ((base.getUserName() != null) ? base.getUserName() : def.getUserName());
    Integer gid = (override.getGroupId() != null && override.getGroupId().intValue() != -1) ? override.getGroupId() : ((base.getGroupId() != null && base.getGroupId().intValue() != -1) ? base.getGroupId() : ((def.getGroupId() != null && def.getGroupId().intValue() != -1) ? def.getGroupId() : null));
    String gname = (override.getGroupName() != null) ? override.getGroupName() : ((base.getGroupName() != null) ? base.getGroupName() : def.getGroupName());
    int mode = (override.getOctalMode() > 0) ? override.getOctalMode() : ((base.getOctalMode() >= 0) ? base.getOctalMode() : def.getOctalMode());
    if (base instanceof FileAttributes)
      return new UserGroupModeFileAttributes(uid, uname, gid, gname, mode, (FileAttributes)base); 
    return new SimpleResourceAttributes(uid, uname, gid, gname, mode, base.isSymbolicLink());
  }
  
  public static boolean isGroupExecutableInOctal(int mode) {
    return isOctalModeEnabled(mode, 8);
  }
  
  public static boolean isGroupReadableInOctal(int mode) {
    return isOctalModeEnabled(mode, 32);
  }
  
  public static boolean isGroupWritableInOctal(int mode) {
    return isOctalModeEnabled(mode, 16);
  }
  
  public static boolean isOwnerExecutableInOctal(int mode) {
    return isOctalModeEnabled(mode, 64);
  }
  
  public static boolean isOwnerReadableInOctal(int mode) {
    return isOctalModeEnabled(mode, 256);
  }
  
  public static boolean isOwnerWritableInOctal(int mode) {
    return isOctalModeEnabled(mode, 128);
  }
  
  public static boolean isWorldExecutableInOctal(int mode) {
    return isOctalModeEnabled(mode, 1);
  }
  
  public static boolean isWorldReadableInOctal(int mode) {
    return isOctalModeEnabled(mode, 4);
  }
  
  public static boolean isWorldWritableInOctal(int mode) {
    return isOctalModeEnabled(mode, 2);
  }
  
  public static boolean isOctalModeEnabled(int mode, int targetMode) {
    return ((mode & targetMode) != 0);
  }
  
  public static PlexusIoResourceAttributes getFileAttributes(File file) throws IOException {
    return getFileAttributes(file, false);
  }
  
  public static PlexusIoResourceAttributes getFileAttributes(File file, boolean followLinks) throws IOException {
    Map<String, PlexusIoResourceAttributes> byPath = getFileAttributesByPath(file, false, followLinks);
    PlexusIoResourceAttributes o = byPath.get(file.getAbsolutePath());
    if (o == null)
      return SimpleResourceAttributes.lastResortDummyAttributesForBrokenOS(); 
    return o;
  }
  
  public static Map<String, PlexusIoResourceAttributes> getFileAttributesByPath(File dir) throws IOException {
    return getFileAttributesByPath(dir, true);
  }
  
  @Nonnull
  public static Map<String, PlexusIoResourceAttributes> getFileAttributesByPath(@Nonnull File dir, boolean recursive) throws IOException {
    return getFileAttributesByPath(dir, recursive, false);
  }
  
  @Nonnull
  public static Map<String, PlexusIoResourceAttributes> getFileAttributesByPath(@Nonnull File dir, boolean recursive, boolean followLinks) throws IOException {
    List<String> fileAndDirectoryNames;
    if (recursive && dir.isDirectory()) {
      fileAndDirectoryNames = FileUtils.getFileAndDirectoryNames(dir, null, null, true, true, true, true);
    } else {
      fileAndDirectoryNames = Collections.singletonList(dir.getAbsolutePath());
    } 
    Map<String, PlexusIoResourceAttributes> attributesByPath = new LinkedHashMap<>();
    for (String fileAndDirectoryName : fileAndDirectoryNames)
      attributesByPath.put(fileAndDirectoryName, new FileAttributes(new File(fileAndDirectoryName), followLinks)); 
    return attributesByPath;
  }
}
