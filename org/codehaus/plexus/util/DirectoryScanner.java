package org.codehaus.plexus.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class DirectoryScanner extends AbstractScanner {
  private static final java.lang.String[] EMPTY_STRING_ARRAY = new java.lang.String[0];
  
  protected File basedir;
  
  protected ArrayList<java.lang.String> filesIncluded;
  
  protected ArrayList<java.lang.String> filesNotIncluded;
  
  protected ArrayList<java.lang.String> filesExcluded;
  
  protected ArrayList<java.lang.String> dirsIncluded;
  
  protected ArrayList<java.lang.String> dirsNotIncluded;
  
  protected ArrayList<java.lang.String> dirsExcluded;
  
  protected ArrayList<java.lang.String> filesDeselected;
  
  protected ArrayList<java.lang.String> dirsDeselected;
  
  protected boolean haveSlowResults = false;
  
  private boolean followSymlinks = true;
  
  protected boolean everythingIncluded = true;
  
  private final char[][] tokenizedEmpty = MatchPattern.tokenizePathToCharArray("", File.separator);
  
  public void setBasedir(java.lang.String basedir) {
    setBasedir(new File(basedir.replace('/', File.separatorChar).replace('\\', File.separatorChar)));
  }
  
  public void setBasedir(File basedir) {
    this.basedir = basedir;
  }
  
  public File getBasedir() {
    return this.basedir;
  }
  
  public void setFollowSymlinks(boolean followSymlinks) {
    this.followSymlinks = followSymlinks;
  }
  
  public boolean isEverythingIncluded() {
    return this.everythingIncluded;
  }
  
  public void scan() throws IllegalStateException {
    if (this.basedir == null)
      throw new IllegalStateException("No basedir set"); 
    if (!this.basedir.exists())
      throw new IllegalStateException("basedir " + this.basedir + " does not exist"); 
    if (!this.basedir.isDirectory())
      throw new IllegalStateException("basedir " + this.basedir + " is not a directory"); 
    setupDefaultFilters();
    setupMatchPatterns();
    this.filesIncluded = new ArrayList<>();
    this.filesNotIncluded = new ArrayList<>();
    this.filesExcluded = new ArrayList<>();
    this.filesDeselected = new ArrayList<>();
    this.dirsIncluded = new ArrayList<>();
    this.dirsNotIncluded = new ArrayList<>();
    this.dirsExcluded = new ArrayList<>();
    this.dirsDeselected = new ArrayList<>();
    if (isIncluded("", this.tokenizedEmpty)) {
      if (!isExcluded("", this.tokenizedEmpty)) {
        if (isSelected("", this.basedir)) {
          this.dirsIncluded.add("");
        } else {
          this.dirsDeselected.add("");
        } 
      } else {
        this.dirsExcluded.add("");
      } 
    } else {
      this.dirsNotIncluded.add("");
    } 
    scandir(this.basedir, "", true);
  }
  
  protected void slowScan() {
    if (this.haveSlowResults)
      return; 
    java.lang.String[] excl = this.dirsExcluded.<java.lang.String>toArray(EMPTY_STRING_ARRAY);
    java.lang.String[] notIncl = this.dirsNotIncluded.<java.lang.String>toArray(EMPTY_STRING_ARRAY);
    for (java.lang.String anExcl : excl) {
      if (!couldHoldIncluded(anExcl))
        scandir(new File(this.basedir, anExcl), anExcl + File.separator, false); 
    } 
    for (java.lang.String aNotIncl : notIncl) {
      if (!couldHoldIncluded(aNotIncl))
        scandir(new File(this.basedir, aNotIncl), aNotIncl + File.separator, false); 
    } 
    this.haveSlowResults = true;
  }
  
  protected void scandir(File dir, java.lang.String vpath, boolean fast) {
    java.lang.String[] newfiles = dir.list();
    if (newfiles == null)
      newfiles = EMPTY_STRING_ARRAY; 
    if (!this.followSymlinks)
      try {
        if (isParentSymbolicLink(dir, (java.lang.String)null)) {
          for (java.lang.String newfile : newfiles) {
            java.lang.String name = vpath + newfile;
            File file = new File(dir, newfile);
            if (file.isDirectory()) {
              this.dirsExcluded.add(name);
            } else {
              this.filesExcluded.add(name);
            } 
          } 
          return;
        } 
      } catch (IOException ioe) {
        java.lang.String msg = "IOException caught while checking for links!";
        System.err.println(msg);
      }  
    if (this.filenameComparator != null)
      Arrays.sort(newfiles, this.filenameComparator); 
    for (java.lang.String newfile : newfiles) {
      java.lang.String name = vpath + newfile;
      char[][] tokenizedName = MatchPattern.tokenizePathToCharArray(name, File.separator);
      File file = new File(dir, newfile);
      if (file.isDirectory()) {
        if (isIncluded(name, tokenizedName)) {
          if (!isExcluded(name, tokenizedName)) {
            if (isSelected(name, file)) {
              this.dirsIncluded.add(name);
              if (fast)
                scandir(file, name + File.separator, fast); 
            } else {
              this.everythingIncluded = false;
              this.dirsDeselected.add(name);
              if (fast && couldHoldIncluded(name))
                scandir(file, name + File.separator, fast); 
            } 
          } else {
            this.everythingIncluded = false;
            this.dirsExcluded.add(name);
            if (fast && couldHoldIncluded(name))
              scandir(file, name + File.separator, fast); 
          } 
        } else {
          this.everythingIncluded = false;
          this.dirsNotIncluded.add(name);
          if (fast && couldHoldIncluded(name))
            scandir(file, name + File.separator, fast); 
        } 
        if (!fast)
          scandir(file, name + File.separator, fast); 
      } else if (file.isFile()) {
        if (isIncluded(name, tokenizedName)) {
          if (!isExcluded(name, tokenizedName)) {
            if (isSelected(name, file)) {
              this.filesIncluded.add(name);
            } else {
              this.everythingIncluded = false;
              this.filesDeselected.add(name);
            } 
          } else {
            this.everythingIncluded = false;
            this.filesExcluded.add(name);
          } 
        } else {
          this.everythingIncluded = false;
          this.filesNotIncluded.add(name);
        } 
      } 
    } 
  }
  
  protected boolean isSelected(java.lang.String name, File file) {
    return true;
  }
  
  public java.lang.String[] getIncludedFiles() {
    return this.filesIncluded.<java.lang.String>toArray(EMPTY_STRING_ARRAY);
  }
  
  public java.lang.String[] getNotIncludedFiles() {
    slowScan();
    return this.filesNotIncluded.<java.lang.String>toArray(EMPTY_STRING_ARRAY);
  }
  
  public java.lang.String[] getExcludedFiles() {
    slowScan();
    return this.filesExcluded.<java.lang.String>toArray(EMPTY_STRING_ARRAY);
  }
  
  public java.lang.String[] getDeselectedFiles() {
    slowScan();
    return this.filesDeselected.<java.lang.String>toArray(EMPTY_STRING_ARRAY);
  }
  
  public java.lang.String[] getIncludedDirectories() {
    return this.dirsIncluded.<java.lang.String>toArray(EMPTY_STRING_ARRAY);
  }
  
  public java.lang.String[] getNotIncludedDirectories() {
    slowScan();
    return this.dirsNotIncluded.<java.lang.String>toArray(EMPTY_STRING_ARRAY);
  }
  
  public java.lang.String[] getExcludedDirectories() {
    slowScan();
    return this.dirsExcluded.<java.lang.String>toArray(EMPTY_STRING_ARRAY);
  }
  
  public java.lang.String[] getDeselectedDirectories() {
    slowScan();
    return this.dirsDeselected.<java.lang.String>toArray(EMPTY_STRING_ARRAY);
  }
  
  public boolean isSymbolicLink(File parent, java.lang.String name) throws IOException {
    return NioFiles.isSymbolicLink(new File(parent, name));
  }
  
  public boolean isParentSymbolicLink(File parent, java.lang.String name) throws IOException {
    return NioFiles.isSymbolicLink(parent);
  }
}
