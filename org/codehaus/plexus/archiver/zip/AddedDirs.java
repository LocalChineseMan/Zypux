package org.codehaus.plexus.archiver.zip;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class AddedDirs {
  private final Set<String> addedDirs = new HashSet<>();
  
  @Deprecated
  public Stack<String> asStringStack(String entry) {
    Stack<String> directories = new Stack<>();
    int slashPos = entry.length() - (entry.endsWith("/") ? 1 : 0);
    while ((slashPos = entry.lastIndexOf('/', slashPos - 1)) != -1) {
      String dir = entry.substring(0, slashPos + 1);
      if (this.addedDirs.contains(dir))
        break; 
      directories.push(dir);
    } 
    return directories;
  }
  
  public Deque<String> asStringDeque(String entry) {
    Deque<String> directories = new ArrayDeque<>();
    int slashPos = entry.length() - (entry.endsWith("/") ? 1 : 0);
    while ((slashPos = entry.lastIndexOf('/', slashPos - 1)) != -1) {
      String dir = entry.substring(0, slashPos + 1);
      if (this.addedDirs.contains(dir))
        break; 
      directories.push(dir);
    } 
    return directories;
  }
  
  public void clear() {
    this.addedDirs.clear();
  }
  
  public boolean update(String vPath) {
    return !this.addedDirs.add(vPath);
  }
  
  public Set<String> allAddedDirs() {
    return new HashSet<>(this.addedDirs);
  }
}
