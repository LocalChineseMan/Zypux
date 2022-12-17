package org.codehaus.plexus.util;

import java.io.File;
import java.util.StringTokenizer;

public class PathTool {
  public static final java.lang.String getRelativePath(java.lang.String basedir, java.lang.String filename) {
    basedir = uppercaseDrive(basedir);
    filename = uppercaseDrive(filename);
    if (basedir == null || basedir.length() == 0 || filename == null || filename.length() == 0 || 
      !filename.startsWith(basedir))
      return ""; 
    java.lang.String separator = determineSeparator(filename);
    basedir = StringUtils.chompLast(basedir, separator);
    filename = StringUtils.chompLast(filename, separator);
    java.lang.String relativeFilename = filename.substring(basedir.length());
    return determineRelativePath(relativeFilename, separator);
  }
  
  public static final java.lang.String getRelativePath(java.lang.String filename) {
    filename = uppercaseDrive(filename);
    if (filename == null || filename.length() == 0)
      return ""; 
    java.lang.String separator = determineSeparator(filename);
    filename = StringUtils.chompLast(filename, separator);
    if (!filename.startsWith(separator))
      filename = separator + filename; 
    return determineRelativePath(filename, separator);
  }
  
  public static final java.lang.String getDirectoryComponent(java.lang.String filename) {
    if (filename == null || filename.length() == 0)
      return ""; 
    java.lang.String separator = determineSeparator(filename);
    java.lang.String directory = StringUtils.chomp(filename, separator);
    if (filename.equals(directory))
      return "."; 
    return directory;
  }
  
  public static final java.lang.String calculateLink(java.lang.String link, java.lang.String relativePath) {
    if (link == null)
      link = ""; 
    if (relativePath == null)
      relativePath = ""; 
    if (link.startsWith("/site/"))
      return link.substring(5); 
    if (link.startsWith("/absolute/"))
      return link.substring(10); 
    if (link.contains(":"))
      return link; 
    if (StringUtils.equals(relativePath, ".")) {
      if (link.startsWith("/"))
        return link.substring(1); 
      return link;
    } 
    if (relativePath.endsWith("/") && link.startsWith("/"))
      return relativePath + "." + link.substring(1); 
    if (relativePath.endsWith("/") || link.startsWith("/"))
      return relativePath + link; 
    return relativePath + "/" + link;
  }
  
  public static final java.lang.String getRelativeWebPath(java.lang.String oldPath, java.lang.String newPath) {
    if (StringUtils.isEmpty(oldPath) || StringUtils.isEmpty(newPath))
      return ""; 
    java.lang.String resultPath = buildRelativePath(newPath, oldPath, '/');
    if (newPath.endsWith("/") && !resultPath.endsWith("/"))
      return resultPath + "/"; 
    return resultPath;
  }
  
  public static final java.lang.String getRelativeFilePath(java.lang.String oldPath, java.lang.String newPath) {
    if (StringUtils.isEmpty(oldPath) || StringUtils.isEmpty(newPath))
      return ""; 
    java.lang.String fromPath = (new File(oldPath)).getPath();
    java.lang.String toPath = (new File(newPath)).getPath();
    if (toPath.matches("^\\[a-zA-Z]:"))
      toPath = toPath.substring(1); 
    if (fromPath.matches("^\\[a-zA-Z]:"))
      fromPath = fromPath.substring(1); 
    if (fromPath.startsWith(":", 1))
      fromPath = Character.toLowerCase(fromPath.charAt(0)) + fromPath.substring(1); 
    if (toPath.startsWith(":", 1))
      toPath = Character.toLowerCase(toPath.charAt(0)) + toPath.substring(1); 
    if (toPath.startsWith(":", 1) && fromPath.startsWith(":", 1) && 
      !toPath.substring(0, 1).equals(fromPath.substring(0, 1)))
      return null; 
    if ((toPath.startsWith(":", 1) && !fromPath.startsWith(":", 1)) || (
      !toPath.startsWith(":", 1) && fromPath.startsWith(":", 1)))
      return null; 
    java.lang.String resultPath = buildRelativePath(toPath, fromPath, File.separatorChar);
    if (newPath.endsWith(File.separator) && !resultPath.endsWith(File.separator))
      return resultPath + File.separator; 
    return resultPath;
  }
  
  private static final java.lang.String determineRelativePath(java.lang.String filename, java.lang.String separator) {
    if (filename.length() == 0)
      return ""; 
    int slashCount = StringUtils.countMatches(filename, separator) - 1;
    if (slashCount <= 0)
      return "."; 
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < slashCount; i++)
      sb.append("../"); 
    return StringUtils.chop(sb.toString());
  }
  
  private static final java.lang.String determineSeparator(java.lang.String filename) {
    int forwardCount = StringUtils.countMatches(filename, "/");
    int backwardCount = StringUtils.countMatches(filename, "\\");
    return (forwardCount >= backwardCount) ? "/" : "\\";
  }
  
  static final java.lang.String uppercaseDrive(java.lang.String path) {
    if (path == null)
      return null; 
    if (path.length() >= 2 && path.charAt(1) == ':')
      path = Character.toUpperCase(path.charAt(0)) + path.substring(1); 
    return path;
  }
  
  private static final java.lang.String buildRelativePath(java.lang.String toPath, java.lang.String fromPath, char separatorChar) {
    StringTokenizer toTokeniser = new StringTokenizer(toPath, java.lang.String.valueOf(separatorChar));
    StringTokenizer fromTokeniser = new StringTokenizer(fromPath, java.lang.String.valueOf(separatorChar));
    int count = 0;
    while (toTokeniser.hasMoreTokens() && fromTokeniser.hasMoreTokens()) {
      if (separatorChar == '\\') {
        if (!fromTokeniser.nextToken().equalsIgnoreCase(toTokeniser.nextToken()))
          break; 
      } else if (!fromTokeniser.nextToken().equals(toTokeniser.nextToken())) {
        break;
      } 
      count++;
    } 
    toTokeniser = new StringTokenizer(toPath, java.lang.String.valueOf(separatorChar));
    fromTokeniser = new StringTokenizer(fromPath, java.lang.String.valueOf(separatorChar));
    while (count-- > 0) {
      fromTokeniser.nextToken();
      toTokeniser.nextToken();
    } 
    java.lang.String relativePath = "";
    while (fromTokeniser.hasMoreTokens()) {
      fromTokeniser.nextToken();
      relativePath = relativePath + "..";
      if (fromTokeniser.hasMoreTokens())
        relativePath = relativePath + separatorChar; 
    } 
    if (relativePath.length() != 0 && toTokeniser.hasMoreTokens())
      relativePath = relativePath + separatorChar; 
    while (toTokeniser.hasMoreTokens()) {
      relativePath = relativePath + toTokeniser.nextToken();
      if (toTokeniser.hasMoreTokens())
        relativePath = relativePath + separatorChar; 
    } 
    return relativePath;
  }
}
