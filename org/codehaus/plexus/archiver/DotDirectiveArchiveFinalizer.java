package org.codehaus.plexus.archiver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import org.codehaus.plexus.archiver.util.DefaultFileSet;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

public class DotDirectiveArchiveFinalizer extends AbstractArchiveFinalizer {
  private static final String DEFAULT_DOT_FILE_PREFIX = ".plxarc";
  
  private final File dotFileDirectory;
  
  private final String dotFilePrefix;
  
  public DotDirectiveArchiveFinalizer(File dotFileDirectory) {
    this(dotFileDirectory, ".plxarc");
  }
  
  public DotDirectiveArchiveFinalizer(File dotFileDirectory, String dotFilePrefix) {
    this.dotFileDirectory = dotFileDirectory;
    this.dotFilePrefix = dotFilePrefix;
  }
  
  public void finalizeArchiveCreation(Archiver archiver) throws ArchiverException {
    try {
      List<File> dotFiles = FileUtils.getFiles(this.dotFileDirectory, this.dotFilePrefix + "*", null);
      for (File dotFile : dotFiles) {
        BufferedReader in = Files.newBufferedReader(dotFile.toPath(), StandardCharsets.UTF_8);
        try {
          for (String line = in.readLine(); line != null; line = in.readLine()) {
            String[] s = StringUtils.split(line, ":");
            if (s.length == 1) {
              File directory = new File(this.dotFileDirectory, s[0]);
              System.out.println("adding directory = " + directory);
              archiver.addFileSet((FileSet)new DefaultFileSet(directory));
            } else {
              File directory = new File(this.dotFileDirectory, s[0]);
              System.out.println("adding directory = " + directory + " to: " + s[1]);
              if (s[1].endsWith("/")) {
                archiver.addFileSet((FileSet)(new DefaultFileSet(directory)).prefixed(s[1]));
              } else {
                archiver.addFileSet((FileSet)(new DefaultFileSet(directory)).prefixed(s[1] + "/"));
              } 
            } 
          } 
          if (in != null)
            in.close(); 
        } catch (Throwable throwable) {
          if (in != null)
            try {
              in.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
      } 
    } catch (IOException e) {
      throw new ArchiverException("Error processing dot files.", e);
    } 
  }
  
  public List getVirtualFiles() {
    return Collections.EMPTY_LIST;
  }
}
