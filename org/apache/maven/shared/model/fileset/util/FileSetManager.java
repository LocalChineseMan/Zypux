package org.apache.maven.shared.model.fileset.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.mappers.FileNameMapper;
import org.apache.maven.shared.model.fileset.mappers.MapperException;
import org.apache.maven.shared.model.fileset.mappers.MapperUtil;
import org.codehaus.plexus.util.DirectoryScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSetManager {
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  
  private final boolean verbose;
  
  private final Logger logger;
  
  public FileSetManager(Logger logger, boolean verbose) {
    this.logger = Objects.<Logger>requireNonNull(logger);
    this.verbose = verbose;
  }
  
  public FileSetManager(Logger logger) {
    this(logger, false);
  }
  
  public FileSetManager() {
    this(LoggerFactory.getLogger(FileSetManager.class), false);
  }
  
  public Map<String, String> mapIncludedFiles(FileSet fileSet) throws MapperException {
    String[] sourcePaths = getIncludedFiles(fileSet);
    Map<String, String> mappedPaths = new LinkedHashMap<>();
    FileNameMapper fileMapper = MapperUtil.getFileNameMapper(fileSet.getMapper());
    for (String sourcePath : sourcePaths) {
      String destPath;
      if (fileMapper != null) {
        destPath = fileMapper.mapFileName(sourcePath);
      } else {
        destPath = sourcePath;
      } 
      mappedPaths.put(sourcePath, destPath);
    } 
    return mappedPaths;
  }
  
  public String[] getIncludedFiles(FileSet fileSet) {
    DirectoryScanner scanner = scan(fileSet);
    if (scanner != null)
      return scanner.getIncludedFiles(); 
    return EMPTY_STRING_ARRAY;
  }
  
  public String[] getIncludedDirectories(FileSet fileSet) {
    DirectoryScanner scanner = scan(fileSet);
    if (scanner != null)
      return scanner.getIncludedDirectories(); 
    return EMPTY_STRING_ARRAY;
  }
  
  public String[] getExcludedFiles(FileSet fileSet) {
    DirectoryScanner scanner = scan(fileSet);
    if (scanner != null)
      return scanner.getExcludedFiles(); 
    return EMPTY_STRING_ARRAY;
  }
  
  public String[] getExcludedDirectories(FileSet fileSet) {
    DirectoryScanner scanner = scan(fileSet);
    if (scanner != null)
      return scanner.getExcludedDirectories(); 
    return EMPTY_STRING_ARRAY;
  }
  
  public void delete(FileSet fileSet) throws IOException {
    delete(fileSet, true);
  }
  
  public void delete(FileSet fileSet, boolean throwsError) throws IOException {
    Set<String> deletablePaths = findDeletablePaths(fileSet);
    if (this.logger.isDebugEnabled()) {
      String paths = String.valueOf(deletablePaths).replace(',', '\n');
      this.logger.debug("Found deletable paths: " + paths);
    } 
    List<String> warnMessages = new LinkedList<>();
    for (String path : deletablePaths) {
      File file = new File(fileSet.getDirectory(), path);
      if (file.exists()) {
        if (file.isDirectory()) {
          if (fileSet.isFollowSymlinks() || !isSymlink(file)) {
            if (this.verbose)
              this.logger.info("Deleting directory: " + file); 
            removeDir(file, fileSet.isFollowSymlinks(), throwsError, warnMessages);
            continue;
          } 
          if (this.verbose)
            this.logger.info("Deleting symlink to directory: " + file); 
          if (!file.delete()) {
            String message = "Unable to delete symlink " + file.getAbsolutePath();
            if (throwsError)
              throw new IOException(message); 
            if (!warnMessages.contains(message))
              warnMessages.add(message); 
          } 
          continue;
        } 
        if (this.verbose)
          this.logger.info("Deleting file: " + file); 
        if (!FileUtils.deleteQuietly(file)) {
          String message = "Failed to delete file " + file.getAbsolutePath() + ". Reason is unknown.";
          if (throwsError)
            throw new IOException(message); 
          warnMessages.add(message);
        } 
      } 
    } 
    if (this.logger.isWarnEnabled() && !throwsError && warnMessages.size() > 0)
      for (String warnMessage : warnMessages)
        this.logger.warn(warnMessage);  
  }
  
  private boolean isSymlink(File file) throws IOException {
    File fileInCanonicalParent, parentDir = file.getParentFile();
    if (parentDir == null) {
      fileInCanonicalParent = file;
    } else {
      fileInCanonicalParent = new File(parentDir.getCanonicalPath(), file.getName());
    } 
    if (this.logger.isDebugEnabled())
      this.logger.debug("Checking for symlink:\nFile's canonical path: " + fileInCanonicalParent
          .getCanonicalPath() + "\nFile's absolute path with canonical parent: " + fileInCanonicalParent
          .getPath()); 
    return !fileInCanonicalParent.getCanonicalFile().equals(fileInCanonicalParent.getAbsoluteFile());
  }
  
  private Set<String> findDeletablePaths(FileSet fileSet) {
    Set<String> includes = findDeletableDirectories(fileSet);
    includes.addAll(findDeletableFiles(fileSet, includes));
    return includes;
  }
  
  private Set<String> findDeletableDirectories(FileSet fileSet) {
    if (this.verbose)
      this.logger.info("Scanning for deletable directories."); 
    DirectoryScanner scanner = scan(fileSet);
    if (scanner == null)
      return Collections.emptySet(); 
    Set<String> includes = new HashSet<>(Arrays.asList(scanner.getIncludedDirectories()));
    List<String> excludes = new ArrayList<>(Arrays.asList(scanner.getExcludedDirectories()));
    List<String> linksForDeletion = new ArrayList<>();
    if (!fileSet.isFollowSymlinks()) {
      if (this.verbose)
        this.logger.info("Adding symbolic link dirs which were previously excluded to the list being deleted."); 
      scanner.setFollowSymlinks(true);
      scanner.scan();
      if (this.logger.isDebugEnabled()) {
        this.logger.debug("Originally marked for delete: " + includes);
        this.logger.debug("Marked for preserve (with followSymlinks == false): " + excludes);
      } 
      List<String> includedDirsAndSymlinks = Arrays.asList(scanner.getIncludedDirectories());
      linksForDeletion.addAll(excludes);
      linksForDeletion.retainAll(includedDirsAndSymlinks);
      if (this.logger.isDebugEnabled())
        this.logger.debug("Symlinks marked for deletion (originally mismarked): " + linksForDeletion); 
      excludes.removeAll(includedDirsAndSymlinks);
    } 
    excludeParentDirectoriesOfExcludedPaths(excludes, includes);
    includes.addAll(linksForDeletion);
    return includes;
  }
  
  private Set<String> findDeletableFiles(FileSet fileSet, Set<String> deletableDirectories) {
    if (this.verbose)
      this.logger.info("Re-scanning for deletable files."); 
    DirectoryScanner scanner = scan(fileSet);
    if (scanner == null)
      return deletableDirectories; 
    deletableDirectories.addAll(Arrays.asList(scanner.getIncludedFiles()));
    List<String> excludes = new ArrayList<>(Arrays.asList(scanner.getExcludedFiles()));
    List<String> linksForDeletion = new ArrayList<>();
    if (!fileSet.isFollowSymlinks()) {
      if (this.verbose)
        this.logger.info("Adding symbolic link files which were previously excluded to the list being deleted."); 
      scanner.setFollowSymlinks(true);
      scanner.scan();
      if (this.logger.isDebugEnabled()) {
        this.logger.debug("Originally marked for delete: " + deletableDirectories);
        this.logger.debug("Marked for preserve (with followSymlinks == false): " + excludes);
      } 
      List<String> includedFilesAndSymlinks = Arrays.asList(scanner.getIncludedFiles());
      linksForDeletion.addAll(excludes);
      linksForDeletion.retainAll(includedFilesAndSymlinks);
      if (this.logger.isDebugEnabled())
        this.logger.debug("Symlinks marked for deletion (originally mismarked): " + linksForDeletion); 
      excludes.removeAll(includedFilesAndSymlinks);
    } 
    excludeParentDirectoriesOfExcludedPaths(excludes, deletableDirectories);
    deletableDirectories.addAll(linksForDeletion);
    return deletableDirectories;
  }
  
  private void excludeParentDirectoriesOfExcludedPaths(List<String> excludedPaths, Set<String> deletablePaths) {
    for (String path : excludedPaths) {
      String parentPath = (new File(path)).getParent();
      while (parentPath != null) {
        if (this.logger.isDebugEnabled())
          this.logger.debug("Verifying path " + parentPath + " is not present; contains file which is excluded."); 
        boolean removed = deletablePaths.remove(parentPath);
        if (removed && this.logger.isDebugEnabled())
          this.logger.debug("Path " + parentPath + " was removed from delete list."); 
        parentPath = (new File(parentPath)).getParent();
      } 
    } 
    if (!excludedPaths.isEmpty()) {
      if (this.logger.isDebugEnabled())
        this.logger.debug("Verifying path . is not present; contains file which is excluded."); 
      boolean removed = deletablePaths.remove("");
      if (removed && this.logger.isDebugEnabled())
        this.logger.debug("Path . was removed from delete list."); 
    } 
  }
  
  private void removeDir(File dir, boolean followSymlinks, boolean throwsError, List<String> warnMessages) throws IOException {
    String[] list = dir.list();
    if (list == null)
      list = new String[0]; 
    for (String s : list) {
      File f = new File(dir, s);
      if (f.isDirectory() && (followSymlinks || !isSymlink(f))) {
        removeDir(f, followSymlinks, throwsError, warnMessages);
      } else if (!FileUtils.deleteQuietly(f)) {
        String message = "Unable to delete file " + f.getAbsolutePath();
        if (throwsError)
          throw new IOException(message); 
        if (!warnMessages.contains(message))
          warnMessages.add(message); 
      } 
    } 
    if (!FileUtils.deleteQuietly(dir)) {
      String message = "Unable to delete directory " + dir.getAbsolutePath();
      if (throwsError)
        throw new IOException(message); 
      if (!warnMessages.contains(message))
        warnMessages.add(message); 
    } 
  }
  
  private DirectoryScanner scan(FileSet fileSet) {
    File basedir = new File(fileSet.getDirectory());
    if (!basedir.exists() || !basedir.isDirectory())
      return null; 
    DirectoryScanner scanner = new DirectoryScanner();
    String[] includesArray = fileSet.getIncludesArray();
    String[] excludesArray = fileSet.getExcludesArray();
    if (includesArray.length > 0)
      scanner.setIncludes(includesArray); 
    if (excludesArray.length > 0)
      scanner.setExcludes(excludesArray); 
    if (fileSet.isUseDefaultExcludes())
      scanner.addDefaultExcludes(); 
    scanner.setBasedir(basedir);
    scanner.setFollowSymlinks(fileSet.isFollowSymlinks());
    scanner.scan();
    return scanner;
  }
}
