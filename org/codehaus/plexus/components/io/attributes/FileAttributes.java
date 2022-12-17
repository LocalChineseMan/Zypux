package org.codehaus.plexus.components.io.attributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FileAttributes implements PlexusIoResourceAttributes {
  public static final LinkOption[] FOLLOW_LINK_OPTIONS = new LinkOption[0];
  
  public static final LinkOption[] NOFOLLOW_LINK_OPTIONS = new LinkOption[] { LinkOption.NOFOLLOW_LINKS };
  
  @Nullable
  private final Integer groupId;
  
  @Nullable
  private final String groupName;
  
  @Nullable
  private final Integer userId;
  
  private final String userName;
  
  private final boolean symbolicLink;
  
  private final boolean regularFile;
  
  private final boolean directory;
  
  private final boolean other;
  
  private final int octalMode;
  
  private final Set<PosixFilePermission> permissions;
  
  private final long size;
  
  private final FileTime lastModifiedTime;
  
  @Deprecated
  public FileAttributes(@Nonnull File file, @Nonnull Map<Integer, String> userCache, @Nonnull Map<Integer, String> groupCache) throws IOException {
    this(file);
  }
  
  public FileAttributes(@Nonnull File file) throws IOException {
    this(file, false);
  }
  
  public FileAttributes(@Nonnull File file, boolean followLinks) throws IOException {
    String names;
    LinkOption[] options = followLinks ? FOLLOW_LINK_OPTIONS : NOFOLLOW_LINK_OPTIONS;
    Path path = file.toPath();
    Set<String> views = path.getFileSystem().supportedFileAttributeViews();
    if (views.contains("unix")) {
      names = "unix:*";
    } else if (views.contains("posix")) {
      names = "posix:*";
    } else {
      names = "basic:*";
    } 
    Map<String, Object> attrs = Files.readAttributes(path, names, options);
    if (!attrs.containsKey("group") && !attrs.containsKey("owner") && views.contains("owner")) {
      Map<String, Object> ownerAttrs = Files.readAttributes(path, "owner:*", options);
      Map<String, Object> newAttrs = new HashMap<>(attrs);
      newAttrs.putAll(ownerAttrs);
      attrs = newAttrs;
    } 
    this.groupId = (Integer)attrs.get("gid");
    this.groupName = attrs.containsKey("group") ? ((Principal)attrs.get("group")).getName() : null;
    this.userId = (Integer)attrs.get("uid");
    this.userName = attrs.containsKey("owner") ? ((Principal)attrs.get("owner")).getName() : null;
    this.symbolicLink = ((Boolean)attrs.get("isSymbolicLink")).booleanValue();
    this.regularFile = ((Boolean)attrs.get("isRegularFile")).booleanValue();
    this.directory = ((Boolean)attrs.get("isDirectory")).booleanValue();
    this.other = ((Boolean)attrs.get("isOther")).booleanValue();
    this.octalMode = attrs.containsKey("mode") ? (((Integer)attrs.get("mode")).intValue() & 0xFFF) : -1;
    this.permissions = attrs.containsKey("permissions") ? (Set<PosixFilePermission>)attrs.get("permissions") : Collections.<PosixFilePermission>emptySet();
    this.size = ((Long)attrs.get("size")).longValue();
    this.lastModifiedTime = (FileTime)attrs.get("lastModifiedTime");
  }
  
  public FileAttributes(@Nullable Integer userId, String userName, @Nullable Integer groupId, @Nullable String groupName, int octalMode, boolean symbolicLink, boolean regularFile, boolean directory, boolean other, Set<PosixFilePermission> permissions, long size, FileTime lastModifiedTime) {
    this.userId = userId;
    this.userName = userName;
    this.groupId = groupId;
    this.groupName = groupName;
    this.octalMode = octalMode;
    this.symbolicLink = symbolicLink;
    this.regularFile = regularFile;
    this.directory = directory;
    this.other = other;
    this.permissions = permissions;
    this.size = size;
    this.lastModifiedTime = lastModifiedTime;
  }
  
  @Nonnull
  public static PlexusIoResourceAttributes uncached(@Nonnull File file) throws IOException {
    return new FileAttributes(file);
  }
  
  @Nullable
  public Integer getGroupId() {
    return this.groupId;
  }
  
  public boolean hasGroupId() {
    return false;
  }
  
  public boolean hasUserId() {
    return false;
  }
  
  @Nullable
  public String getGroupName() {
    return this.groupName;
  }
  
  public Integer getUserId() {
    return this.userId;
  }
  
  public String getUserName() {
    return this.userName;
  }
  
  public boolean isGroupExecutable() {
    return containsPermission(PosixFilePermission.GROUP_EXECUTE);
  }
  
  private boolean containsPermission(PosixFilePermission groupExecute) {
    return this.permissions.contains(groupExecute);
  }
  
  public boolean isGroupReadable() {
    return containsPermission(PosixFilePermission.GROUP_READ);
  }
  
  public boolean isGroupWritable() {
    return containsPermission(PosixFilePermission.GROUP_WRITE);
  }
  
  public boolean isOwnerExecutable() {
    return containsPermission(PosixFilePermission.OWNER_EXECUTE);
  }
  
  public boolean isOwnerReadable() {
    return containsPermission(PosixFilePermission.OWNER_READ);
  }
  
  public boolean isOwnerWritable() {
    return containsPermission(PosixFilePermission.OWNER_WRITE);
  }
  
  public boolean isWorldExecutable() {
    return containsPermission(PosixFilePermission.OTHERS_EXECUTE);
  }
  
  public boolean isWorldReadable() {
    return containsPermission(PosixFilePermission.OTHERS_READ);
  }
  
  public boolean isWorldWritable() {
    return containsPermission(PosixFilePermission.OTHERS_WRITE);
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(System.lineSeparator());
    sb.append("File Attributes:");
    sb.append(System.lineSeparator());
    sb.append("------------------------------");
    sb.append(System.lineSeparator());
    sb.append("user: ");
    sb.append((this.userName == null) ? "" : this.userName);
    sb.append(System.lineSeparator());
    sb.append("group: ");
    sb.append((this.groupName == null) ? "" : this.groupName);
    sb.append(System.lineSeparator());
    sb.append("uid: ");
    sb.append(hasUserId() ? Integer.toString(this.userId.intValue()) : "");
    sb.append(System.lineSeparator());
    sb.append("gid: ");
    sb.append(hasGroupId() ? Integer.toString(this.groupId.intValue()) : "");
    return sb.toString();
  }
  
  public int getOctalMode() {
    return this.octalMode;
  }
  
  public int calculatePosixOctalMode() {
    int result = 0;
    if (isOwnerReadable())
      result |= 0x100; 
    if (isOwnerWritable())
      result |= 0x80; 
    if (isOwnerExecutable())
      result |= 0x40; 
    if (isGroupReadable())
      result |= 0x20; 
    if (isGroupWritable())
      result |= 0x10; 
    if (isGroupExecutable())
      result |= 0x8; 
    if (isWorldReadable())
      result |= 0x4; 
    if (isWorldWritable())
      result |= 0x2; 
    if (isWorldExecutable())
      result |= 0x1; 
    return result;
  }
  
  public String getOctalModeString() {
    return Integer.toString(getOctalMode(), 8);
  }
  
  public boolean isSymbolicLink() {
    return this.symbolicLink;
  }
  
  public boolean isRegularFile() {
    return this.regularFile;
  }
  
  public boolean isDirectory() {
    return this.directory;
  }
  
  public boolean isOther() {
    return this.other;
  }
  
  public long getSize() {
    return this.size;
  }
  
  public FileTime getLastModifiedTime() {
    return this.lastModifiedTime;
  }
  
  protected Set<PosixFilePermission> getPermissions() {
    return this.permissions;
  }
}
