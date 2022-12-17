package org.codehaus.plexus.components.io.attributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import javax.annotation.Nonnull;

public class SymlinkUtils {
  @Nonnull
  public static File readSymbolicLink(@Nonnull File symlink) throws IOException {
    Path path = Files.readSymbolicLink(symlink.toPath());
    return path.toFile();
  }
  
  @Nonnull
  public static File createSymbolicLink(@Nonnull File symlink, File target) throws IOException {
    Path link = symlink.toPath();
    if (!Files.exists(link, new LinkOption[] { LinkOption.NOFOLLOW_LINKS }))
      link = Files.createSymbolicLink(link, target.toPath(), (FileAttribute<?>[])new FileAttribute[0]); 
    return link.toFile();
  }
}
