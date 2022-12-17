package org.codehaus.plexus.archiver.util;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;

public class FilePermissionUtils {
  public static FilePermission getFilePermissionFromMode(String mode, Logger logger) {
    if (StringUtils.isBlank(mode))
      throw new IllegalArgumentException(" file mode cannot be empty"); 
    if (mode.length() != 3 && mode.length() != 4)
      throw new IllegalArgumentException(" file mode must be 3 or 4 characters"); 
    List<String> modes = new ArrayList<>(mode.length());
    for (int i = 0, size = mode.length(); i < size; i++)
      modes.add(String.valueOf(mode.charAt(i))); 
    boolean executable = false, ownerOnlyExecutable = true, ownerOnlyReadable = true, readable = false;
    boolean ownerOnlyWritable = true, writable = false;
    try {
      int userMode = Integer.parseInt(modes.get((mode.length() == 4) ? 1 : 0));
      switch (userMode) {
        case 0:
          break;
        case 1:
          executable = true;
          break;
        case 2:
          writable = true;
          break;
        case 3:
          writable = true;
          executable = true;
          break;
        case 4:
          readable = true;
          break;
        case 5:
          readable = true;
          executable = true;
          break;
        case 6:
          readable = true;
          writable = true;
          break;
        case 7:
          writable = true;
          readable = true;
          executable = true;
          break;
        default:
          logger.warn("ignore file mode " + userMode);
          break;
      } 
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(" file mode must contains only number " + mode);
    } 
    try {
      int allMode = Integer.parseInt(modes.get((mode.length() == 4) ? 3 : 2));
      switch (allMode) {
        case 0:
          return new FilePermission(executable, ownerOnlyExecutable, ownerOnlyReadable, readable, ownerOnlyWritable, writable);
        case 1:
          executable = true;
          ownerOnlyExecutable = false;
        case 2:
          writable = true;
          ownerOnlyWritable = false;
        case 3:
          writable = true;
          executable = true;
          ownerOnlyExecutable = false;
          ownerOnlyWritable = false;
        case 4:
          readable = true;
          ownerOnlyReadable = false;
        case 5:
          readable = true;
          executable = true;
          ownerOnlyReadable = false;
          ownerOnlyExecutable = false;
        case 6:
          readable = true;
          ownerOnlyReadable = false;
          writable = true;
          ownerOnlyWritable = false;
        case 7:
          writable = true;
          readable = true;
          executable = true;
          ownerOnlyReadable = false;
          ownerOnlyExecutable = false;
          ownerOnlyWritable = false;
      } 
      logger.warn("ignore file mode " + allMode);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(" file mode must contains only number " + mode);
    } 
  }
}
