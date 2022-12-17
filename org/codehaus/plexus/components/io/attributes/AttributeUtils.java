package org.codehaus.plexus.components.io.attributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AttributeUtils {
  public static long getLastModified(@Nonnull File file) {
    try {
      BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class, new LinkOption[0]);
      return basicFileAttributes.lastModifiedTime().toMillis();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } 
  }
  
  public static void chmod(@Nonnull File file, int mode) throws IOException {
    Path path = file.toPath();
    if (!Files.isSymbolicLink(path))
      Files.setPosixFilePermissions(path, getPermissions(mode)); 
  }
  
  @Nonnull
  public static Set<PosixFilePermission> getPermissions(int mode) {
    Set<PosixFilePermission> perms = new HashSet<>();
    if ((mode & 0x100) > 0)
      perms.add(PosixFilePermission.OWNER_READ); 
    if ((mode & 0x80) > 0)
      perms.add(PosixFilePermission.OWNER_WRITE); 
    if ((mode & 0x40) > 0)
      perms.add(PosixFilePermission.OWNER_EXECUTE); 
    if ((mode & 0x20) > 0)
      perms.add(PosixFilePermission.GROUP_READ); 
    if ((mode & 0x10) > 0)
      perms.add(PosixFilePermission.GROUP_WRITE); 
    if ((mode & 0x8) > 0)
      perms.add(PosixFilePermission.GROUP_EXECUTE); 
    if ((mode & 0x4) > 0)
      perms.add(PosixFilePermission.OTHERS_READ); 
    if ((mode & 0x2) > 0)
      perms.add(PosixFilePermission.OTHERS_WRITE); 
    if ((mode & 0x1) > 0)
      perms.add(PosixFilePermission.OTHERS_EXECUTE); 
    return perms;
  }
  
  @Nonnull
  public static PosixFileAttributes getPosixFileAttributes(@Nonnull File file) throws IOException {
    return Files.<PosixFileAttributes>readAttributes(file.toPath(), PosixFileAttributes.class, new LinkOption[] { LinkOption.NOFOLLOW_LINKS });
  }
  
  @Nonnull
  public static BasicFileAttributes getFileAttributes(@Nonnull File file) throws IOException {
    return getFileAttributes(file.toPath());
  }
  
  public static BasicFileAttributes getFileAttributes(Path path) throws IOException {
    if (isUnix(path))
      try {
        return Files.readAttributes(path, (Class)PosixFileAttributes.class, new LinkOption[] { LinkOption.NOFOLLOW_LINKS });
      } catch (UnsupportedOperationException unsupportedOperationException) {} 
    return Files.readAttributes(path, BasicFileAttributes.class, new LinkOption[] { LinkOption.NOFOLLOW_LINKS });
  }
  
  public static boolean isUnix(Path path) {
    return path.getFileSystem().supportedFileAttributeViews().contains("unix");
  }
  
  @Nullable
  public static FileOwnerAttributeView getFileOwnershipInfo(@Nonnull File file) throws IOException {
    try {
      return Files.<FileOwnerAttributeView>getFileAttributeView(file.toPath(), FileOwnerAttributeView.class, new LinkOption[] { LinkOption.NOFOLLOW_LINKS });
    } catch (UnsupportedOperationException e) {
      return null;
    } 
  }
}
